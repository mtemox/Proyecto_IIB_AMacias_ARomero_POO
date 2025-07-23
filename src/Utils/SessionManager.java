package Utils;

import Modelo.UsuarioSistema;

public final class SessionManager {

    private static SessionManager instance;
    private UsuarioSistema usuarioLogueado;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public UsuarioSistema getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public void setUsuarioLogueado(UsuarioSistema usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
    }

    public void cerrarSesion() {
        this.usuarioLogueado = null;
    }
}