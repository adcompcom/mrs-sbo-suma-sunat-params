package pe.adcomp.suma.sunat.params.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.adcomp.suma.sunat.params.dto.PadronSunatDTO;
import pe.adcomp.suma.sunat.params.service.PadronSunatService;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Padrón SUNAT", description = "API para consultar información del Padrón de Contribuyentes SUNAT (RUC/DNI). Requiere autenticación JWT.")
@RestController
@RequestMapping("/api/padron-sunat")
@SecurityRequirement(name = "bearerAuth")
public class PadronSunatController {

    @Autowired
    private PadronSunatService padronSunatService;

    @Operation(
            summary = "Buscar información por RUC o DNI",
            description = "Consulta la información del Padrón SUNAT usando el número de RUC (11 dígitos) o DNI (8 dígitos). **Requiere token JWT en el header Authorization.**"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Información encontrada exitosamente",
                    content = @Content(schema = @Schema(implementation = PadronSunatDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Formato de documento inválido",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autorizado - Token JWT inválido o no proporcionado",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Documento no encontrado en el padrón",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorDocumento(
            @Parameter(description = "Número de RUC (11 dígitos) o DNI (8 dígitos)", example = "20123456789", required = true)
            @RequestParam("documento") String numeroDocumento) {
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

    @Operation(
            summary = "Buscar información por RUC o DNI (ruta alternativa)",
            description = "Consulta la información del Padrón SUNAT usando el número de documento en la ruta"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Información encontrada exitosamente",
                    content = @Content(schema = @Schema(implementation = PadronSunatDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Formato de documento inválido"),
            @ApiResponse(responseCode = "404", description = "Documento no encontrado")
    })
    @GetMapping("/{documento}")
    public ResponseEntity<?> buscarPorDocumentoPath(
            @Parameter(description = "Número de RUC (11 dígitos) o DNI (8 dígitos)", example = "20123456789", required = true)
            @PathVariable("documento") String numeroDocumento) {
        return buscarPorDocumento(numeroDocumento);
    }

    @Operation(
            summary = "Verificar estado del servicio",
            description = "Endpoint de health check para verificar que el servicio está activo"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Servicio activo",
            content = @Content(schema = @Schema(implementation = Map.class))
    )
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Padrón SUNAT Service");
        return ResponseEntity.ok(response);
    }
}
