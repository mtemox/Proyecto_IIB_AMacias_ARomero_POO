package Vista;

import DAO.PenalizacionDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;

public class PanelGestionPenalizaciones {
    private JTextField txtBusquedaCedula;
    private JComboBox cmbFiltroEstado;
    private JButton btnBuscar;
    private JTable tblPenalizaciones;
    private JButton btnMarcarComoPagada;
    private JPanel PanelGestionPenalizaciones;

    private DefaultTableModel tableModel;
    private PenalizacionDAO penalizacionDAO;

    public PanelGestionPenalizaciones() {
        penalizacionDAO = new PenalizacionDAO();
        inicializarTabla();
        configurarFiltros();
        configurarListeners();
        cargarPenalizaciones(); // Carga inicial
    }

    public JPanel getPanel() {
        return PanelGestionPenalizaciones;
    }

    private void inicializarTabla() {
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.addColumn("ID Penalización");
        tableModel.addColumn("ID Socio"); // Ocultaremos esta columna
        tableModel.addColumn("Cédula Socio");
        tableModel.addColumn("Nombre Socio");
        tableModel.addColumn("Monto ($)");
        tableModel.addColumn("Estado");
        tableModel.addColumn("Fecha de Emisión");
        tableModel.addColumn("Libro del Préstamo");

        tblPenalizaciones.setModel(tableModel);

        // Ocultar la columna de ID de socio, solo la necesitamos para la lógica interna
        ocultarColumna(1);
    }

    private void ocultarColumna(int indiceColumna) {
        TableColumn columna = tblPenalizaciones.getColumnModel().getColumn(indiceColumna);
        columna.setMinWidth(0);
        columna.setMaxWidth(0);
        columna.setWidth(0);
        columna.setPreferredWidth(0);
    }

    private void configurarFiltros() {
        cmbFiltroEstado.addItem("TODOS");
        cmbFiltroEstado.addItem("PENDIENTE");
        cmbFiltroEstado.addItem("PAGADA");
        cmbFiltroEstado.setSelectedItem("PENDIENTE"); // Por defecto mostrar las pendientes
    }

    private void configurarListeners() {

        // Accion para el botón btnBuscar
        btnBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarPenalizaciones();
            }
        });

        // Accion para el botón btnMarcarComoPagada
        btnMarcarComoPagada.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                marcarComoPagada();

            }
        });

    }

    private void cargarPenalizaciones() {
        String cedula = txtBusquedaCedula.getText().trim();
        String estado = (String) cmbFiltroEstado.getSelectedItem();

        tableModel.setRowCount(0); // Limpiar tabla
        List<Object[]> penalizaciones = penalizacionDAO.buscarPenalizaciones(cedula, estado);

        for (Object[] row : penalizaciones) {
            tableModel.addRow(row);
        }
    }

    private void marcarComoPagada() {
        int filaSeleccionada = tblPenalizaciones.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(PanelGestionPenalizaciones, "Por favor, seleccione una penalización de la tabla.", "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String estadoActual = (String) tableModel.getValueAt(filaSeleccionada, 5);
        if ("PAGADA".equals(estadoActual)) {
            JOptionPane.showMessageDialog(PanelGestionPenalizaciones, "Esta penalización ya ha sido pagada.", "Operación Inválida", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        long penalizacionId = (long) tableModel.getValueAt(filaSeleccionada, 0);
        long socioId = (long) tableModel.getValueAt(filaSeleccionada, 1); // Obtenemos el ID del socio de la columna oculta
        BigDecimal monto = (BigDecimal) tableModel.getValueAt(filaSeleccionada, 4);

        int confirmacion = JOptionPane.showConfirmDialog(
                PanelGestionPenalizaciones,
                "¿Confirmas el pago de la multa de $" + monto + "?",
                "Confirmar Pago",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            boolean exito = penalizacionDAO.pagarPenalizacion(penalizacionId, socioId);
            if (exito) {
                JOptionPane.showMessageDialog(PanelGestionPenalizaciones, "¡Pago registrado exitosamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarPenalizaciones(); // Recargar la tabla para ver los cambios
            } else {
                JOptionPane.showMessageDialog(PanelGestionPenalizaciones, "Ocurrió un error al registrar el pago.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
