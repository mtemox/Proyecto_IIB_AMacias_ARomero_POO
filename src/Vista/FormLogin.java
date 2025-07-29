package Vista;

import DAO.UsuarioSistemaDAO;
import Modelo.UsuarioSistema;
import Utils.SessionManager;
import Utils.ImageUtils;

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

        ImageUtils.loadImage(lblLogo, "/resources/logo4.jpg", 540, 860);
        ImageUtils.loadImage(lblLogoTitle, "/resources/logo.png", 75, 75);

        setSize(1140, 860);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        

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
