package Utils;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Clase de utilidad para exportar datos de una JTable a diferentes formatos de archivo.
 * Proporciona métodos estáticos para exportar a PDF y CSV.
 */
public class ReporteExporter {

    /**
     * Exporta los datos de una JTable a un archivo PDF.
     * Abre un diálogo para que el usuario elija dónde guardar el archivo.
     * @param table La JTable que contiene los datos a exportar.
     * @param tituloReporte El título que se mostrará en la cabecera del PDF.
     */
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

    // --- METODO PARA REEMPLAZAR EL DE EXCEL ---
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