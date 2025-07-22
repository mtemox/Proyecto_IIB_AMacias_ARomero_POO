package Vista;

import DAO.LibroDAO; // Importar el DAO
import Modelo.Libro; // Importar el modelo
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PanelGestionLibros {
    private JPanel PanelGestionLibros;
    private JTextField txtBusquedaLibro;
    private JButton btnBuscarLibro;
    private JButton btnAgregarNuevoLibro;
    private JPanel panelGridDeLibros;

    public PanelGestionLibros() {
        // Establecemos un layout de tipo rejilla para organizar las tarjetas
        // GridLayout(filas, columnas, espacio_horizontal, espacio_vertical)
        // Poner 0 en filas significa "tantas filas como sea necesario"
        panelGridDeLibros.setLayout(new GridLayout(0, 4, 15, 15));

        // Llamamos al metodo para cargar los datos en cuanto se crea el panel
        cargarLibros();
    }

    // Getter para que el FormPrincipal pueda mostrar este panel
    public JPanel getPanel() {
        return PanelGestionLibros;
    }

    /**
     * Usa el LibroDAO para obtener los libros de la BD y crea
     * una tarjeta por cada uno, añadiéndola al panel.
     */

    private void cargarLibros() {
        LibroDAO libroDAO = new LibroDAO();
        List<Libro> listaDeLibros = libroDAO.obtenerTodosLosLibros();

        // Limpiamos el panel por si tenía algo antes
        panelGridDeLibros.removeAll();

        // Recorremos la lista de libros y creamos una tarjeta para cada uno
        for (Libro libro : listaDeLibros) {
            PanelLibroCard card = new PanelLibroCard(); // Creamos una nueva tarjeta
            card.setData(libro); // Le pasamos los datos del libro
            panelGridDeLibros.add(card.getPanelCard()); // Añadimos la tarjeta al panel de rejilla
        }

        // Revalidamos y repintamos el panel para que los cambios se muestren
        panelGridDeLibros.revalidate();
        panelGridDeLibros.repaint();
    }
}
