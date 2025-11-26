package pe.adcomp.suma.sunat.params.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "padron_sunat")
public class PadronSunat {

    @Id
    @Column(name = "ruc", nullable = false)
    private Long ruc;

    @Column(name = "dni")
    private Long dni;

    @Column(name = "nombre_razon_social", length = 500)
    private String nombreRazonSocial;

    @Column(name = "estado_contribuyente", length = 50)
    private String estadoContribuyente;

    @Column(name = "condicion_domicilio", length = 50)
    private String condicionDomicilio;

    @Column(name = "ubigeo", length = 10)
    private String ubigeo;

    @Column(name = "direccion", columnDefinition = "TEXT")
    private String direccion;

    @Column(name = "agente")
    private Boolean agente;

    // Constructors
    public PadronSunat() {
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
