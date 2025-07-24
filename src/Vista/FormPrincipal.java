package Vista;

import Utils.SessionManager;
import Modelo.UsuarioSistema;
import Modelo.Libro;

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

    java.net.URL imageUrl = getClass().getResource("/resources/logo.png");

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

        if (imageUrl != null) {
            // 1. Crea el ImageIcon original
            ImageIcon originalIcon = new ImageIcon(imageUrl);
            // 2. Define el nuevo tamaño que deseas para la imagen
            int nuevoAncho = 75; // Por ejemplo, 100 píxeles de ancho
            int nuevoAlto = 75;  // Por ejemplo, 100 píxeles de alto
            // 3. Obtiene la 'Image' del ImageIcon original y la escala
            //    Image.SCALE_SMOOTH le da mayor calidad al redimensionamiento.
            java.awt.Image imagenOriginal = originalIcon.getImage();
            java.awt.Image imagenRedimensionada = imagenOriginal.getScaledInstance(nuevoAncho, nuevoAlto, java.awt.Image.SCALE_SMOOTH);
            // 4. Crea un nuevo ImageIcon a partir de la imagen ya redimensionada
            ImageIcon iconoRedimensionado = new ImageIcon(imagenRedimensionada);
            // 5. Asigna el icono final (ya con el tamaño correcto) a tu JLabel
            lblLogo.setIcon(iconoRedimensionado);
        } else {
            System.err.println("Error: No se encontró la imagen en la ruta especificada.");
        }

        setSize(1500, 720);
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
