package Vista;

import javax.swing.*;
import java.awt.*;

/**
 * Un panel simple que muestra una imagen de bienvenida.
 * Es el primer panel que se ve al iniciar sesión.
 */
public class PanelBienvenida extends JFrame {
    private JPanel PanelBienvenida;
    private JLabel lblLogo;

    java.net.URL imageUrl = getClass().getResource("/resources/logo5.jpg");

    /**
     * Devuelve el panel de bienvenida con la imagen ya cargada y redimensionada.
     * @return El JPanel de bienvenida.
     */
    public JPanel getPanelBienvenida() {

        if (imageUrl != null) {
            // 1. Crea el ImageIcon original
            ImageIcon originalIcon = new ImageIcon(imageUrl);
            // 2. Define el nuevo tamaño que deseas para la imagen
            int nuevoAncho = 800; // Por ejemplo, 100 píxeles de ancho
            int nuevoAlto = 520;  // Por ejemplo, 100 píxeles de alto
            // 3. Obtiene la 'Image' del ImageIcon original y la escala
            //    Image.SCALE_SMOOTH le da mayor calidad al redimensionamiento.
            java.awt.Image imagenOriginal = originalIcon.getImage();
            java.awt.Image imagenRedimensionada = imagenOriginal.getScaledInstance(nuevoAncho, nuevoAlto, java.awt.Image.SCALE_SMOOTH);
            // 4. Crea un nuevo ImageIcon a partir de la imagen ya redimensionada
            ImageIcon iconoRedimensionado = new ImageIcon(imagenRedimensionada);
            // 5. Asigna el icono final (ya con el tamaño correcto) a tu JLabel
            lblLogo.setIcon(iconoRedimensionado);
        } else {
            System.err.println("Error: No se encontró la imagen en la ruta especificada.");
        }

        return PanelBienvenida;
    }

}
