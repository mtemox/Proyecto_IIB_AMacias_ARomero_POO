package Modelo;

import java.util.Objects;

/**
 * Representa una editorial de libros.
 */
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

    /**
     * Devuelve el nombre de la editorial.
     * Esencial para que JComboBox muestre el texto correcto.
     * @return El nombre de la editorial.
     */
    @Override
    public String toString() {
        return nombre; // CLAVE para JComboBox
    }

    /**
     * Compara dos objetos Editorial basándose en su ID.
     * Crucial para que `setSelectedItem()` funcione correctamente.
     * @param o El objeto a comparar.
     * @return `true` si los IDs son iguales.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Editorial editorial = (Editorial) o;
        return id == editorial.id;
    }

    /**
     * Genera un código hash basado en el ID de la editorial.
     * @return El código hash del objeto.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}