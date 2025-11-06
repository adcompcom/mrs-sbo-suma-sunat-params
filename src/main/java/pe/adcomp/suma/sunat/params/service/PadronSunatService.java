package pe.adcomp.suma.sunat.params.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.adcomp.suma.sunat.params.dto.PadronSunatDTO;
import pe.adcomp.suma.sunat.params.entity.PadronSunat;
import pe.adcomp.suma.sunat.params.repository.PadronSunatRepository;

import java.util.Optional;

@Service
public class PadronSunatService {

    @Autowired
    private PadronSunatRepository padronSunatRepository;

    /**
     * Busca información del padrón SUNAT por número de documento
     * Discrimina automáticamente si es RUC (11 dígitos) o DNI (8 dígitos)
     * 
     * @param numeroDocumento El número de documento (RUC o DNI)
     * @return PadronSunatDTO con la información encontrada
     * @throws IllegalArgumentException si el número de documento no tiene 8 u 11 dígitos
     * @throws RuntimeException si no se encuentra el registro
     */
    public PadronSunatDTO buscarPorDocumento(String numeroDocumento) {
        // Validar que el número de documento no sea nulo o vacío
        if (numeroDocumento == null || numeroDocumento.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de documento no puede estar vacío");
        }

        // Limpiar espacios
        numeroDocumento = numeroDocumento.trim();

        // Validar que solo contenga números
        if (!numeroDocumento.matches("\\d+")) {
            throw new IllegalArgumentException("El número de documento debe contener solo dígitos");
        }

        // Validar longitud
        int longitud = numeroDocumento.length();
        if (longitud != 8 && longitud != 11) {
            throw new IllegalArgumentException("El número de documento debe tener 8 dígitos (DNI) u 11 dígitos (RUC)");
        }

        // Convertir a Long
        Long numero = Long.parseLong(numeroDocumento);

        // Buscar en la base de datos
        Optional<PadronSunat> padronOpt;
        
        if (longitud == 11) {
            // Es un RUC
            padronOpt = padronSunatRepository.findByRuc(numero);
        } else {
            // Es un DNI
            padronOpt = padronSunatRepository.findByDni(numero);
        }

        // Validar si se encontró el registro
        if (padronOpt.isEmpty()) {
            throw new RuntimeException("No se encontró información para el documento: " + numeroDocumento);
        }

        // Convertir Entity a DTO
        return convertirADTO(padronOpt.get());
    }

    /**
     * Convierte una entidad PadronSunat a un DTO
     */
    private PadronSunatDTO convertirADTO(PadronSunat entity) {
        PadronSunatDTO dto = new PadronSunatDTO();
        dto.setRuc(entity.getRuc());
        dto.setDni(entity.getDni());
        dto.setNombreRazonSocial(entity.getNombreRazonSocial());
        dto.setEstadoContribuyente(entity.getEstadoContribuyente());
        dto.setCondicionDomicilio(entity.getCondicionDomicilio());
        dto.setUbigeo(entity.getUbigeo());
        dto.setDireccion(entity.getDireccion());
        dto.setAgente(entity.getAgente());
        return dto;
    }
}
