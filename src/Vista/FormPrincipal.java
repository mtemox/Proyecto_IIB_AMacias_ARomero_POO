package Vista;

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
        setSize(1500, 720);
        //setExtendedState(JFrame.MAXIMIZED_BOTH); // Para iniciar maximizado
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        PanelBienvenida panelBienvenida = new PanelBienvenida();
        mostrarPanel(panelBienvenida.getPanelBienvenida());

        btnGestionLibros.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PanelGestionLibros panel = new PanelGestionLibros();
                mostrarPanel(panel.getPanel()); // Usas tu metodo para cambiar de panel
            }
        });
        btnNuevoPrestamo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PanelNuevoPrestamo panelNuevoPrestamo = new PanelNuevoPrestamo();
                mostrarPanel(panelNuevoPrestamo.getPanelNuevoPrestamo());
            }
        });
    }

}
