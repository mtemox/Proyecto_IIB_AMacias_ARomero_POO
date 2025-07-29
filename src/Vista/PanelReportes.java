package Vista;

import DAO.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import Utils.ReporteExporter; // Importa la nueva clase
import javax.swing.table.TableModel;

public class PanelReportes {
    private JPanel PanelReportes;
    private JComboBox<String> cmbTipoReporte;
    private JTextField txtFechaDesde;
    private JTextField txtFechaHasta;
    private JButton btnGenerarReporte;
    private JTable tblReporte;
    private JButton btnExportarPdf;
    private JButton btnExportarExcel;
    private JLabel lblFechaDesde;
    private JLabel lblFechaHasta;

    private LibroDAO libroDAO;
    private SocioDAO socioDAO;
    private PenalizacionDAO penalizacionDAO;

    public PanelReportes() {
        // Inicializamos todos los DAOs que vamos a necesitar
        this.libroDAO = new LibroDAO();
        this.socioDAO = new SocioDAO();
        this.penalizacionDAO = new PenalizacionDAO();

        configurarPanel();
        configurarListeners();
    }

    public JPanel getPanel() {
        return PanelReportes;
    }

    private void configurarPanel() {
        // Llenar el ComboBox con los reportes disponibles
        cmbTipoReporte.addItem("Seleccione un reporte...");
        cmbTipoReporte.addItem("Libros Más Prestados");
        cmbTipoReporte.addItem("Socios Más Activos");
        cmbTipoReporte.addItem("Ingresos por Penalizaciones");

        // Ocultar los campos de fecha al inicio
        setVisibilidadFechas(false);
    }

    private void configurarListeners() {
        // Mostrar/ocultar campos de fecha según el reporte seleccionado
        cmbTipoReporte.addActionListener(e -> {
            String seleccion = (String) cmbTipoReporte.getSelectedItem();
            if ("Ingresos por Penalizaciones".equals(seleccion)) {
                setVisibilidadFechas(true);
            } else {
                setVisibilidadFechas(false);
            }
        });

        // Accion para el botón btnGenerarReporte
        btnGenerarReporte.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generarReporte();
            }
        });

        // Accion para el botón btnExportarPdf
        btnExportarPdf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                TableModel model = tblReporte.getModel();
                if (model.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(PanelReportes, "No hay datos en la tabla para exportar.", "Tabla Vacía", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String titulo = (String) cmbTipoReporte.getSelectedItem();
                ReporteExporter.exportarAPdf(tblReporte, "Reporte: " + titulo);

            }
        });

        // Accion para el botón btnExportarExcel
        btnExportarExcel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                TableModel model = tblReporte.getModel();
                if (model.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(PanelReportes, "No hay datos en la tabla para exportar.", "Tabla Vacía", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                // Llama al nuevo método que no necesita librerías
                ReporteExporter.exportarACSV(tblReporte);

            }
        });
    }

    private void generarReporte() {
        String tipoReporte = (String) cmbTipoReporte.getSelectedItem();

        switch (tipoReporte) {
            case "Libros Más Prestados":
                generarReporteLibros();
                break;
            case "Socios Más Activos":
                generarReporteSocios();
                break;
            case "Ingresos por Penalizaciones":
                generarReporteIngresos();
                break;
            default:
                JOptionPane.showMessageDialog(PanelReportes, "Por favor, seleccione un tipo de reporte.", "Aviso", JOptionPane.WARNING_MESSAGE);
                break;
        }
    }

    private void generarReporteLibros() {
        String[] columnas = {"Título del Libro", "Cantidad de Préstamos"};
        List<Object[]> datos = libroDAO.getReporteLibrosMasPrestados();
        mostrarReporteEnTabla(columnas, datos);
    }

    private void generarReporteSocios() {
        String[] columnas = {"Nombre del Socio", "Cantidad de Préstamos"};
        List<Object[]> datos = socioDAO.getReporteSociosMasActivos();
        mostrarReporteEnTabla(columnas, datos);
    }

    private void generarReporteIngresos() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate fechaInicio = LocalDate.parse(txtFechaDesde.getText(), formatter);
            LocalDate fechaFin = LocalDate.parse(txtFechaHasta.getText(), formatter);

            if (fechaFin.isBefore(fechaInicio)) {
                JOptionPane.showMessageDialog(PanelReportes, "La fecha final no puede ser anterior a la fecha de inicio.", "Error de Fechas", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String[] columnas = {"Total de Ingresos ($)", "Cantidad de Multas Pagadas"};
            Object[] resultado = penalizacionDAO.getReporteIngresosPorFechas(fechaInicio, fechaFin);

            // Convertimos el resultado único a una lista para reutilizar el método de la tabla
            List<Object[]> datos = new java.util.ArrayList<>();
            if (resultado != null) {
                datos.add(resultado);
            }
            mostrarReporteEnTabla(columnas, datos);

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(PanelReportes, "Formato de fecha inválido. Use AAAA-MM-DD.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Metodo genérico para limpiar la tabla y mostrar nuevos datos de un reporte.
     * @param nombresColumnas Los títulos para las columnas de la tabla.
     * @param datos La lista de filas de datos para mostrar.
     */
    private void mostrarReporteEnTabla(String[] nombresColumnas, List<Object[]> datos) {
        DefaultTableModel model = new DefaultTableModel(nombresColumnas, 0);
        tblReporte.setModel(model);

        if (datos == null || datos.isEmpty()) {
            JOptionPane.showMessageDialog(PanelReportes, "No se encontraron datos para el reporte seleccionado.", "Sin Resultados", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Object[] fila : datos) {
                model.addRow(fila);
            }
        }
    }

    private void setVisibilidadFechas(boolean visible) {
        lblFechaDesde.setVisible(visible);
        txtFechaDesde.setVisible(visible);
        lblFechaHasta.setVisible(visible);
        txtFechaHasta.setVisible(visible);
    }
}