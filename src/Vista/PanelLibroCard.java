package Vista;

import Modelo.Libro; // Importar el modelo
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class PanelLibroCard extends JFrame {
    private JPanel panelCard;
    private JLabel lblPortada;
    private JLabel lblTituloLibro;
    private JLabel lblAutorLibro;
    private JLabel lblDisponibilidad;
    private JButton btnVerDetalles;

    // Getter para que el panel principal pueda añadir esta tarjeta
    public JPanel getPanelCard() {
        return panelCard;
    }

    /**
     * Rellena la tarjeta con los datos de un libro específico.
     * @param libro El objeto Libro con los datos a mostrar.
     */

    public void setData(Libro libro) {
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
}
