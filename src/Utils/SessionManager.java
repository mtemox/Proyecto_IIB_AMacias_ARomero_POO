package Utils;

import Modelo.UsuarioSistema;

/**
 * Gestiona la sesión del usuario en toda la aplicación.
 * Utiliza el patrón Singleton para asegurar que solo haya una instancia
 * que mantenga la información del usuario que ha iniciado sesión.
 */
public final class SessionManager {

    private static SessionManager instance;
    private UsuarioSistema usuarioLogueado;

    /**
     * Constructor privado para evitar la instanciación.
     */
    private SessionManager() {}

    /**
     * Obtiene la única instancia del SessionManager.
     * @return La instancia Singleton de SessionManager.
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Obtiene el usuario que ha iniciado sesión.
     * @return El objeto UsuarioSistema del usuario logueado, o null si no hay sesión.
     */
    public UsuarioSistema getUsuarioLogueado() {
        return usuarioLogueado;
    }

    /**
     * Establece el usuario que ha iniciado sesión.
     * @param usuarioLogueado El objeto UsuarioSistema a guardar en la sesión.
     */
    public void setUsuarioLogueado(UsuarioSistema usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
    }

    /**
     * Cierra la sesión actual, eliminando la información del usuario.
     */
    public void cerrarSesion() {
        this.usuarioLogueado = null;
    }
}