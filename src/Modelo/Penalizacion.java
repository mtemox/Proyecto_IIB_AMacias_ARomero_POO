package Modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Penalizacion {
    private long id;
    private long prestamoId;
    private long socioId;
    private BigDecimal monto;
    private LocalDate fechaGeneracion;
    private LocalDate fechaPago;
    private String estadoPenalizacion;
    private String observaciones;

    // --- CONSTRUCTORES, GETTERS Y SETTERS ---

    public Penalizacion() {}

    // Getters y Setters para todos los campos...
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getPrestamoId() { return prestamoId; }
    public void setPrestamoId(long prestamoId) { this.prestamoId = prestamoId; }
    public long getSocioId() { return socioId; }
    public void setSocioId(long socioId) { this.socioId = socioId; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public LocalDate getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDate fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }
    public String getEstadoPenalizacion() { return estadoPenalizacion; }
    public void setEstadoPenalizacion(String estadoPenalizacion) { this.estadoPenalizacion = estadoPenalizacion; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}