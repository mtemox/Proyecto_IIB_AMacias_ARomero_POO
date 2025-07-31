package Modelo;

import java.util.List;

/**
 * Representa un libro en el sistema de la biblioteca.
 * Contiene todos los atributos de un libro, así como campos adicionales
 * para almacenar información relacionada (como nombres de autores, etc.).
 */
public class Libro {

    private long id;
    private String isbn;
    private String titulo;
    private int anioPublicacion;
    private String portadaUrl;
    private int cantidadTotal;
    private int cantidadDisponible;

    private long editorialId;
    private long categoriaId;
    private java.util.List<String> autoresIds; // Para manejar los IDs en la edición

    // Estos campos se llenarán con los JOINs de la base de datos
    private String autores; // Puede tener varios autores, los unimos en un String
    private String categoria;
    private String descripcionCategoria;
    private String editorial;

    // Constructor vacío
    public Libro() {
    }

    // Getters y Setters para todos los campos

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getAnioPublicacion() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(int anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }

    public int getCantidadTotal() {
        return cantidadTotal;
    }

    public void setCantidadTotal(int cantidadTotal) {
        this.cantidadTotal = cantidadTotal;
    }

    public String getDescripcionCategoria() {
        return descripcionCategoria;
    }

    public void setDescripcionCategoria(String descripcionCategoria) {
        this.descripcionCategoria = descripcionCategoria;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPortadaUrl() {
        return portadaUrl;
    }

    public void setPortadaUrl(String portadaUrl) {
        this.portadaUrl = portadaUrl;
    }

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(int cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public String getAutores() {
        return autores;
    }

    public void setAutores(String autores) {
        this.autores = autores;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public long getEditorialId() {
        return editorialId;
    }

    public void setEditorialId(long editorialId) {
        this.editorialId = editorialId;
    }

    public long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public List<String> getAutoresIds() {
        return autoresIds;
    }

    public void setAutoresIds(List<String> autoresIds) {
        this.autoresIds = autoresIds;
    }
}