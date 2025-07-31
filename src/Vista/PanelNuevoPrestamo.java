package Vista;

import DAO.LibroDAO;
import DAO.PrestamoDAO;
import DAO.SocioDAO;
import Modelo.Libro;
import Modelo.Prestamo;
import Modelo.Socio;
import Utils.SessionManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

/**
 * Panel para el registro de un nuevo préstamo.
 * Permite buscar un socio por cédula y un libro por ISBN para crear el préstamo.
 */
public class PanelNuevoPrestamo extends JFrame{
    private JPanel PanelNuevoPrestamo;
    private JTextField txtBusquedaCedulaSocio;
    private JButton btnBuscarSocioPrestamo;
    private JLabel lblInfoSocio;
    private JTextField txtBusquedaIsbnLibro;
    private JButton btnBuscarLibroPrestamo;
    private JLabel lblInfoLibro;
    private JLabel lblFechaDevolucion;
    private JButton btnConfirmarPrestamo;

    // Variables para guardar los objetos encontrados
    private Socio socioSeleccionado = null;
    private Libro libroSeleccionado = null;

    /**
     * Devuelve el panel principal para ser mostrado.
     * @return El JPanel para registrar un nuevo préstamo.
     */
    public JPanel getPanelNuevoPrestamo() {
        return PanelNuevoPrestamo;
    }

    /**
     * Constructor por defecto. Inicializa los listeners y la fecha de devolución.
     */
    public PanelNuevoPrestamo() {

        // Establecer la fecha de devolución estimada (ej. 15 días desde hoy)
        LocalDate fechaDevolucion = LocalDate.now().plusDays(15);
        lblFechaDevolucion.setText("Fecha de devolución estimada: " + fechaDevolucion);
        btnConfirmarPrestamo.setEnabled(false); // Deshabilitado hasta que se confirme socio y libro

        // Accion para el boton btnBuscarSocioPrestamo
        btnBuscarSocioPrestamo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarSocio();
            }
        });

        // Accion para el boton btnBuscarLibroPrestamo
        btnBuscarLibroPrestamo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarLibro();
            }
        });

        // Accion para el boton btnConfirmarPrestamo
        btnConfirmarPrestamo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmarPrestamo();
            }
        });

    }

    /**
     * Busca un socio por la cédula ingresada y actualiza la UI con su información.
     */
    private void buscarSocio() {
        String cedula = txtBusquedaCedulaSocio.getText().trim();
        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(PanelNuevoPrestamo, "Por favor, ingrese una cédula.", "Campo Vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SocioDAO socioDAO = new SocioDAO();
        socioSeleccionado = socioDAO.buscarPorCedula(cedula);

        if (socioSeleccionado != null) {
            lblInfoSocio.setText("Socio: " + socioSeleccionado.getNombre() + " " + socioSeleccionado.getApellido());
        } else {
            lblInfoSocio.setText("Socio no encontrado o inactivo.");
            socioSeleccionado = null; // Limpiar selección
        }
        validarEstadoPrestamo();
    }

    /**
     * Busca un libro por el ISBN ingresado y actualiza la UI con su información.
     */
    private void buscarLibro() {
        String isbn = txtBusquedaIsbnLibro.getText().trim();
        if (isbn.isEmpty()) {
            JOptionPane.showMessageDialog(PanelNuevoPrestamo, "Por favor, ingrese un ISBN.", "Campo Vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LibroDAO libroDAO = new LibroDAO();
        libroSeleccionado = libroDAO.buscarPorIsbn(isbn);

        if (libroSeleccionado != null) {
            lblInfoLibro.setText("Libro: " + libroSeleccionado.getTitulo() + " (Disponibles: " + libroSeleccionado.getCantidadDisponible() + ")");
        } else {
            lblInfoLibro.setText("Libro no encontrado o sin ejemplares disponibles.");
            libroSeleccionado = null; // Limpiar selección
        }
        validarEstadoPrestamo();
    }

    /**
     * Habilita el botón de confirmar préstamo solo si se ha encontrado un socio y un libro válidos.
     */
    private void validarEstadoPrestamo() {
        // Habilita el botón de confirmar solo si se ha seleccionado un socio Y un libro válidos
        if (socioSeleccionado != null && libroSeleccionado != null) {
            btnConfirmarPrestamo.setEnabled(true);
        } else {
            btnConfirmarPrestamo.setEnabled(false);
        }
    }

    /**
     * Confirma y registra el préstamo en la base de datos con los datos seleccionados.
     */
    private void confirmarPrestamo() {
        // Crear el objeto Prestamo
        Prestamo nuevoPrestamo = new Prestamo();
        nuevoPrestamo.setSocioId(socioSeleccionado.getId());
        nuevoPrestamo.setLibroId(libroSeleccionado.getId());
        nuevoPrestamo.setFechaPrestamo(LocalDate.now());
        nuevoPrestamo.setFechaDevolucionEstimada(LocalDate.now().plusDays(15));


        //nuevoPrestamo.setUsuarioSistemaId(1); // Es el ID del usuario que ha iniciado sesión

        // --- LÍNEA MODIFICADA ---
        // Obtener el ID del usuario que ha iniciado sesión desde el SessionManager
        nuevoPrestamo.setUsuarioSistemaId(SessionManager.getInstance().getUsuarioLogueado().getId());
        // -

        // Llamar al DAO para registrarlo
        PrestamoDAO prestamoDAO = new PrestamoDAO();
        boolean exito = prestamoDAO.registrarPrestamo(nuevoPrestamo);

        if (exito) {
            JOptionPane.showMessageDialog(PanelNuevoPrestamo, "¡Préstamo registrado exitosamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            // Limpiar formulario para el siguiente préstamo
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(PanelNuevoPrestamo, "Ocurrió un error al registrar el préstamo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Limpia todos los campos del formulario para preparar un nuevo registro de préstamo.
     */
    private void limpiarFormulario() {
        txtBusquedaCedulaSocio.setText("");
        txtBusquedaIsbnLibro.setText("");
        lblInfoSocio.setText("Información del socio...");
        lblInfoLibro.setText("Información del libro...");
        socioSeleccionado = null;
        libroSeleccionado = null;
        btnConfirmarPrestamo.setEnabled(false);
    }

    // Getter para que el FormPrincipal pueda mostrar este panel
    public JPanel getPanel() {
        return PanelNuevoPrestamo;
    }

    /**
     * Constructor para iniciar el panel con un libro ya seleccionado.
     * @param libro El libro a prestar.
     */
    public PanelNuevoPrestamo(Libro libro) {
        this(); // Llama al constructor por defecto para inicializar todo
        this.libroSeleccionado = libro;
        txtBusquedaIsbnLibro.setText(libro.getIsbn());
        // Actualizamos la UI con la info del libro
        lblInfoLibro.setText("<html><b>Libro:</b> " + libro.getTitulo() + "<br><b>Disponibles:</b> " + libro.getCantidadDisponible() + "</html>");
        validarEstadoPrestamo();
    }

}
