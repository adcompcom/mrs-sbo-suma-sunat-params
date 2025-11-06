package pe.adcomp.suma.sunat.params.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pe.adcomp.suma.sunat.params.dto.PadronSunatDTO;
import pe.adcomp.suma.sunat.params.service.PadronSunatService;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias para PadronSunatController usando MockMvc
 * No levanta el servidor completo
 */
@WebMvcTest(PadronSunatController.class)
@DisplayName("Pruebas unitarias de PadronSunatController")
@SuppressWarnings("unchecked")
class PadronSunatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PadronSunatService padronSunatService;

    // ========================= PRUEBAS DE BÚSQUEDA POR DOCUMENTO =========================

    @Test
    @DisplayName("GET /api/padron-sunat/buscar?documento=20123456789 - Exitoso")
    void testBuscarPorDocumentoExitoso() throws Exception {
        // Arrange
        PadronSunatDTO mockResponse = new PadronSunatDTO();
        mockResponse.setRuc(20123456789L);
        mockResponse.setNombreRazonSocial("EMPRESA XYZ S.A.");
        mockResponse.setEstadoContribuyente("Habido");

        when(padronSunatService.buscarPorDocumento("20123456789"))
                .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/padron-sunat/buscar")
                        .param("documento", "20123456789"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ruc", is(20123456789L)))
                .andExpect(jsonPath("$.nombreRazonSocial", is("EMPRESA XYZ S.A.")))
                .andExpect(jsonPath("$.estadoContribuyente", is("Habido")));

        verify(padronSunatService, times(1)).buscarPorDocumento("20123456789");
    }

    @Test
    @DisplayName("GET /api/padron-sunat/buscar?documento=12345678 - Exitoso (DNI)")
    void testBuscarPorDniExitoso() throws Exception {
        // Arrange
        PadronSunatDTO mockResponse = new PadronSunatDTO();
        mockResponse.setDni(12345678L);
        mockResponse.setNombreRazonSocial("Juan Pérez López");

        when(padronSunatService.buscarPorDocumento("12345678"))
                .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/padron-sunat/buscar")
                        .param("documento", "12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dni", is((int) 12345678)))
                .andExpect(jsonPath("$.nombreRazonSocial", is("Juan Pérez López")));

        verify(padronSunatService, times(1)).buscarPorDocumento("12345678");
    }

    @Test
    @DisplayName("GET /api/padron-sunat/buscar - Documento no existe (404)")
    void testBuscarPorDocumentoNoEncontrado() throws Exception {
        // Arrange
        when(padronSunatService.buscarPorDocumento(anyString()))
                .thenThrow(new RuntimeException("No se encontró información para el documento: 99999999999"));

        // Act & Assert
        mockMvc.perform(get("/api/padron-sunat/buscar")
                        .param("documento", "99999999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("No encontrado")))
                .andExpect(jsonPath("$.mensaje", containsString("No se encontró")));

        verify(padronSunatService, times(1)).buscarPorDocumento("99999999999");
    }

    @Test
    @DisplayName("GET /api/padron-sunat/buscar?documento=ABC12345 - Validación fallida (400)")
    void testBuscarPorDocumentoValidacionFallida() throws Exception {
        // Arrange
        when(padronSunatService.buscarPorDocumento("ABC12345"))
                .thenThrow(new IllegalArgumentException("El número de documento debe contener solo dígitos"));

        // Act & Assert
        mockMvc.perform(get("/api/padron-sunat/buscar")
                        .param("documento", "ABC12345"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validación fallida")))
                .andExpect(jsonPath("$.mensaje", containsString("solo dígitos")));

        verify(padronSunatService, times(1)).buscarPorDocumento("ABC12345");
    }

    @Test
    @DisplayName("GET /api/padron-sunat/buscar?documento=123456 - Longitud inválida (400)")
    void testBuscarPorDocumentoLongitudInvalida() throws Exception {
        // Arrange
        when(padronSunatService.buscarPorDocumento("123456"))
                .thenThrow(new IllegalArgumentException("El número de documento debe tener 8 dígitos (DNI) u 11 dígitos (RUC)"));

        // Act & Assert
        mockMvc.perform(get("/api/padron-sunat/buscar")
                        .param("documento", "123456"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validación fallida")))
                .andExpect(jsonPath("$.mensaje", containsString("8 dígitos (DNI) u 11 dígitos (RUC)")));

        verify(padronSunatService, times(1)).buscarPorDocumento("123456");
    }

    // ========================= PRUEBAS DE PATH VARIABLE =========================

    @Test
    @DisplayName("GET /api/padron-sunat/20123456789 - Exitoso (Path variable)")
    void testBuscarPorDocumentoPathVariable() throws Exception {
        // Arrange
        PadronSunatDTO mockResponse = new PadronSunatDTO();
        mockResponse.setRuc(20123456789L);
        mockResponse.setNombreRazonSocial("EMPRESA XYZ S.A.");

        when(padronSunatService.buscarPorDocumento("20123456789"))
                .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/padron-sunat/20123456789"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ruc", is(20123456789L)))
                .andExpect(jsonPath("$.nombreRazonSocial", is("EMPRESA XYZ S.A.")));

        verify(padronSunatService, times(1)).buscarPorDocumento("20123456789");
    }

    // ========================= PRUEBAS DE HEALTH CHECK =========================

    @Test
    @DisplayName("GET /api/padron-sunat/health - Status UP")
    void testHealthCheck() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/padron-sunat/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")))
                .andExpect(jsonPath("$.service", is("Padrón SUNAT Service")));
    }

    // ========================= PRUEBAS DE PARÁMETRO FALTANTE =========================

    @Test
    @DisplayName("GET /api/padron-sunat/buscar (sin parámetro) - Error 400")
    void testBuscarSinParametro() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/padron-sunat/buscar"))
                .andExpect(status().isBadRequest());
    }

}
