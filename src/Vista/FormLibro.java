package Vista;

import DAO.*;
import Modelo.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class FormLibro extends JDialog {
    private JPanel contentPane;
    private JTextField txtTitulo;
    private JTextField txtIsbn;
    private JTextField txtAnio;
    private JTextField txtUrlPortada;
    private JSpinner spinnerTotal;
    private JSpinner spinnerDisponible;
    private JTextField txtIdEditorial;
    private JTextField txtIdCategoria;
    private JTextField txtIdAutores;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private JLabel lblTituloFormulario;
    private JComboBox cmbEditorial;
    private JComboBox cmbCategoria;
    private JList listAutores;

    private LibroDAO libroDAO;
    private Libro libroParaEditar; // Null si es un libro nuevo
    private PanelGestionLibros panelPadre; // Para poder refrescar la lista de libros

    // Constructor para un libro nuevo
    public FormLibro(JFrame parent, PanelGestionLibros panelPadre, Libro libro) {
        super(parent, true);
        this.panelPadre = panelPadre;
        this.libroDAO = new LibroDAO();
        this.libroParaEditar = libro;

        configurarVentana();
        cargarDatosParaCombosYLista();

        if (libro == null) {
            lblTituloFormulario.setText("Agregar Nuevo Libro");
        } else {
            lblTituloFormulario.setText("Editar Libro");
            llenarFormulario();
        }
    }

    private void configurarVentana() {
        setContentPane(contentPane);
        setTitle("Gestión de Libro");
        pack();
        setLocationRelativeTo(getParent());

        spinnerTotal.setModel(new SpinnerNumberModel(1, 1, 1000, 1));
        spinnerDisponible.setModel(new SpinnerNumberModel(1, 0, 1000, 1));
        listAutores.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        btnGuardar.addActionListener(e -> guardarLibro());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void cargarDatosParaCombosYLista() {
        // Cargar Categorías
        List<Categoria> categorias = new CategoriaDAO().obtenerTodas();
        cmbCategoria.setModel(new DefaultComboBoxModel<>(new Vector<>(categorias)));

        // Cargar Editoriales
        List<Editorial> editoriales = new EditorialDAO().obtenerTodas();
        cmbEditorial.setModel(new DefaultComboBoxModel<>(new Vector<>(editoriales)));

        // Cargar Autores
        List<Autor> autores = new AutorDAO().obtenerTodos();
        listAutores.setListData(new Vector<>(autores));
    }

    private void llenarFormulario() {
        txtTitulo.setText(libroParaEditar.getTitulo());
        txtIsbn.setText(libroParaEditar.getIsbn());
        txtAnio.setText(String.valueOf(libroParaEditar.getAnioPublicacion()));
        txtUrlPortada.setText(libroParaEditar.getPortadaUrl());
        spinnerTotal.setValue(libroParaEditar.getCantidadTotal());
        spinnerDisponible.setValue(libroParaEditar.getCantidadDisponible());

        // Pre-seleccionar categoría
        seleccionarItemEnComboBox(cmbCategoria, libroParaEditar.getCategoriaId());

        // Pre-seleccionar editorial
        seleccionarItemEnComboBox(cmbEditorial, libroParaEditar.getEditorialId());

        // Pre-seleccionar autores en la lista
        if (libroParaEditar.getAutoresIds() != null) {
            List<Long> idsAutoresSeleccionados = libroParaEditar.getAutoresIds().stream().map(Long::parseLong).collect(Collectors.toList());
            seleccionarItemsEnLista(listAutores, idsAutoresSeleccionados);
        }
    }

    private void guardarLibro() {
        if (txtTitulo.getText().trim().isEmpty() || txtIsbn.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El título y el ISBN son obligatorios.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Libro libro = new Libro();
        libro.setTitulo(txtTitulo.getText());
        libro.setIsbn(txtIsbn.getText());
        libro.setAnioPublicacion(Integer.parseInt(txtAnio.getText()));
        libro.setPortadaUrl(txtUrlPortada.getText());
        libro.setCantidadTotal((Integer) spinnerTotal.getValue());
        libro.setCantidadDisponible((Integer) spinnerDisponible.getValue());

        // Obtener ID de los objetos seleccionados
        libro.setCategoriaId(((Categoria) cmbCategoria.getSelectedItem()).getId());
        libro.setEditorialId(((Editorial) cmbEditorial.getSelectedItem()).getId());

        List<Autor> autoresSeleccionados = listAutores.getSelectedValuesList();
        if (autoresSeleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar al menos un autor.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<Long> idsAutores = autoresSeleccionados.stream().map(Autor::getId).collect(Collectors.toList());

        boolean exito;
        if (libroParaEditar == null) {
            exito = libroDAO.registrarLibro(libro, idsAutores);
        } else {
            libro.setId(libroParaEditar.getId());
            exito = libroDAO.actualizarLibro(libro, idsAutores);
        }

        if (exito) {
            JOptionPane.showMessageDialog(this, "Libro guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            panelPadre.cargarLibrosAsync(null);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Ocurrió un error al guardar el libro.", "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- MÉTODOS DE AYUDA PARA SELECCIONAR ITEMS ---
    private <T> void seleccionarItemEnComboBox(JComboBox<T> comboBox, long idBuscado) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            T item = comboBox.getItemAt(i);
            long itemId = 0;
            if (item instanceof Categoria) itemId = ((Categoria) item).getId();
            else if (item instanceof Editorial) itemId = ((Editorial) item).getId();

            if (itemId == idBuscado) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private void seleccionarItemsEnLista(JList<Autor> lista, List<Long> idsBuscados) {
        List<Integer> indicesParaSeleccionar = new ArrayList<>();
        ListModel<Autor> model = lista.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            if (idsBuscados.contains(model.getElementAt(i).getId())) {
                indicesParaSeleccionar.add(i);
            }
        }
        int[] indices = indicesParaSeleccionar.stream().mapToInt(i -> i).toArray();
        lista.setSelectedIndices(indices);
    }
}
