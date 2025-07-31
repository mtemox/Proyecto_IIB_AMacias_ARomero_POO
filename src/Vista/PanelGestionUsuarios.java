package Vista;

import DAO.UsuarioSistemaDAO;
import Modelo.UsuarioSistema;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel para la gestión de usuarios del sistema (empleados).
 * Accesible solo para administradores, permite crear y modificar usuarios.
 */
public class PanelGestionUsuarios {
    private JPanel PanelGestionUsuarios;
    private JTable tblUsuarios;
    private JTextField txtUsername;
    private JComboBox<String> cmbRol;
    private JComboBox<String> cmbEstado;
    private JButton btnNuevoUsuario;
    private JButton btnGuardarUsuario;
    private JButton btnEditarUsuario;
    private JPasswordField txtPassword;

    private DefaultTableModel tableModel;
    private UsuarioSistemaDAO usuarioDAO;
    private long idUsuarioSeleccionado = -1;

    /**
     * Constructor del panel. Inicializa componentes, DAO y carga los usuarios.
     */
    public PanelGestionUsuarios() {
        usuarioDAO = new UsuarioSistemaDAO();
        inicializarTabla();
        configurarComboBoxes();
        cargarUsuarios();
        configurarListeners();
        habilitarCampos(false);

    }

    /**
     * Devuelve el panel principal para ser mostrado.
     * @return El JPanel de gestión de usuarios.
     */
    public JPanel getPanel() {
        return PanelGestionUsuarios;
    }

    /**
     * Configura el modelo y las columnas de la tabla de usuarios.
     */
    private void inicializarTabla() {
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.addColumn("ID");
        tableModel.addColumn("Username");
        tableModel.addColumn("Password");
        tableModel.addColumn("Rol");
        tableModel.addColumn("Estado");
        tblUsuarios.setModel(tableModel);
    }

    /**
     * Rellena los ComboBox de rol y estado con las opciones válidas.
     */
    private void configurarComboBoxes() {
        // Roles definidos en la BD
        cmbRol.addItem("ADMINISTRADOR");
        cmbRol.addItem("BIBLIOTECARIO");

        // Estados definidos en la BD
        cmbEstado.addItem("ACTIVO");
        cmbEstado.addItem("INACTIVO");
    }

    /**
     * Carga todos los usuarios desde la base de datos y los muestra en la tabla.
     */
    private void cargarUsuarios() {
        tableModel.setRowCount(0);
        List<UsuarioSistema> listaUsuarios = usuarioDAO.obtenerTodosLosUsuarios();
        for (UsuarioSistema usuario : listaUsuarios) {
            tableModel.addRow(new Object[]{
                    usuario.getId(),
                    usuario.getUsername(),
                    usuario.getPassword(),
                    usuario.getRol(),
                    usuario.getEstado()
            });
        }
    }

    /**
     * Configura los listeners para los botones de nuevo, editar y guardar.
     */
    private void configurarListeners() {

        // Accion para el boton btnNuevoUsuario
        btnNuevoUsuario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarCampos();
                habilitarCampos(true);
                txtUsername.setEditable(true);
                idUsuarioSeleccionado = -1;
            }
        });

        // Accion para el boton btnNuevoUsuario
        btnEditarUsuario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int filaSeleccionada = tblUsuarios.getSelectedRow();
                if (filaSeleccionada != -1) {
                    mostrarDatosUsuarioEnFormulario(filaSeleccionada);
                    habilitarCampos(true);
                    txtUsername.setEditable(false); // El username no se debería poder cambiar
                } else {
                    JOptionPane.showMessageDialog(PanelGestionUsuarios, "Seleccione un usuario de la tabla para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }

            }
        });

        // Accion para el boton btnGuardarUsuario
        btnGuardarUsuario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarUsuario();
            }
        });

    }

    /**
     * Muestra los datos del usuario seleccionado en la tabla en los campos del formulario.
     * @param fila El índice de la fila seleccionada.
     */
    private void mostrarDatosUsuarioEnFormulario(int fila) {
        idUsuarioSeleccionado = (long) tableModel.getValueAt(fila, 0);
        txtUsername.setText((String) tableModel.getValueAt(fila, 1));
        txtPassword.setText((String) tableModel.getValueAt(fila, 2));
        cmbRol.setSelectedItem(tableModel.getValueAt(fila, 3));
        cmbEstado.setSelectedItem(tableModel.getValueAt(fila, 4));
        //txtPassword.setText(""); // La contraseña no se muestra por seguridad
        //txtPassword.setToolTipText("Dejar en blanco para no cambiar la contraseña");
    }

    /**
     * Guarda los datos del formulario, ya sea creando un nuevo usuario o actualizando uno existente.
     */
    private void guardarUsuario() {
        if (txtUsername.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(PanelGestionUsuarios, "El campo Username es obligatorio.", "Campo Vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Si es un usuario nuevo, la contraseña es obligatoria
        String password = new String(txtPassword.getPassword());
        if (idUsuarioSeleccionado == -1 && password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(PanelGestionUsuarios, "La contraseña es obligatoria para nuevos usuarios.", "Campo Vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UsuarioSistema usuario = new UsuarioSistema();
        usuario.setUsername(txtUsername.getText().trim());
        usuario.setPassword(password); // Se guarda en texto plano (temporalmente)
        usuario.setRol((String) cmbRol.getSelectedItem());
        usuario.setEstado((String) cmbEstado.getSelectedItem());

        boolean exito;
        String mensajeAccion = "";
        if (idUsuarioSeleccionado == -1) { // Es un nuevo usuario
            exito = usuarioDAO.registrarUsuario(usuario);
            mensajeAccion = "registrado";
        } else { // Es una actualización
            usuario.setId(idUsuarioSeleccionado);
            exito = usuarioDAO.actualizarUsuario(usuario);
            mensajeAccion = "actualizado";
        }

        if (exito) {
            JOptionPane.showMessageDialog(PanelGestionUsuarios, "Usuario " + mensajeAccion + " exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarUsuarios();
            limpiarCampos();
            habilitarCampos(false);
        } else {
            JOptionPane.showMessageDialog(PanelGestionUsuarios, "Ocurrió un error al guardar. Verifique si el username ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Habilita o deshabilita los campos del formulario para edición.
     * @param habilitar `true` para habilitar, `false` para deshabilitar.
     */
    private void habilitarCampos(boolean habilitar) {
        txtUsername.setEditable(habilitar);
        txtPassword.setEditable(habilitar);
        cmbRol.setEnabled(habilitar);
        cmbEstado.setEnabled(habilitar);
        btnGuardarUsuario.setEnabled(habilitar);
    }

    /**
     * Limpia todos los campos del formulario y resetea el ID seleccionado.
     */
    private void limpiarCampos() {
        idUsuarioSeleccionado = -1;
        txtUsername.setText("");
        txtPassword.setText("");
        cmbRol.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);
        txtUsername.setToolTipText(null);
        txtPassword.setToolTipText(null);
    }
}
