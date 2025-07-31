package Modelo;

import java.time.LocalDate;

/**
 * Representa un préstamo de un libro a un socio.
 * Contiene toda la información relevante sobre la transacción del préstamo.
 */
public class Prestamo {

    private int id;
    private long libroId;
    private long socioId;
    private long usuarioSistemaId;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionEstimada;
    private LocalDate fechaDevolucionReal;
    private String estadoPrestamo;

    /**
     * Constructor por defecto.
     */
    public Prestamo() {
    }

    // --- Getters y Setters para todos los campos ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getLibroId() {
        return libroId;
    }

    public void setLibroId(long libroId) {
        this.libroId = libroId;
    }

    public long getSocioId() {
        return socioId;
    }

    public void setSocioId(long socioId) {
        this.socioId = socioId;
    }

    public long getUsuarioSistemaId() {
        return usuarioSistemaId;
    }

    public void setUsuarioSistemaId(long usuarioSistemaId) {
        this.usuarioSistemaId = usuarioSistemaId;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaDevolucionEstimada() {
        return fechaDevolucionEstimada;
    }

    public void setFechaDevolucionEstimada(LocalDate fechaDevolucionEstimada) {
        this.fechaDevolucionEstimada = fechaDevolucionEstimada;
    }

    public LocalDate getFechaDevolucionReal() {
        return fechaDevolucionReal;
    }

    public void setFechaDevolucionReal(LocalDate fechaDevolucionReal) {
        this.fechaDevolucionReal = fechaDevolucionReal;
    }

    public String getEstadoPrestamo() {
        return estadoPrestamo;
    }

    public void setEstadoPrestamo(String estadoPrestamo) {
        this.estadoPrestamo = estadoPrestamo;
    }
}