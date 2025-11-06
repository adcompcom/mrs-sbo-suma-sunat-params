package pe.adcomp.suma.sunat.params.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.adcomp.suma.sunat.params.dto.CambioSunatDTO;
import pe.adcomp.suma.sunat.params.entity.CambioSunat;
import pe.adcomp.suma.sunat.params.repository.CambioSunatRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para operaciones de CambioSunat
 */
@Service
public class CambioSunatService {

    @Autowired
    private CambioSunatRepository cambioSunatRepository;

    /**
     * Busca el cambio SUNAT para una fecha específica
     *
     * @param fecha La fecha en formato yyyy-MM-dd (ej: 2025-11-06)
     * @return CambioSunatDTO con la información del cambio
     * @throws IllegalArgumentException si la fecha tiene formato incorrecto
     * @throws RuntimeException si no se encuentra el registro
     */
    public CambioSunatDTO buscarPorFecha(String fecha) {
        // Validar que la fecha no sea nula o vacía
        if (fecha == null || fecha.trim().isEmpty()) {
            throw new IllegalArgumentException("La fecha no puede estar vacía");
        }

        // Limpiar espacios
        fecha = fecha.trim();

        // Validar formato de fecha (yyyy-MM-dd)
        if (!fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("La fecha debe estar en formato yyyy-MM-dd (ej: 2025-11-06)");
        }

        LocalDate fechaLocal;
        try {
            fechaLocal = LocalDate.parse(fecha);
        } catch (Exception e) {
            throw new IllegalArgumentException("Fecha inválida: " + fecha + ". Debe estar en formato yyyy-MM-dd");
        }

        // Buscar en la base de datos
        Optional<CambioSunat> cambioOpt = cambioSunatRepository.findByFecha(fechaLocal);

        // Validar si se encontró el registro
        if (cambioOpt.isEmpty()) {
            throw new RuntimeException("No se encontró cambio SUNAT para la fecha: " + fecha);
        }

        return convertirADTO(cambioOpt.get());
    }

    /**
     * Busca cambios SUNAT para un mes y año específicos
     *
     * @param ano El año (ej: 2025)
     * @param mes El mes (1-12)
     * @return Lista de CambioSunatDTO ordenada por fecha descendente
     * @throws IllegalArgumentException si el año o mes son inválidos
     */
    public List<CambioSunatDTO> buscarPorAnoYMes(String ano, String mes) {
        // Validar año
        if (ano == null || ano.trim().isEmpty()) {
            throw new IllegalArgumentException("El año no puede estar vacío");
        }

        // Validar mes
        if (mes == null || mes.trim().isEmpty()) {
            throw new IllegalArgumentException("El mes no puede estar vacío");
        }

        ano = ano.trim();
        mes = mes.trim();

        // Validar que sean solo números
        if (!ano.matches("\\d+")) {
            throw new IllegalArgumentException("El año debe contener solo dígitos");
        }

        if (!mes.matches("\\d+")) {
            throw new IllegalArgumentException("El mes debe contener solo dígitos");
        }

        int anoInt;
        int mesInt;

        try {
            anoInt = Integer.parseInt(ano);
            mesInt = Integer.parseInt(mes);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El año y mes deben ser números válidos");
        }

        // Validar rango de año (permitir años razonables)
        if (anoInt < 2000 || anoInt > 2100) {
            throw new IllegalArgumentException("El año debe estar entre 2000 y 2100");
        }

        // Validar rango de mes (1-12)
        if (mesInt < 1 || mesInt > 12) {
            throw new IllegalArgumentException("El mes debe estar entre 1 y 12");
        }

        // Buscar en la base de datos
        List<CambioSunat> cambios = cambioSunatRepository.findByAnoAndMes(anoInt, mesInt);

        // Si no hay resultados, retornar lista vacía (esto no es error)
        if (cambios.isEmpty()) {
            throw new RuntimeException("No se encontraron registros de cambio SUNAT para " + mes + "/" + ano);
        }

        return cambios.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una entidad CambioSunat a un DTO
     */
    private CambioSunatDTO convertirADTO(CambioSunat entity) {
        CambioSunatDTO dto = new CambioSunatDTO();
        dto.setFecha(entity.getFecha());
        dto.setCompra(entity.getCompra());
        dto.setVenta(entity.getVenta());
        return dto;
    }
}
