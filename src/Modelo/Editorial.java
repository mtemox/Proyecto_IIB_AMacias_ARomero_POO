package Modelo;

public class Editorial {
    private long id;
    private String nombre;

    // Constructor, getters y setters...
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return nombre; // CLAVE para JComboBox
    }
}