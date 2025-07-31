package Vista;

import DAO.SocioDAO;
import Modelo.Socio;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel para la gestión integral de socios.
 * Permite ver una lista de socios, así como agregar, editar y guardar sus datos.
 */
public class PanelGestionSocios {
    private JPanel PanelGestionSocios;
    private JTextField txtBusquedaSocio;
    private JTable tblSocios;
    private JTextField txtCedula;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtEmail;
    private JTextField txtTelefono;
    private JButton btnNuevoSocio;
    private JButton btnGuardarSocio;
    private JButton btnEditarSocio;
    private JComboBox<String> cmbEstado;

    private DefaultTableModel tableModel;
    private SocioDAO socioDAO;
    private long idSocioSeleccionado = -1L; // Para saber qué socio estamos editando

    /**
     * Constructor del panel. Inicializa la tabla, los listeners y carga los socios.
     */
    public PanelGestionSocios() {

        socioDAO = new SocioDAO();
        inicializarTabla();
        cargarSocios();
        configurarListeners();

        // Llenar el ComboBox de estados
        cmbEstado.addItem("ACTIVO");
        cmbEstado.addItem("CON_MULTAS");
        cmbEstado.addItem("VETADO");

        // Deshabilitar campos y botones al inicio
        habilitarCampos(false);

    }

    /**
     * Devuelve el panel principal para ser mostrado.
     * @return El JPanel de gestión de socios.
     */
    public JPanel getPanel() {
        return PanelGestionSocios;
    }

    /**
     * Configura el modelo y las columnas de la tabla de socios.
     */
    private void inicializarTabla() {
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer que la tabla no sea editable
            }
        };
        tableModel.addColumn("ID");
        tableModel.addColumn("Cédula");
        tableModel.addColumn("Nombre");
        tableModel.addColumn("Apellido");
        tableModel.addColumn("Email");
        tableModel.addColumn("Teléfono");
        tableModel.addColumn("Estado");
        tblSocios.setModel(tableModel);
    }

    /**
     * Carga todos los socios desde la base de datos y los muestra en la tabla.
     */
    private void cargarSocios() {
        tableModel.setRowCount(0); // Limpiar tabla
        List<Socio> listaSocios = socioDAO.obtenerTodosLosSocios();
        for (Socio socio : listaSocios) {
            tableModel.addRow(new Object[]{
                    socio.getId(),
                    socio.getCedula(),
                    socio.getNombre(),
                    socio.getApellido(),
                    socio.getEmail(),
                    socio.getTelefono(),
                    socio.getEstadoSocio()
            });
        }
    }

    /**
     * Configura los listeners para los botones de nuevo, guardar y editar.
     */
    private void configurarListeners() {
        // Listener para el botón "Nuevo"
        btnNuevoSocio.addActionListener(e -> {
            limpiarCampos();
            habilitarCampos(true);
            txtCedula.setEditable(true); // Cédula editable solo para nuevos socios
            idSocioSeleccionado = -1;
        });

        // Listener para el botón "Guardar"
        btnGuardarSocio.addActionListener(e -> guardarSocio());

        // Listener para el botón "Editar"
        btnEditarSocio.addActionListener(e -> {
            int filaSeleccionada = tblSocios.getSelectedRow();
            if (filaSeleccionada != -1) {
                mostrarDatosSocioEnFormulario(filaSeleccionada);
                habilitarCampos(true);
                txtCedula.setEditable(false); // Cédula no se puede editar
            } else {
                JOptionPane.showMessageDialog(PanelGestionSocios, "Por favor, seleccione un socio de la tabla para editar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * Muestra los datos del socio seleccionado en la tabla en los campos del formulario.
     * @param fila El índice de la fila seleccionada en la tabla.
     */
    private void mostrarDatosSocioEnFormulario(int fila) {

        Object valorId = tableModel.getValueAt(fila, 0);
        if (valorId instanceof Long) {
            idSocioSeleccionado = (Long) valorId;
        } else {
            idSocioSeleccionado = Long.parseLong(valorId.toString());
        }
        txtCedula.setText((String) tableModel.getValueAt(fila, 1));
        txtNombre.setText((String) tableModel.getValueAt(fila, 2));
        txtApellido.setText((String) tableModel.getValueAt(fila, 3));
        txtEmail.setText((String) tableModel.getValueAt(fila, 4));
        txtTelefono.setText((String) tableModel.getValueAt(fila, 5));
        cmbEstado.setSelectedItem(tableModel.getValueAt(fila, 6));
    }

    /**
     * Guarda los datos del formulario, ya sea creando un nuevo socio o actualizando uno existente.
     */
    private void guardarSocio() {
        // Validaciones básicas
        if (txtNombre.getText().trim().isEmpty() || txtApellido.getText().trim().isEmpty() || txtCedula.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(PanelGestionSocios, "Los campos Cédula, Nombre y Apellido son obligatorios.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Socio socio = new Socio();
        socio.setCedula(txtCedula.getText());
        socio.setNombre(txtNombre.getText());
        socio.setApellido(txtApellido.getText());
        socio.setEmail(txtEmail.getText());
        socio.setTelefono(txtTelefono.getText());
        socio.setEstadoSocio((String) cmbEstado.getSelectedItem());

        boolean exito;
        if (idSocioSeleccionado == -1) { // Es un nuevo socio
            // NOTA: La lógica de registro de `FormRegistro` es más completa.
            // Aquí simplificamos para la gestión. Idealmente, se unificaría.
            socio.setFechaRegistro(java.time.LocalDate.now());
            exito = socioDAO.registrarSocio(socio);
        } else { // Es una actualización
            socio.setId(idSocioSeleccionado);
            exito = socioDAO.actualizarSocio(socio);
        }

        if (exito) {
            JOptionPane.showMessageDialog(PanelGestionSocios, "Socio guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarSocios();
            limpiarCampos();
            habilitarCampos(false);
        } else {
            JOptionPane.showMessageDialog(PanelGestionSocios, "Ocurrió un error al guardar el socio.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Habilita o deshabilita los campos del formulario para edición.
     * @param habilitar `true` para habilitar, `false` para deshabilitar.
     */
    private void habilitarCampos(boolean habilitar) {
        txtCedula.setEditable(habilitar);
        txtNombre.setEditable(habilitar);
        txtApellido.setEditable(habilitar);
        txtEmail.setEditable(habilitar);
        txtTelefono.setEditable(habilitar);
        cmbEstado.setEnabled(habilitar);
        btnGuardarSocio.setEnabled(habilitar);
    }

    /**
     * Limpia todos los campos del formulario y resetea el ID seleccionado.
     */
    private void limpiarCampos() {
        idSocioSeleccionado = -1;
        txtCedula.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtEmail.setText("");
        txtTelefono.setText("");
        cmbEstado.setSelectedIndex(0);
    }
}
