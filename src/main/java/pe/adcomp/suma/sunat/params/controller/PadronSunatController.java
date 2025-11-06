package pe.adcomp.suma.sunat.params.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.adcomp.suma.sunat.params.dto.PadronSunatDTO;
import pe.adcomp.suma.sunat.params.service.PadronSunatService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/padron-sunat")
public class PadronSunatController {

    @Autowired
    private PadronSunatService padronSunatService;

    /**
     * Endpoint para buscar información del padrón SUNAT por RUC o DNI
     * 
     * @param numeroDocumento El número de documento (RUC de 11 dígitos o DNI de 8 dígitos)
     * @return ResponseEntity con la información encontrada o mensaje de error
     * 
     * Ejemplo de uso:
     * GET /api/padron-sunat/buscar?documento=20123456789  (RUC)
     * GET /api/padron-sunat/buscar?documento=12345678     (DNI)
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorDocumento(@RequestParam("documento") String numeroDocumento) {
        try {
            PadronSunatDTO resultado = padronSunatService.buscarPorDocumento(numeroDocumento);
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
     * Endpoint alternativo usando path variable
     * 
     * Ejemplo de uso:
     * GET /api/padron-sunat/20123456789  (RUC)
     * GET /api/padron-sunat/12345678     (DNI)
     */
    @GetMapping("/{documento}")
    public ResponseEntity<?> buscarPorDocumentoPath(@PathVariable("documento") String numeroDocumento) {
        return buscarPorDocumento(numeroDocumento);
    }

    /**
     * Endpoint para verificar el estado del servicio
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Padrón SUNAT Service");
        return ResponseEntity.ok(response);
    }
}
