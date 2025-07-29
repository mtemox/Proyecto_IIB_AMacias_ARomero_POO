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

        // --- MODIFICADO ---
        // Llamamos al nuevo metodo de carga asíncrona.
        cargarLibrosAsync(null);

        // Llamamos al metodo para cargar los datos en cuanto se crea el panel
        // cargarLibros();

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

    public void cargarLibrosAsync(String termino) {
        // 1. Limpia el panel y muestra un mensaje de carga.
        panelGridDeLibros.removeAll();
        panelGridDeLibros.setLayout(new BorderLayout()); // Layout para centrar el mensaje
        JLabel lblCargando = new JLabel("Cargando libros, por favor espere...", SwingConstants.CENTER);
        lblCargando.setFont(new Font("Arial", Font.ITALIC, 18));
        panelGridDeLibros.add(lblCargando, BorderLayout.CENTER);
        panelGridDeLibros.revalidate();
        panelGridDeLibros.repaint();

        // 2. Crea un SwingWorker para realizar la consulta a la BD en otro hilo.
        SwingWorker<List<Libro>, Void> worker = new SwingWorker<List<Libro>, Void>() {
            @Override
            protected List<Libro> doInBackground() throws Exception {
                // Esta parte se ejecuta en un hilo de fondo (NO TOCAR LA UI AQUÍ)
                if (termino == null || termino.trim().isEmpty()) {
                    return libroDAO.obtenerTodosLosLibros();
                } else {
                    return libroDAO.buscarLibros(termino);
                }
            }

            @Override
            protected void done() {
                // Esta parte se ejecuta de vuelta en el hilo de la UI cuando 'doInBackground' termina.
                try {
                    List<Libro> listaDeLibros = get(); // Obtiene el resultado

                    // 3. Limpia el panel y prepara el layout para las tarjetas.
                    panelGridDeLibros.removeAll();
                    panelGridDeLibros.setLayout(new GridLayout(0, 3, 15, 15));

                    if (listaDeLibros.isEmpty()) {
                        JLabel mensajeVacio = new JLabel("No se encontraron libros.", SwingConstants.CENTER);
                        mensajeVacio.setFont(new Font("Arial", Font.BOLD, 16));
                        panelGridDeLibros.setLayout(new BorderLayout());
                        panelGridDeLibros.add(mensajeVacio, BorderLayout.CENTER);
                    } else {
                        // 4. Crea y añade las tarjetas de libros.
                        for (Libro libro : listaDeLibros) {
                            PanelLibroCard card = new PanelLibroCard(PanelGestionLibros.this);
                            card.setData(libro); // 'setData' ahora también será asíncrono
                            panelGridDeLibros.add(card.getPanelCard());
                        }
                    }

                    // 5. Refresca la UI para mostrar las tarjetas.
                    panelGridDeLibros.revalidate();
                    panelGridDeLibros.repaint();

                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(panelGridDeLibros, "Error al cargar los libros.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        // 6. Inicia el worker.
        worker.execute();
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
