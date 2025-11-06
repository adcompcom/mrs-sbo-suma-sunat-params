package pe.adcomp.suma.sunat.params.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad que mapea la tabla cambio_sunat de la base de datos
 */
@Entity
@Table(name = "cambio_sunat")
public class CambioSunat {

    @Id
    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "compra", precision = 38, scale = 2)
    private BigDecimal compra;

    @Column(name = "venta", precision = 38, scale = 2)
    private BigDecimal venta;

    // Constructores
    public CambioSunat() {
    }

    public CambioSunat(LocalDate fecha, BigDecimal compra, BigDecimal venta) {
        this.fecha = fecha;
        this.compra = compra;
        this.venta = venta;
    }

    // Getters y Setters
    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getCompra() {
        return compra;
    }

    public void setCompra(BigDecimal compra) {
        this.compra = compra;
    }

    public BigDecimal getVenta() {
        return venta;
    }

    public void setVenta(BigDecimal venta) {
        this.venta = venta;
    }

    @Override
    public String toString() {
        return "CambioSunat{" +
                "fecha=" + fecha +
                ", compra=" + compra +
                ", venta=" + venta +
                '}';
    }
}
