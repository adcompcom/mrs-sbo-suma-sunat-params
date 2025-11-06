package pe.adcomp.suma.sunat.params.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.adcomp.suma.sunat.params.dto.CambioSunatDTO;
import pe.adcomp.suma.sunat.params.service.CambioSunatService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador para operaciones de Cambio SUNAT
 */
@RestController
@RequestMapping("/api/cambio-sunat")
public class CambioSunatController {

    @Autowired
    private CambioSunatService cambioSunatService;

    /**
     * Endpoint para buscar el cambio SUNAT por una fecha específica
     * 
     * @param fecha La fecha en formato yyyy-MM-dd (ej: 2025-11-06)
     * @return ResponseEntity con el objeto CambioSunatDTO o mensaje de error
     * 
     * Ejemplos de uso:
     * GET /api/cambio-sunat/buscar-por-fecha?fecha=2025-11-06
     * GET /api/cambio-sunat/2025-11-06  (Path variable)
     */
    @GetMapping("/buscar-por-fecha")
    public ResponseEntity<?> buscarPorFecha(@RequestParam("fecha") String fecha) {
        try {
            CambioSunatDTO resultado = cambioSunatService.buscarPorFecha(fecha);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            // Error de validación (formato incorrecto)
            Map<String, String> error = new HashMap<>();
            error.put("error", "Validación fallida");
            error.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (RuntimeException e) {
            // No se encontró el registro
            Map<String, String> error = new HashMap<>();
            error.put("error", "No encontrado");
            error.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            // Error interno del servidor
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor");
            error.put("mensaje", "Ocurrió un error inesperado al procesar la solicitud");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Endpoint alternativo usando path variable para buscar por fecha
     * 
     * Ejemplo de uso:
     * GET /api/cambio-sunat/2025-11-06
     */
    @GetMapping("/{fecha}")
    public ResponseEntity<?> buscarPorFechaPath(@PathVariable("fecha") String fecha) {
        return buscarPorFecha(fecha);
    }

    /**
     * Endpoint para buscar cambios SUNAT por año y mes
     * Retorna una lista de cambios ordenados por fecha descendente
     * 
     * @param ano El año (ej: 2025)
     * @param mes El mes (1-12)
     * @return ResponseEntity con lista de CambioSunatDTO o mensaje de error
     * 
     * Ejemplos de uso:
     * GET /api/cambio-sunat/buscar-por-mes?ano=2025&mes=11
     * GET /api/cambio-sunat/mes/2025/11  (Path variables)
     */
    @GetMapping("/buscar-por-mes")
    public ResponseEntity<?> buscarPorAnoYMes(@RequestParam("ano") String ano, @RequestParam("mes") String mes) {
        try {
            List<CambioSunatDTO> resultado = cambioSunatService.buscarPorAnoYMes(ano, mes);
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            // Error de validación (formato incorrecto)
            Map<String, String> error = new HashMap<>();
            error.put("error", "Validación fallida");
            error.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (RuntimeException e) {
            // No se encontraron registros
            Map<String, String> error = new HashMap<>();
            error.put("error", "No encontrado");
            error.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            // Error interno del servidor
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor");
            error.put("mensaje", "Ocurrió un error inesperado al procesar la solicitud");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Endpoint alternativo usando path variables para buscar por mes
     * 
     * Ejemplo de uso:
     * GET /api/cambio-sunat/mes/2025/11
     */
    @GetMapping("/mes/{ano}/{mes}")
    public ResponseEntity<?> buscarPorAnoYMesPath(@PathVariable("ano") String ano, @PathVariable("mes") String mes) {
        return buscarPorAnoYMes(ano, mes);
    }

    /**
     * Endpoint para verificar el estado del servicio
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Cambio SUNAT Service");
        return ResponseEntity.ok(response);
    }
}
