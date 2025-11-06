package pe.adcomp.suma.sunat.params.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para CambioSunat
 */
public class CambioSunatDTO {

    private LocalDate fecha;
    private BigDecimal compra;
    private BigDecimal venta;

    // Constructores
    public CambioSunatDTO() {
    }

    public CambioSunatDTO(LocalDate fecha, BigDecimal compra, BigDecimal venta) {
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
        return "CambioSunatDTO{" +
                "fecha=" + fecha +
                ", compra=" + compra +
                ", venta=" + venta +
                '}';
    }
}
