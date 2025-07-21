package Vista;

import DAO.UsuarioSistemaDAO;
import Modelo.UsuarioSistema;

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

    public FormLogin() {
        setTitle("Iniciar Sesión");
        setContentPane(panelLogin);
        pack();
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
