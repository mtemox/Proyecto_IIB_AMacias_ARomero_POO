package Utils;

import javax.swing.*;
import java.awt.Image;
import java.net.URL;

public class ImageUtils {

    /**
     * Carga una imagen desde la ruta de recursos, la redimensiona y la asigna a un JLabel.
     *
     * @param label El JLabel donde se mostrará la imagen.
     * @param resourcePath La ruta de la imagen dentro del proyecto (ej: "/resources/logo.png").
     * @param width El ancho deseado para la imagen.
     * @param height La altura deseada para la imagen.
     */
    public static void loadImage(JLabel label, String resourcePath, int width, int height) {
        URL imageUrl = ImageUtils.class.getResource(resourcePath);

        if (imageUrl != null) {
            // 1. Crea el ImageIcon original
            ImageIcon originalIcon = new ImageIcon(imageUrl);
            // 2. Obtiene la 'Image' del ImageIcon original y la escala
            Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            // 3. Crea un nuevo ImageIcon a partir de la imagen ya redimensionada
            ImageIcon resizedIcon = new ImageIcon(scaledImage);
            // 4. Asigna el icono final al JLabel
            label.setIcon(resizedIcon);
        } else {
            System.err.println("Error: No se encontró el recurso de imagen en la ruta: " + resourcePath);
            label.setText("Img no encontrada");
        }
    }
}