package Vista;

import DAO.LibroDAO; // Importar el DAO
import Modelo.Libro; // Importar el modelo
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PanelGestionLibros {
    private JPanel PanelGestionLibros;
    private JTextField txtBusquedaLibro;
    private JButton btnBuscarLibro;
    private JButton btnAgregarNuevoLibro;
    private JPanel panelGridDeLibros;

    // Declaramos el DAO como una variable de la clase para no crearlo múltiples veces
    private LibroDAO libroDAO;

    public PanelGestionLibros() {
        // Establecemos un layout de tipo rejilla para organizar las tarjetas
        // GridLayout(filas, columnas, espacio_horizontal, espacio_vertical)
        // Poner 0 en filas significa "tantas filas como sea necesario"
        this.libroDAO = new LibroDAO(); // Inicializamos el DAO
        panelGridDeLibros.setLayout(new GridLayout(0, 3, 15, 15));

        // Llamamos al metodo para cargar los datos en cuanto se crea el panel
        cargarLibros();

        // Accion para el boton de btnAgregarNuevoLibro
        btnAgregarNuevoLibro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirFormularioLibro(null);
            }
        });

        // Accion para el boton de btnBuscarLibro
        btnBuscarLibro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String termino = txtBusquedaLibro.getText();
                cargarLibros(termino);
            }
        });
    }

    // Getter para que el FormPrincipal pueda mostrar este panel
    public JPanel getPanel() {
        return PanelGestionLibros;
    }

    /**
     * Usa el LibroDAO para obtener los libros de la BD y crea
     * una tarjeta por cada uno, añadiéndola al panel.
     */

    public void cargarLibros() {
        LibroDAO libroDAO = new LibroDAO();
        List<Libro> listaDeLibros = libroDAO.obtenerTodosLosLibros();

        // Limpiamos el panel por si tenía algo antes
        panelGridDeLibros.removeAll();

        // Recorremos la lista de libros y creamos una tarjeta para cada uno
        for (Libro libro : listaDeLibros) {
            // <-- CAMBIO: Le pasamos "this" (la instancia de PanelGestionLibros) a la tarjeta
            PanelLibroCard card = new PanelLibroCard(this);
            card.setData(libro);
            panelGridDeLibros.add(card.getPanelCard());
        }

        // Revalidamos y repintamos el panel para que los cambios se muestren
        panelGridDeLibros.revalidate();
        panelGridDeLibros.repaint();
    }

    /**
     * Carga las tarjetas de libros en el panel, opcionalmente filtrando por un término de búsqueda.
     * @param termino El texto para filtrar. Si está vacío, se muestran todos los libros.
     */
    public void cargarLibros(String termino) {
        // Limpiamos el panel por si tenía algo antes
        panelGridDeLibros.removeAll();

        List<Libro> listaDeLibros;
        // Decidimos qué método del DAO llamar
        if (termino == null || termino.trim().isEmpty()) {
            listaDeLibros = libroDAO.obtenerTodosLosLibros();
        } else {
            listaDeLibros = libroDAO.buscarLibros(termino);
        }

        // Si la búsqueda no arrojó resultados, mostramos un mensaje
        if (listaDeLibros.isEmpty()) {
            // Usamos un JLabel para mostrar el mensaje dentro del panel
            JLabel mensajeVacio = new JLabel("No se encontraron libros que coincidan con la búsqueda.", SwingConstants.CENTER);
            mensajeVacio.setFont(new Font("Arial", Font.BOLD, 16));
            panelGridDeLibros.setLayout(new BorderLayout()); // Cambiamos el layout para centrar el mensaje
            panelGridDeLibros.add(mensajeVacio, BorderLayout.CENTER);
        } else {
            // Si hay resultados, volvemos a poner el GridLayout y añadimos las tarjetas
            panelGridDeLibros.setLayout(new GridLayout(0, 3, 15, 15));
            for (Libro libro : listaDeLibros) {
                PanelLibroCard card = new PanelLibroCard(this);
                card.setData(libro);
                panelGridDeLibros.add(card.getPanelCard());
            }
        }

        // Revalidamos y repintamos el panel para que los cambios se muestren
        panelGridDeLibros.revalidate();
        panelGridDeLibros.repaint();
    }

    /**
     * Abre el formulario para agregar o editar un libro.
     * @param libro El libro a editar, o null para crear uno nuevo.
     */

    public void abrirFormularioLibro(Libro libro) {
        JFrame framePadre = (JFrame) SwingUtilities.getWindowAncestor(this.PanelGestionLibros);

        // Siempre llamamos al mismo constructor.
        // Le pasamos el libro si estamos editando, o 'null' si es uno nuevo.
        // La lógica para saber si es "nuevo" o "editar" ahora está DENTRO de FormLibro.
        FormLibro form = new FormLibro(framePadre, this, libro);
        // ----------------------------------------------------

        form.setVisible(true); // Esto muestra el diálogo
    }

}
