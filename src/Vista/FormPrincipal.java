package Vista;

import Utils.SessionManager;
import Modelo.UsuarioSistema;
import Modelo.Libro;
import Utils.ImageUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FormPrincipal extends JFrame {
    private JPanel panelPrincipal;
    private JPanel panelNavegacion;
    private JButton btnGestionLibros;
    private JButton btnGestionSocios;
    private JButton btnNuevoPrestamo;
    private JButton btnRegistrarDevolucion;
    private JButton btnGestionUsuarios;
    private JButton btnVerReportes;
    private JButton btnCerrarSesion;
    private JPanel panelContenido;
    private JLabel lblLogo;
    private JButton btnGestionPenalizaciones;
    private JLabel lblLogoLibros;
    private JLabel lblLogoSocios;
    private JLabel lblLogoPrestamos;
    private JLabel lblLogoDevoluciones;
    private JLabel lblLogoPenalizaciones;
    private JLabel lblLogoUsuarios;
    private JLabel lblLogoReportes;
    private JLabel lblLogoSesion;
    private JLabel titulo;


    // Metodo para mostrar los paneles
    private void mostrarPanel(JPanel panel) {
        // Establece el tamaño y la posición del panel que se va a mostrar.
        // Esto asegura que ocupe todo el espacio del panel contenedor.
        panel.setSize(panelContenido.getWidth(), panelContenido.getHeight());
        panel.setLocation(0, 0);

        // Limpia el panel de contenido, quitando cualquier cosa que estuviera antes.
        panelContenido.removeAll();

        // Añade el nuevo panel que recibimos como parámetro.
        // Lo añadimos en la posición "CENTER" del BorderLayout para que se expanda.
        panelContenido.add(panel, java.awt.BorderLayout.CENTER);

        // Estos dos comandos son el truco mágico:
        // revalidate() le dice al panel que necesita recalcular su diseño.
        panelContenido.revalidate();
        // repaint() le dice al panel que necesita redibujarse en la pantalla.
        panelContenido.repaint();
    }

    public FormPrincipal() {
            setTitle("SIBIBLI");
            setContentPane(panelPrincipal);

        ImageUtils.loadImage(lblLogo, "/resources/logo6.png", 200, 200);

        // Iconos para los botones de navegacion
        ImageUtils.loadImage(lblLogoLibros, "/resources/libros.png", 30, 30);
        ImageUtils.loadImage(lblLogoSocios, "/resources/socios.png", 30, 30);
        ImageUtils.loadImage(lblLogoPrestamos, "/resources/prestamo.png", 30, 30);
        ImageUtils.loadImage(lblLogoDevoluciones, "/resources/devolucion.png", 30, 30);
        ImageUtils.loadImage(lblLogoPenalizaciones, "/resources/penalizacion.png", 30, 30);
        ImageUtils.loadImage(lblLogoUsuarios, "/resources/usuario.png", 30, 30);
        ImageUtils.loadImage(lblLogoReportes, "/resources/analitica.png", 30, 30);
        ImageUtils.loadImage(lblLogoSesion, "/resources/acceso.png", 30, 30);

        setSize(1550, 720);
        //setExtendedState(JFrame.MAXIMIZED_BOTH); // Para iniciar maximizado
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        configurarSegunRol();

        PanelBienvenida panelBienvenida = new PanelBienvenida();
        mostrarPanel(panelBienvenida.getPanelBienvenida());

        // Accion para el boton btnGestionLibros
        btnGestionLibros.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PanelGestionLibros panel = new PanelGestionLibros();
                mostrarPanel(panel.getPanel()); // Usas tu metodo para cambiar de panel
            }
        });

        // Accion para el boton btnGestionLibros
        btnNuevoPrestamo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PanelNuevoPrestamo panelNuevoPrestamo = new PanelNuevoPrestamo();
                mostrarPanel(panelNuevoPrestamo.getPanelNuevoPrestamo());
            }
        });

        // Accion para el boton btnGestionLibros
        btnGestionSocios.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PanelGestionSocios panel = new PanelGestionSocios();
                mostrarPanel(panel.getPanel()); // Usas tu metodo para cambiar de panel
            }

        // Accion para el boton btnCerrarSesion
        });
        btnCerrarSesion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int confirmacion = JOptionPane.showConfirmDialog(
                        FormPrincipal.this,
                        "¿Estás seguro de que quieres cerrar la sesión?",
                        "Confirmar Cierre de Sesión",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirmacion == JOptionPane.YES_OPTION) {
                    SessionManager.getInstance().cerrarSesion(); // Limpiar la sesión
                    new FormLogin(); // Volver a la ventana de login
                    dispose(); // Cerrar la ventana principal
                }

            }
        });

        // Accion para el boton btnGestionUsuarios
        btnGestionUsuarios.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PanelGestionUsuarios panel = new PanelGestionUsuarios();
                mostrarPanel(panel.getPanel());
            }
        });

        // Accion para el boton btnRegistrarDevolucion
        btnRegistrarDevolucion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PanelDevolucion panel = new PanelDevolucion();
                mostrarPanel(panel.getPanel());
            }
        });

        // Accion para el boton btnGestionPenalizaciones
        btnGestionPenalizaciones.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PanelGestionPenalizaciones panel = new PanelGestionPenalizaciones();
                mostrarPanel(panel.getPanel());
            }
        });

        // Accion para el boton btnVerReportes
        btnVerReportes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PanelReportes panelReportes = new PanelReportes();
                mostrarPanel(panelReportes.getPanel());
            }
        });
    }

    // --- METODO ---
    private void configurarSegunRol() {
        UsuarioSistema usuario = SessionManager.getInstance().getUsuarioLogueado();
        if (usuario != null) {
            String rol = usuario.getRol();

            // Por defecto, el bibliotecario puede hacer casi todo
            // El administrador tiene acceso a un panel extra: Gestión de Usuarios.
            if ("BIBLIOTECARIO".equals(rol)) {
                btnGestionUsuarios.setEnabled(false); // Deshabilitar para el bibliotecario
                btnGestionUsuarios.setToolTipText("Acceso solo para Administradores");
            } else if ("ADMINISTRADOR".equals(rol)) {
                btnGestionUsuarios.setEnabled(true); // Habilitado para el admin
            }
        } else {
            // Si por alguna razón no hay usuario en sesión, volver al login
            JOptionPane.showMessageDialog(this, "Error de sesión. Por favor, inicie sesión de nuevo.", "Error", JOptionPane.ERROR_MESSAGE);
            new FormLogin();
            dispose();
        }
    }

    /**
     * Navega al panel de nuevo préstamo, precargando un libro.
     * @param libro El libro que se va a prestar.
     */

    public void navegarAPrestamosConLibro(Libro libro) {
        PanelNuevoPrestamo panel = new PanelNuevoPrestamo(libro);
        mostrarPanel(panel.getPanelNuevoPrestamo());
    }

}
