package pe.adcomp.suma.sunat.params.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información del Padrón de Contribuyentes SUNAT")
public class PadronSunatDTO {

    @Schema(description = "Número de RUC (11 dígitos)", example = "20123456789")
    private Long ruc;
    
    @Schema(description = "Número de DNI (8 dígitos)", example = "12345678")
    private Long dni;
    
    @Schema(description = "Nombre completo o Razón Social", example = "EMPRESA DEMO S.A.C.")
    private String nombreRazonSocial;
    
    @Schema(description = "Estado del contribuyente en SUNAT", example = "ACTIVO")
    private String estadoContribuyente;
    
    @Schema(description = "Condición del domicilio fiscal", example = "HABIDO")
    private String condicionDomicilio;
    
    @Schema(description = "Código de ubigeo", example = "150101")
    private String ubigeo;
    
    @Schema(description = "Dirección fiscal completa", example = "AV. EJEMPLO NRO. 123 LIMA - LIMA - LIMA")
    private String direccion;
    
    @Schema(description = "Indica si es agente de retención/percepción", example = "true")
    private Boolean agente;

    // Constructors
    public PadronSunatDTO() {
    }

    // Getters and Setters
    public Long getRuc() {
        return ruc;
    }

    public void setRuc(Long ruc) {
        this.ruc = ruc;
    }

    public Long getDni() {
        return dni;
    }

    public void setDni(Long dni) {
        this.dni = dni;
    }

    public String getNombreRazonSocial() {
        return nombreRazonSocial;
    }

    public void setNombreRazonSocial(String nombreRazonSocial) {
        this.nombreRazonSocial = nombreRazonSocial;
    }

    public String getEstadoContribuyente() {
        return estadoContribuyente;
    }

    public void setEstadoContribuyente(String estadoContribuyente) {
        this.estadoContribuyente = estadoContribuyente;
    }

    public String getCondicionDomicilio() {
        return condicionDomicilio;
    }

    public void setCondicionDomicilio(String condicionDomicilio) {
        this.condicionDomicilio = condicionDomicilio;
    }

    public String getUbigeo() {
        return ubigeo;
    }

    public void setUbigeo(String ubigeo) {
        this.ubigeo = ubigeo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Boolean getAgente() {
        return agente;
    }

    public void setAgente(Boolean agente) {
        this.agente = agente;
    }
}
