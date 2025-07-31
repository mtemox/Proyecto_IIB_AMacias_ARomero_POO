package Modelo;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Representa a un autor de un libro.
 * Contiene información personal del autor y métodos para facilitar su uso en la UI.
 */
public class Autor {
    private long id;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private String nacionalidad;
    private String nombreCompleto; // Para mostrar en la UI

    // Constructor, getters y setters...
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

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

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    /**
     * Devuelve el nombre completo del autor.
     * Esencial para que componentes como JList muestren el texto correcto.
     * @return El nombre completo del autor.
     */
    @Override
    public String toString() {
        // Esto es CLAVE para que JList muestre el nombre_completo
        return getNombreCompleto();
    }

    /**
     * Compara dos objetos Autor basándose en su ID.
     * Crucial para que métodos como `setSelectedValue()` en JList funcionen correctamente.
     * @param o El objeto a comparar.
     * @return `true` si los IDs son iguales, `false` en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Autor autor = (Autor) o;
        return id == autor.id;
    }

    /**
     * Genera un código hash basado en el ID del autor.
     * @return El código hash del objeto.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}