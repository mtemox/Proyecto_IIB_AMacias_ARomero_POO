package Modelo;

import java.time.LocalDate;

public class Socio {
    private int id;
    private String cedula;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private LocalDate fechaRegistro;
    private String estadoSocio;
    // Agrega un campo para la contraseña, solo para el registro
    private String password;

    // --- CONSTRUCTORES, GETTERS Y SETTERS ---
    // Crea un constructor para el registro
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


    // Getter y setter de los atributos
    public int getId() {
        return id;
    }

    public void setId(int id) {
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