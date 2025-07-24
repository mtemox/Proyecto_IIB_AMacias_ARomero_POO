package Modelo;

public class Autor {
    private long id;
    private String nombreCompleto;

    // Constructor, getters y setters...
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    @Override
    public String toString() {
        return nombreCompleto; // CLAVE para JList
    }
}