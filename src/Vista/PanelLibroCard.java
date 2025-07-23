package Vista;

import DAO.LibroDAO;
import Modelo.Libro;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class PanelLibroCard extends JFrame {
    private JPanel panelCard;
    private JLabel lblPortada;
    private JLabel lblTituloLibro;
    private JLabel lblAutorLibro;
    private JLabel lblDisponibilidad;
    private JButton btnVerDetalles;

    private Libro libro; // Almacenamos el objeto libro
    private LibroDAO libroDAO;

    public PanelLibroCard() {
        this.libroDAO = new LibroDAO();
        btnVerDetalles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarDetalles();
            }
        });
    }

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

        // --- Cargar la imagen desde la URL ---
        // NOTA: En una aplicación real, esto debería hacerse en un hilo separado (con SwingWorker)
        // para no congelar la interfaz si la descarga de la imagen tarda mucho.
        // Cargar imagen (esto puede hacerse en un hilo aparte con SwingWorker para mejor rendimiento)
        try {
            URL url = new URL(libro.getPortadaUrl());
            Image image = ImageIO.read(url);
            // Redimensionar la imagen para que quepa en el JLabel
            Image scaledImage = image.getScaledInstance(120, 180, Image.SCALE_SMOOTH);
            lblPortada.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            // Si hay un error (URL mala, sin internet), poner una imagen por defecto
            System.err.println("Error al cargar portada: " + e.getMessage());
            lblPortada.setIcon(null); // O poner un icono de "imagen no encontrada"
            lblPortada.setText("Sin portada");
        }
    }

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
}
