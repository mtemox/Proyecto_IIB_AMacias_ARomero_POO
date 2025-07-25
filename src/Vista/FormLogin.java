package Vista;

import DAO.UsuarioSistemaDAO;
import Modelo.UsuarioSistema;
import Utils.SessionManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FormLogin extends JFrame {
    private JButton btnIngresar;
    private JPanel panelLogin;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JLabel lblLogo;
    private JButton lblRegistrarse;
    private JLabel lblLogoTitle;
    java.net.URL imageUrl = getClass().getResource("/resources/logo.png");
    java.net.URL imageUrl2 = getClass().getResource("/resources/logo4.jpg");

    public FormLogin() {
        setTitle("Iniciar Sesión");
        setContentPane(panelLogin);

        if (imageUrl != null && imageUrl2 != null) {
            // 1. Crea el ImageIcon original
            ImageIcon originalIcon = new ImageIcon(imageUrl2);
            ImageIcon originalIcon2 = new ImageIcon(imageUrl);
            // 2. Define el nuevo tamaño que deseas para la imagen
            int nuevoAncho = 540; // Por ejemplo, 100 píxeles de ancho
            int nuevoAlto = 860;  // Por ejemplo, 100 píxeles de alto

            int nuevoAncho2 = 75; // Por ejemplo, 100 píxeles de ancho
            int nuevoAlto2 = 75;  // Por ejemplo, 100 píxeles de alto

            // 3. Obtiene la 'Image' del ImageIcon original y la escala
            //    Image.SCALE_SMOOTH le da mayor calidad al redimensionamiento.
            java.awt.Image imagenOriginal = originalIcon.getImage();
            java.awt.Image imagenOriginal2 = originalIcon2.getImage();
            java.awt.Image imagenRedimensionada = imagenOriginal.getScaledInstance(nuevoAncho, nuevoAlto, java.awt.Image.SCALE_SMOOTH);
            java.awt.Image imagenRedimensionada2 = imagenOriginal2.getScaledInstance(nuevoAncho2, nuevoAlto2, java.awt.Image.SCALE_SMOOTH);
            // 4. Crea un nuevo ImageIcon a partir de la imagen ya redimensionada
            ImageIcon iconoRedimensionado = new ImageIcon(imagenRedimensionada);
            ImageIcon iconoRedimensionado2 = new ImageIcon(imagenRedimensionada2);
            // 5. Asigna el icono final (ya con el tamaño correcto) a tu JLabel
            lblLogo.setIcon(iconoRedimensionado);
            lblLogoTitle.setIcon(iconoRedimensionado2);
        } else {
            System.err.println("Error: No se encontró la imagen en la ruta especificada.");
        }

        setSize(1140, 860);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);



        lblRegistrarse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                new FormRegistro();
                dispose();

            }
        });

        btnIngresar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String username = txtUsuario.getText();
                String passwordIngresada = new String(txtPassword.getPassword());

                UsuarioSistemaDAO usuarioDAO = new UsuarioSistemaDAO();
                // 1. Buscamos al usuario en la BD
                UsuarioSistema usuarioEncontrado = usuarioDAO.obtenerUsuarioPorUsername(username);

                // 2. Verificamos si el usuario existe
                if (usuarioEncontrado != null) {
                    // 3. ¡AQUÍ IRÍA LA VERIFICACIÓN DE LA CONTRASEÑA ENCRIPTADA!
                    // Por ahora, haremos una comparación simple.
                    // En un proyecto real, usarías una librería como BCrypt.
                    // Ejemplo: if (BCrypt.checkpw(passwordIngresada, usuarioEncontrado.getPassword())) { ... }

                    if (passwordIngresada.equals(usuarioEncontrado.getPassword())) { // Comparación simple (NO SEGURA)
                        JOptionPane.showMessageDialog(null, "¡Bienvenido " + usuarioEncontrado.getRol() + "!", "Login Exitoso", JOptionPane.INFORMATION_MESSAGE);
                        SessionManager.getInstance().setUsuarioLogueado(usuarioEncontrado);
                        // Abrir el FormPrincipal
                        new FormPrincipal();
                        dispose(); // Cierra la ventana de login
                    } else {
                        JOptionPane.showMessageDialog(null, "Contraseña incorrecta.", "Error de Login", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Usuario no encontrado o inactivo.", "Error de Login", JOptionPane.ERROR_MESSAGE);
                }

            }
        });
    }

}
