package Modelo;

import java.util.Objects;

public class Editorial {
    private long id;
    private String nombre;
    private String paisOrigen;

    // Constructor, getters y setters...
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPaisOrigen() {
        return paisOrigen;
    }

    public void setPaisOrigen(String paisOrigen) {
        this.paisOrigen = paisOrigen;
    }

    @Override
    public String toString() {
        return nombre; // CLAVE para JComboBox
    }

    // Es crucial para que setSelectedItem() funcione correctamente
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Editorial editorial = (Editorial) o;
        return id == editorial.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}