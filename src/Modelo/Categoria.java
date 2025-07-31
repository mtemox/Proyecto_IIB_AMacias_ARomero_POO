package Modelo;

import java.util.Objects;

/**
 * Representa una categoría o género de un libro.
 */
public class Categoria {
    private long id;
    private String nombre;
    private String descripcion;

    // Constructor, getters y setters...
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Devuelve el nombre de la categoría.
     * Esencial para que componentes como JComboBox muestren el texto correcto.
     * @return El nombre de la categoría.
     */
    @Override
    public String toString() {
        return nombre; // Esto es CLAVE para que JComboBox muestre el nombre
    }

    /**
     * Compara dos objetos Categoria basándose en su ID.
     * Crucial para que `setSelectedItem()` en JComboBox funcione correctamente.
     * @param o El objeto a comparar.
     * @return `true` si los IDs son iguales.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categoria categoria = (Categoria) o;
        return id == categoria.id;
    }

    /**
     * Genera un código hash basado en el ID de la categoría.
     * @return El código hash del objeto.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}