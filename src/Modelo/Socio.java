package Modelo;

import java.time.LocalDate;

/**
 * Representa a un socio o cliente de la biblioteca.
 */
public class Socio {
    private long id;
    private String cedula;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private LocalDate fechaRegistro;
    private String estadoSocio;
    // Agrega un campo para la contraseña, solo para el registro
    private String password;

    /**
     * Constructor para crear un nuevo socio, ideal para el formulario de registro.
     * Establece la fecha de registro actual y el estado como 'ACTIVO'.
     * @param cedula Cédula del socio.
     * @param nombre Nombre del socio.
     * @param apellido Apellido del socio.
     * @param email Correo electrónico del socio.
     * @param telefono Teléfono del socio.
     * @param password Contraseña para el socio (en un sistema real, esto sería para un portal de socios).
     */
    public Socio(String cedula, String nombre, String apellido, String email, String telefono, String password) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.password = password;
        this.fechaRegistro = LocalDate.now(); // Se establece la fecha actual
        this.estadoSocio = "ACTIVO";
    }

    /**
     * Constructor vacío.
     */
    public Socio() {}

    // Getter y setter de los atributos
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getEstadoSocio() {
        return estadoSocio;
    }

    public void setEstadoSocio(String estadoSocio) {
        this.estadoSocio = estadoSocio;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}