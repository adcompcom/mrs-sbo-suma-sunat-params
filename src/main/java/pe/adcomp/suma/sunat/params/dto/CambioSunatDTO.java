package pe.adcomp.suma.sunat.params.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Tipo de cambio oficial SUNAT (Dólar Estadounidense)")
public class CambioSunatDTO {

    @Schema(description = "Fecha del tipo de cambio", example = "2025-11-06")
    private LocalDate fecha;
    
    @Schema(description = "Tipo de cambio para compra", example = "3.750")
    private BigDecimal compra;
    
    @Schema(description = "Tipo de cambio para venta", example = "3.755")
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
