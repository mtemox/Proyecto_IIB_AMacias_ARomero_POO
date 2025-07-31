package Vista;

import DAO.PrestamoDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel para gestionar la devolución de libros.
 * Permite buscar préstamos activos por cédula y procesar su devolución.
 */
public class PanelDevolucion {
    private JPanel PanelDevolucion;
    private JTextField txtBusquedaCedula;
    private JButton btnBuscarPrestamos;
    private JTable tblPrestamosActivos;
    private JButton btnProcesarDevolucion;
    private JScrollPane scrollPane;

    private DefaultTableModel tableModel;
    private PrestamoDAO prestamoDAO;

    /**
     * Constructor del panel. Inicializa el DAO, la tabla y los listeners.
     * Carga inicialmente todos los préstamos activos.
     */
    public PanelDevolucion() {
        prestamoDAO = new PrestamoDAO();
        inicializarTabla();
        configurarListeners();
        cargarPrestamos();
    }

    /**
     * Devuelve el panel principal para ser mostrado.
     * @return El JPanel de devolución.
     */
    public JPanel getPanel() {
        return PanelDevolucion;
    }

    /**
     * Configura el modelo de la tabla, sus columnas y su comportamiento.
     */
    private void inicializarTabla() {
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.addColumn("ID Préstamo");
        tableModel.addColumn("Título del Libro");
        tableModel.addColumn("Socio");
        tableModel.addColumn("Fecha Devolución Estimada");
        tableModel.addColumn("Estado");
        tableModel.addColumn("Cédula Socio");
        tblPrestamosActivos.setModel(tableModel);
        tblPrestamosActivos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Solo se puede seleccionar una fila
    }

    /**
     * Asigna los listeners a los botones de búsqueda y procesar devolución.
     */
    private void configurarListeners() {

        // Accion para el boton btnBuscarPrestamos
        btnBuscarPrestamos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarPrestamos();
            }
        });

        // Accion para el boton btnProcesarDevolucion
        btnProcesarDevolucion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                procesarDevolucion();
            }
        });

    }

    /**
     * Carga los préstamos activos en la tabla, opcionalmente filtrando por la cédula
     * ingresada en el campo de texto.
     */
    private void cargarPrestamos() {
        String cedula = txtBusquedaCedula.getText().trim();

        tableModel.setRowCount(0); // Limpiar la tabla
        List<Object[]> prestamos = prestamoDAO.buscarPrestamosActivosPorCedula(cedula);

        if (prestamos.isEmpty() && !cedula.isEmpty()) {
            JOptionPane.showMessageDialog(PanelDevolucion, "No se encontraron préstamos activos para la cédula ingresada.", "Sin Resultados", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Object[] prestamo : prestamos) {
                tableModel.addRow(prestamo);
            }
        }
    }

    /**
     * Carga los préstamos activos en la tabla, filtrandos por la cédula
     * ingresada en el campo de texto.
     */
    private void buscarPrestamos() {
        String cedula = txtBusquedaCedula.getText().trim();
        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(PanelDevolucion, "Por favor, ingrese la cédula del socio.", "Campo Vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        tableModel.setRowCount(0); // Limpiar la tabla
        List<Object[]> prestamos = prestamoDAO.buscarPrestamosActivosPorCedula(cedula);

        if (prestamos.isEmpty()) {
            JOptionPane.showMessageDialog(PanelDevolucion, "No se encontraron préstamos activos para la cédula ingresada.", "Sin Resultados", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Object[] prestamo : prestamos) {
                tableModel.addRow(prestamo);
            }
        }
    }

    /**
     * Procesa la devolución del préstamo seleccionado en la tabla.
     * Pide confirmación al usuario y llama al DAO para registrar la devolución.
     */
    private void procesarDevolucion() {
        int filaSeleccionada = tblPrestamosActivos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(PanelDevolucion, "Por favor, seleccione un préstamo de la tabla para procesar la devolución.", "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        long prestamoId = (long) tableModel.getValueAt(filaSeleccionada, 0);
        String libroTitulo = (String) tableModel.getValueAt(filaSeleccionada, 1);

        int confirmacion = JOptionPane.showConfirmDialog(
                PanelDevolucion,
                "¿Confirmas la devolución del libro '" + libroTitulo + "'?",
                "Confirmar Devolución",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            boolean exito = prestamoDAO.registrarDevolucion(prestamoId);
            if (exito) {
                JOptionPane.showMessageDialog(PanelDevolucion, "¡Devolución procesada exitosamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                // Volver a buscar para actualizar la tabla y mostrar los préstamos restantes
                buscarPrestamos();
            } else {
                JOptionPane.showMessageDialog(PanelDevolucion, "Ocurrió un error al procesar la devolución.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
