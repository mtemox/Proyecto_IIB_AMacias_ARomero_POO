package Utils;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class ReporteExporter {

    public static void exportarAPdf(JTable table, String tituloReporte) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar reporte como PDF");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos PDF", "pdf"));

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getAbsolutePath().endsWith(".pdf")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
            }

            try (FileOutputStream fos = new FileOutputStream(fileToSave)) {
                Document document = new Document();
                PdfWriter.getInstance(document, fos);
                document.open();

                // Título
                Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
                Paragraph titulo = new Paragraph(tituloReporte, fontTitulo);
                titulo.setAlignment(Element.ALIGN_CENTER);
                document.add(titulo);
                document.add(new Paragraph(" ")); // Espacio

                // Tabla
                PdfPTable pdfTable = new PdfPTable(table.getColumnCount());
                pdfTable.setWidthPercentage(100);

                // Encabezados
                Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
                for (int i = 0; i < table.getColumnCount(); i++) {
                    pdfTable.addCell(new Phrase(table.getColumnName(i), fontHeader));
                }

                // Datos
                for (int rows = 0; rows < table.getRowCount(); rows++) {
                    for (int cols = 0; cols < table.getColumnCount(); cols++) {
                        Object value = table.getValueAt(rows, cols);
                        pdfTable.addCell(value != null ? value.toString() : "");
                    }
                }
                document.add(pdfTable);
                document.close();
                JOptionPane.showMessageDialog(null, "Reporte PDF guardado exitosamente en:\n" + fileToSave.getAbsolutePath());
            } catch (IOException | DocumentException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al guardar el reporte PDF.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void exportarAExcel(JTable table, String nombreHoja) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar reporte como Excel");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos Excel (*.xlsx)", "xlsx"));

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getAbsolutePath().endsWith(".xlsx")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".xlsx");
            }

            try (Workbook workbook = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(fileToSave)) {
                Sheet sheet = workbook.createSheet(nombreHoja);
                TableModel model = table.getModel();

                // Encabezados
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < model.getColumnCount(); i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(model.getColumnName(i));
                }

                // Datos
                for (int i = 0; i < model.getRowCount(); i++) {
                    Row row = sheet.createRow(i + 1);
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        Cell cell = row.createCell(j);
                        Object value = model.getValueAt(i, j);
                        if (value instanceof Number) {
                            cell.setCellValue(((Number) value).doubleValue());
                        } else {
                            cell.setCellValue(value != null ? value.toString() : "");
                        }
                    }
                }
                workbook.write(fos);
                JOptionPane.showMessageDialog(null, "Reporte Excel guardado exitosamente en:\n" + fileToSave.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al guardar el reporte Excel.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // --- NUEVO MÉTODO PARA REEMPLAZAR EL DE EXCEL ---
    /**
     * Exporta los datos de una JTable a un archivo CSV.
     * Este método no requiere librerías externas.
     *
     * @param table La tabla con los datos a exportar.
     */
    public static void exportarACSV(JTable table) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar reporte como CSV (para Excel)");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos CSV", "csv"));

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            // Asegurarse de que el archivo tenga la extensión .csv
            if (!fileToSave.getAbsolutePath().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }

            try (BufferedWriter bfw = new BufferedWriter(new FileWriter(fileToSave))) {
                TableModel model = table.getModel();

                // Escribir los encabezados de la tabla
                for (int i = 0; i < model.getColumnCount(); i++) {
                    bfw.write(model.getColumnName(i) + (i == model.getColumnCount() - 1 ? "" : ","));
                }
                bfw.newLine();

                // Escribir los datos de la tabla
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        Object value = model.getValueAt(i, j);
                        String cellValue = (value != null) ? value.toString() : "";

                        // Si el valor contiene una coma, lo encerramos en comillas dobles
                        if (cellValue.contains(",")) {
                            cellValue = "\"" + cellValue + "\"";
                        }

                        bfw.write(cellValue + (j == model.getColumnCount() - 1 ? "" : ","));
                    }
                    bfw.newLine();
                }

                JOptionPane.showMessageDialog(null, "Reporte CSV guardado exitosamente.\nPuedes abrir este archivo con Excel.");

            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al guardar el archivo CSV.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}