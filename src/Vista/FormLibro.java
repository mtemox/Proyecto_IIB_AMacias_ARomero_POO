package Vista;

import DAO.*;
import Modelo.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private JComboBox<Editorial> cmbEditorial;
    private JComboBox<Categoria> cmbCategoria;
    private JList<Autor> listAutores;

    // Paneles contenedores del archivo .form (debes asegurarte que existan)
    private JPanel panelCategoria;
    private JButton btnNuevaCategoria;
    private JPanel panelEditorial;
    private JPanel panelAutores;
    private JButton btnNuevoAutor;
    private JButton btnNuevaEditorial;

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

        // Accion para el boton btnGuardar
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarLibro();

            }
        });

        // Accion para el boton btnCancelar
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Accion para el boton btnNuevoAutor
        btnNuevoAutor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarNuevoAutor();
            }
        });

        // Accion para el boton btnNuevaEditorial
        btnNuevaEditorial.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarNuevaEditorial();
            }
        });

        // Accion para el boton btnNuevaCategoria
        btnNuevaCategoria.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarNuevaCategoria();
            }
        });
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


    // LÓGICA PARA LOS NUEVOS BOTONES
    private void agregarNuevoAutor() {
        JTextField nombreField = new JTextField(20);
        JTextField apellidoField = new JTextField(20);
        JTextField nacionalidadField = new JTextField(20);
        JTextField fechaNacField = new JTextField(10);
        fechaNacField.setToolTipText("Formato: AAAA-MM-DD");

        JPanel panelDialogo = new JPanel(new GridLayout(0, 2, 5, 5));
        panelDialogo.add(new JLabel("Nombre:"));
        panelDialogo.add(nombreField);
        panelDialogo.add(new JLabel("Apellido:"));
        panelDialogo.add(apellidoField);
        panelDialogo.add(new JLabel("Nacionalidad:"));
        panelDialogo.add(nacionalidadField);
        panelDialogo.add(new JLabel("Fecha Nacimiento (opcional):"));
        panelDialogo.add(fechaNacField);

        int result = JOptionPane.showConfirmDialog(this, panelDialogo, "Agregar Nuevo Autor",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String nombre = nombreField.getText().trim();
            String apellido = apellidoField.getText().trim();
            if (nombre.isEmpty() || apellido.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nombre y Apellido son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Autor nuevoAutor = new Autor();
            nuevoAutor.setNombre(nombre);
            nuevoAutor.setApellido(apellido);
            nuevoAutor.setNacionalidad(nacionalidadField.getText().trim());
            try {
                if (!fechaNacField.getText().trim().isEmpty()) {
                    nuevoAutor.setFechaNacimiento(LocalDate.parse(fechaNacField.getText().trim()));
                }
            } catch (java.time.format.DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use AAAA-MM-DD.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
                return;
            }

            AutorDAO autorDAO = new AutorDAO();
            Autor autorRegistrado = autorDAO.registrarAutor(nuevoAutor);

            if (autorRegistrado != null) {
                JOptionPane.showMessageDialog(this, "Autor '" + autorRegistrado.getNombreCompleto() + "' agregado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                // Refrescar la lista y seleccionar el nuevo autor
                List<Autor> autoresActuales = new AutorDAO().obtenerTodos();
                listAutores.setListData(new Vector<>(autoresActuales));
                listAutores.setSelectedValue(autorRegistrado, true); // El true hace scroll para que sea visible
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo agregar el autor.", "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void agregarNuevaEditorial() {
        JTextField nombreField = new JTextField(20);
        JTextField paisField = new JTextField(20);

        JPanel panelDialogo = new JPanel(new GridLayout(0, 2, 5, 5));
        panelDialogo.add(new JLabel("Nombre:"));
        panelDialogo.add(nombreField);
        panelDialogo.add(new JLabel("País de Origen (opcional):"));
        panelDialogo.add(paisField);

        int result = JOptionPane.showConfirmDialog(this, panelDialogo, "Agregar Nueva Editorial",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String nombre = nombreField.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre de la editorial es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Editorial nuevaEditorial = new Editorial();
            nuevaEditorial.setNombre(nombre);
            nuevaEditorial.setPaisOrigen(paisField.getText().trim());

            EditorialDAO editorialDAO = new EditorialDAO();
            Editorial editorialRegistrada = editorialDAO.registrarEditorial(nuevaEditorial);

            if (editorialRegistrada != null) {
                JOptionPane.showMessageDialog(this, "Editorial '" + editorialRegistrada.getNombre() + "' agregada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                List<Editorial> editorialesActuales = new EditorialDAO().obtenerTodas();
                cmbEditorial.setModel(new DefaultComboBoxModel<>(new Vector<>(editorialesActuales)));
                cmbEditorial.setSelectedItem(editorialRegistrada);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo agregar la editorial. Es posible que ya exista.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void agregarNuevaCategoria() {
        JTextField nombreField = new JTextField(20);
        JTextArea descripcionArea = new JTextArea(3, 20); // Área de texto para descripciones más largas
        descripcionArea.setLineWrap(true);
        descripcionArea.setWrapStyleWord(true);

        JPanel panelDialogo = new JPanel(new BorderLayout(5, 5));

        JPanel formPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        formPanel.add(new JLabel("Nombre:"));
        formPanel.add(nombreField);
        formPanel.add(new JLabel("Descripción (opcional):"));

        panelDialogo.add(formPanel, BorderLayout.NORTH);
        panelDialogo.add(new JScrollPane(descripcionArea), BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, panelDialogo, "Agregar Nueva Categoría",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String nombre = nombreField.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre de la categoría es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Categoria nuevaCategoria = new Categoria();
            nuevaCategoria.setNombre(nombre);
            nuevaCategoria.setDescripcion(descripcionArea.getText().trim());

            CategoriaDAO categoriaDAO = new CategoriaDAO();
            Categoria categoriaRegistrada = categoriaDAO.registrarCategoria(nuevaCategoria);

            if (categoriaRegistrada != null) {
                JOptionPane.showMessageDialog(this, "Categoría '" + categoriaRegistrada.getNombre() + "' agregada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                List<Categoria> categoriasActuales = new CategoriaDAO().obtenerTodas();
                cmbCategoria.setModel(new DefaultComboBoxModel<>(new Vector<>(categoriasActuales)));
                cmbCategoria.setSelectedItem(categoriaRegistrada);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo agregar la categoría. Es posible que ya exista.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
