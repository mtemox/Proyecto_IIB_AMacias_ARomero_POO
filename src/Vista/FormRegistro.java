package Vista;

import DAO.SocioDAO;
import Modelo.Socio;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FormRegistro extends JFrame {
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtCedula;
    private JTextField txtEmail;
    private JTextField txtTelefono;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JButton btnRegistrar;
    private JButton btnCancelar;
    private JPanel panelRegistro;

    public boolean validarCampos(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    public FormRegistro() {
        setTitle("Registro");
        setContentPane(panelRegistro);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);


        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 1. Recoger datos de los campos de texto
                String nombre = txtNombre.getText();
                String apellido = txtApellido.getText();
                String cedula = txtCedula.getText();
                String email = txtEmail.getText();
                String telefono = txtTelefono.getText();
                String username = txtUsername.getText();
                char[] passwordChar = txtPassword.getPassword();
                String password = new String(passwordChar);
                char[] passwordAgainChar = txtConfirmPassword.getPassword();
                String passwordAgain = new String(passwordAgainChar);


                // 2. Validar los datos (que no estén vacíos, que las contraseñas coincidan, etc.)
                if (    validarCampos(nombre) && validarCampos(apellido) && validarCampos(cedula) && validarCampos(email) &&
                        validarCampos(telefono) && validarCampos(username) && validarCampos(password) && validarCampos(passwordAgain)) {

                    // 3. Crear el objeto Socio
                    Socio nuevoSocio = new Socio(cedula, nombre, apellido, email, telefono, password);

                    // 4. Llamar al DAO para guardarlo
                    SocioDAO socioDAO = new SocioDAO();
                    boolean exito = socioDAO.registrarSocio(nuevoSocio);

                    // 5. Mostrar mensaje al usuario
                    if (exito) {
                        JOptionPane.showMessageDialog(null, "¡Registro exitoso!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        dispose(); // Cierra la ventana de registro
                    } else {
                        JOptionPane.showMessageDialog(null, "Hubo un error al registrar. Inténtelo de nuevo.", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } else {

                    JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                    return; // Detiene el proceso

                }
            }
        });
    }

}
