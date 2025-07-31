package Modelo;

/**
 * Representa un usuario del sistema (empleado), que puede ser
 * un 'ADMINISTRADOR' o un 'BIBLIOTECARIO'.
 */
public class UsuarioSistema {
    private long id;
    private String username;
    private String password; // Solo para guardar la contraseña encriptada de la BD
    private String rol;
    private String estado;

    /**
     * Constructor por defecto.
     */
    public UsuarioSistema() {}

    /**
     * Constructor para crear un nuevo usuario con todos sus datos.
     * @param username Nombre de usuario único.
     * @param password Contraseña del usuario.
     * @param rol Rol del usuario ('ADMINISTRADOR' o 'BIBLIOTECARIO').
     * @param estado Estado del usuario ('ACTIVO' o 'INACTIVO').
     */
    public UsuarioSistema(String username, String password, String rol, String estado) {
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.estado = estado;
    }

    // --- CONSTRUCTORES, GETTERS Y SETTERS ---
    // getters y setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}