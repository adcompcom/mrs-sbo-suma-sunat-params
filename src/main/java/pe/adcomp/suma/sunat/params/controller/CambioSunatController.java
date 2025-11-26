package pe.adcomp.suma.sunat.params.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import pe.adcomp.suma.sunat.params.dto.CambioSunatDTO;
import pe.adcomp.suma.sunat.params.service.CambioSunatService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Tipo de Cambio SUNAT", description = "API para consultar el tipo de cambio oficial de SUNAT. Requiere autenticación JWT.")
@RestController
@RequestMapping("/api/cambio-sunat")
@SecurityRequirement(name = "bearerAuth")
public class CambioSunatController {

    @Autowired
    private CambioSunatService cambioSunatService;

    @Operation(
            summary = "Buscar tipo de cambio por fecha",
            description = "Consulta el tipo de cambio SUNAT para una fecha específica en formato yyyy-MM-dd"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tipo de cambio encontrado exitosamente",
                    content = @Content(schema = @Schema(implementation = CambioSunatDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Formato de fecha inválido",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró tipo de cambio para la fecha indicada",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping("/buscar-por-fecha")
    public ResponseEntity<?> buscarPorFecha(
            @Parameter(description = "Fecha en formato yyyy-MM-dd", example = "2025-11-06", required = true)
            @RequestParam("fecha") String fecha) {
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

    @Operation(
            summary = "Buscar tipo de cambio por fecha (ruta alternativa)",
            description = "Consulta el tipo de cambio SUNAT usando la fecha en la ruta"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tipo de cambio encontrado",
                    content = @Content(schema = @Schema(implementation = CambioSunatDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Formato de fecha inválido"),
            @ApiResponse(responseCode = "404", description = "No se encontró tipo de cambio")
    })
    @GetMapping("/{fecha}")
    public ResponseEntity<?> buscarPorFechaPath(
            @Parameter(description = "Fecha en formato yyyy-MM-dd", example = "2025-11-06", required = true)
            @PathVariable("fecha") String fecha) {
        return buscarPorFecha(fecha);
    }

    @Operation(
            summary = "Buscar tipos de cambio por año y mes",
            description = "Consulta todos los tipos de cambio SUNAT para un mes específico, ordenados por fecha descendente"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de tipos de cambio encontrados",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CambioSunatDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Formato de año o mes inválido",
                    content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontraron tipos de cambio para el período indicado",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping("/buscar-por-mes")
    public ResponseEntity<?> buscarPorAnoYMes(
            @Parameter(description = "Año (formato yyyy)", example = "2025", required = true)
            @RequestParam("ano") String ano,
            @Parameter(description = "Mes (1-12)", example = "11", required = true)
            @RequestParam("mes") String mes) {
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

    @Operation(
            summary = "Buscar tipos de cambio por año y mes (ruta alternativa)",
            description = "Consulta los tipos de cambio usando año y mes en la ruta"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de tipos de cambio encontrados",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CambioSunatDTO.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Formato inválido"),
            @ApiResponse(responseCode = "404", description = "No se encontraron registros")
    })
    @GetMapping("/mes/{ano}/{mes}")
    public ResponseEntity<?> buscarPorAnoYMesPath(
            @Parameter(description = "Año (formato yyyy)", example = "2025", required = true)
            @PathVariable("ano") String ano,
            @Parameter(description = "Mes (1-12)", example = "11", required = true)
            @PathVariable("mes") String mes) {
        return buscarPorAnoYMes(ano, mes);
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
        response.put("service", "Cambio SUNAT Service");
        return ResponseEntity.ok(response);
    }
}
