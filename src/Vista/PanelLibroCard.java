package Vista;

import DAO.LibroDAO;
import Modelo.Libro;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

/**
 * Representa una "tarjeta" individual para mostrar la información resumida de un libro.
 * Incluye la portada, título, autor, disponibilidad y botones de acción.
 */
public class PanelLibroCard extends JFrame {
    private JPanel panelCard;
    private JLabel lblPortada;
    private JLabel lblTituloLibro;
    private JLabel lblAutorLibro;
    private JLabel lblDisponibilidad;
    private JButton btnVerDetalles;
    private JButton btnEditar;
    private JButton btnEliminar;

    private Libro libro; // Almacenamos el objeto libro
    private LibroDAO libroDAO;
    // Campo para guardar la referencia al panel que nos creó
    private final PanelGestionLibros panelGestionPadre;

    /**
     * Constructor de la tarjeta de libro.
     * @param panelPadre Referencia al panel de gestión que contiene esta tarjeta,
     * necesaria para refrescar la vista después de una acción.
     */
    public PanelLibroCard(PanelGestionLibros panelPadre) {
        this.panelGestionPadre = panelPadre; // Guardamos la referencia
        this.libroDAO = new LibroDAO();

        // Accion para el boton btnVerDetalles
        btnVerDetalles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarDetalles();
            }
        });

        // Accion para el boton btnEditar
        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editarLibro();
            }
        });

        // Accion para el boton btnEliminar
        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarLibro();
            }
        });
    }

    /**
     * Devuelve el panel de la tarjeta para ser añadido a un contenedor.
     * @return El JPanel que conforma la tarjeta.
     */
    // Getter para que el panel principal pueda añadir esta tarjeta
    public JPanel getPanelCard() {
        return panelCard;
    }

    /**
     * Rellena la tarjeta con los datos de un libro específico.
     * @param libro El objeto Libro con los datos a mostrar.
     */
    public void setData(Libro libro) {
        this.libro = libro;

        lblTituloLibro.setText("<html>" + libro.getTitulo() + "</html>"); // El <html> permite el salto de línea automático si el título es largo
        lblAutorLibro.setText(libro.getAutores());


        if (libro.getCantidadDisponible() > 0) {
            lblDisponibilidad.setText("Disponibles: " + libro.getCantidadDisponible());
            lblDisponibilidad.setForeground(new Color(0, 150, 0)); // Color verde
        } else {
            lblDisponibilidad.setText("No disponible");
            lblDisponibilidad.setForeground(Color.RED);
        }

        // --- MODIFICADO ---
        // Cargar la imagen de forma asíncrona
        lblPortada.setIcon(null);
        lblPortada.setText("Cargando..."); // Mensaje mientras se descarga la imagen

        SwingWorker<ImageIcon, Void> imageLoader = new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                // Se ejecuta en un hilo de fondo
                try {
                    URL url = new URL(libro.getPortadaUrl());
                    Image image = ImageIO.read(url);
                    Image scaledImage = image.getScaledInstance(120, 180, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaledImage);
                } catch (Exception e) {
                    System.err.println("Error al cargar portada para '" + libro.getTitulo() + "': " + e.getMessage());
                    return null; // Devuelve null si hay error
                }
            }

            @Override
            protected void done() {
                // Se ejecuta en el hilo de la UI
                try {
                    ImageIcon icon = get();
                    if (icon != null) {
                        lblPortada.setIcon(icon);
                        lblPortada.setText(""); // Limpiar texto
                    } else {
                        lblPortada.setText("Sin portada");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    lblPortada.setText("Error");
                }
            }
        };

        imageLoader.execute();
    }

    /**
     * Muestra un cuadro de diálogo con los detalles completos del libro.
     * Ofrece la opción de prestar el libro si hay ejemplares disponibles.
     */
    private void mostrarDetalles() {
        // Obtenemos los detalles completos desde la BD (esto no cambia)
        Libro libroDetallado = libroDAO.obtenerDetallesLibro(this.libro.getId());
        if (libroDetallado == null) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los detalles del libro.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- SECCIÓN SIMPLIFICADA ---
        // 1. Crear el mensaje como un String simple con saltos de línea.
        String mensaje = "ISBN: " + libroDetallado.getIsbn() + "\n" +
                "Autor(es): " + libroDetallado.getAutores() + "\n" +
                "Editorial: " + libroDetallado.getEditorial() + "\n" +
                "Año: " + libroDetallado.getAnioPublicacion() + "\n" +
                "Categoría: " + libroDetallado.getCategoria() + "\n\n" +
                "Descripción: " + (libroDetallado.getDescripcionCategoria() != null ? libroDetallado.getDescripcionCategoria() : "No disponible") + "\n" +
                "--------------------------------------------------\n" +
                "Total de Ejemplares: " + libroDetallado.getCantidadTotal() + "\n" +
                "Disponibles para préstamo: " + libroDetallado.getCantidadDisponible();
        // --- FIN DE LA SECCIÓN SIMPLIFICADA ---

        // 2. Definir los botones personalizados (esto no cambia)
        Object[] options;
        if (libroDetallado.getCantidadDisponible() > 0) {
            options = new Object[]{"Prestar", "Cerrar"};
        } else {
            options = new Object[]{"Cerrar"};
        }

        // 3. Mostrar el JOptionPane, pasándole el String simple
        int result = JOptionPane.showOptionDialog(
                this,
                mensaje, // ¡Ahora pasamos el String en lugar del panel!
                libroDetallado.getTitulo(),
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[options.length - 1]
        );

        // 4. Actuar según la opción elegida
        if (result == 0 && libroDetallado.getCantidadDisponible() > 0) { // Si se hizo clic en "Prestar"
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this.panelCard);
            if (topFrame instanceof FormPrincipal) {
                ((FormPrincipal) topFrame).navegarAPrestamosConLibro(libroDetallado);
            }
        }
    }

    /**
     * Llama al panel de gestión padre para abrir el formulario de edición para este libro.
     */
    private void editarLibro() {
        if (this.panelGestionPadre != null) {
            this.panelGestionPadre.abrirFormularioLibro(this.libro);
        }
    }

    /**
     * Pide confirmación y, si se acepta, elimina el libro de la base de datos.
     * Luego, solicita al panel padre que refresque la lista de libros.
     */
    private void eliminarLibro() {
        int confirmacion = JOptionPane.showConfirmDialog(
                this.panelCard,
                "¿Estás seguro de que quieres eliminar el libro '" + libro.getTitulo() + "'?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            boolean exito = libroDAO.eliminarLibro(this.libro.getId());
            if (exito) {
                JOptionPane.showMessageDialog(this.panelCard, "Libro eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                if (this.panelGestionPadre != null) {
                    // --- CORRECCIÓN AQUÍ ---
                    // Se llama al nuevo método asíncrono para recargar todos los libros.
                    // Pasamos 'null' para que no aplique ningún filtro de búsqueda.
                    this.panelGestionPadre.cargarLibrosAsync(null); // Refrescar la vista principal
                }
            } else {
                JOptionPane.showMessageDialog(this.panelCard, "No se pudo eliminar el libro.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
