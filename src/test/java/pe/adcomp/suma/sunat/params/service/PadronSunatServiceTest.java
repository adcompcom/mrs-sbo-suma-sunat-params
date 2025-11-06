package pe.adcomp.suma.sunat.params.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.adcomp.suma.sunat.params.dto.PadronSunatDTO;
import pe.adcomp.suma.sunat.params.entity.PadronSunat;
import pe.adcomp.suma.sunat.params.repository.PadronSunatRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para PadronSunatService usando Mockito
 * No levanta el servidor ni la BD
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias de PadronSunatService")
class PadronSunatServiceTest {

    @Mock
    private PadronSunatRepository padronSunatRepository;

    @InjectMocks
    private PadronSunatService padronSunatService;

    private PadronSunat padronSunatMock;

    @BeforeEach
    void setUp() {
        // Crear datos de prueba
        padronSunatMock = new PadronSunat();
        padronSunatMock.setRuc(20123456789L);
        padronSunatMock.setDni(null);
        padronSunatMock.setNombreRazonSocial("EMPRESA XYZ S.A.");
        padronSunatMock.setEstadoContribuyente("Habido");
        padronSunatMock.setCondicionDomicilio("Principal");
        padronSunatMock.setUbigeo("150131");
        padronSunatMock.setDireccion("Calle Principal 123, Lima");
        padronSunatMock.setAgente(false);
    }

    // ========================= PRUEBAS DE BÚSQUEDA POR RUC =========================

    @Test
    @DisplayName("Buscar por RUC válido (11 dígitos) - Exitoso")
    void testBuscarPorRucValido() {
        // Arrange
        String ruc = "20123456789";
        when(padronSunatRepository.findByRuc(20123456789L))
                .thenReturn(Optional.of(padronSunatMock));

        // Act
        PadronSunatDTO resultado = padronSunatService.buscarPorDocumento(ruc);

        // Assert
        assertNotNull(resultado);
        assertEquals(20123456789L, resultado.getRuc());
        assertEquals("EMPRESA XYZ S.A.", resultado.getNombreRazonSocial());
        assertEquals("Habido", resultado.getEstadoContribuyente());
        verify(padronSunatRepository, times(1)).findByRuc(20123456789L);
    }

    @Test
    @DisplayName("Buscar por RUC inválido - Registro no encontrado")
    void testBuscarPorRucNoEncontrado() {
        // Arrange
        String ruc = "99999999999";
        when(padronSunatRepository.findByRuc(99999999999L))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                padronSunatService.buscarPorDocumento(ruc)
        );
        assertTrue(exception.getMessage().contains("No se encontró"));
        verify(padronSunatRepository, times(1)).findByRuc(99999999999L);
    }

    @Test
    @DisplayName("Buscar por RUC con menos de 11 dígitos - Error de validación")
    void testBuscarPorRucConMenosDeDiezDigitos() {
        // Arrange
        String ruc = "2012345678"; // 10 dígitos

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                padronSunatService.buscarPorDocumento(ruc)
        );
        assertTrue(exception.getMessage().contains("8 dígitos (DNI) u 11 dígitos (RUC)"));
        verify(padronSunatRepository, never()).findByRuc(anyLong());
    }

    @Test
    @DisplayName("Buscar por RUC con más de 11 dígitos - Error de validación")
    void testBuscarPorRucConMasDeOnceDigitos() {
        // Arrange
        String ruc = "201234567890"; // 12 dígitos

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                padronSunatService.buscarPorDocumento(ruc)
        );
        assertTrue(exception.getMessage().contains("8 dígitos (DNI) u 11 dígitos (RUC)"));
        verify(padronSunatRepository, never()).findByRuc(anyLong());
    }

    // ========================= PRUEBAS DE BÚSQUEDA POR DNI =========================

    @Test
    @DisplayName("Buscar por DNI válido (8 dígitos) - Exitoso")
    void testBuscarPorDniValido() {
        // Arrange
        String dni = "12345678";
        PadronSunat padronDni = new PadronSunat();
        padronDni.setRuc(null);
        padronDni.setDni(12345678L);
        padronDni.setNombreRazonSocial("Juan Pérez López");

        when(padronSunatRepository.findByDni(12345678L))
                .thenReturn(Optional.of(padronDni));

        // Act
        PadronSunatDTO resultado = padronSunatService.buscarPorDocumento(dni);

        // Assert
        assertNotNull(resultado);
        assertEquals(12345678L, resultado.getDni());
        assertEquals("Juan Pérez López", resultado.getNombreRazonSocial());
        verify(padronSunatRepository, times(1)).findByDni(12345678L);
    }

    @Test
    @DisplayName("Buscar por DNI inválido - Registro no encontrado")
    void testBuscarPorDniNoEncontrado() {
        // Arrange
        String dni = "99999999";
        when(padronSunatRepository.findByDni(99999999L))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                padronSunatService.buscarPorDocumento(dni)
        );
        assertTrue(exception.getMessage().contains("No se encontró"));
        verify(padronSunatRepository, times(1)).findByDni(99999999L);
    }

    @Test
    @DisplayName("Buscar por DNI con menos de 8 dígitos - Error de validación")
    void testBuscarPorDniConMenosDe8Digitos() {
        // Arrange
        String dni = "1234567"; // 7 dígitos

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                padronSunatService.buscarPorDocumento(dni)
        );
        assertTrue(exception.getMessage().contains("8 dígitos (DNI) u 11 dígitos (RUC)"));
        verify(padronSunatRepository, never()).findByDni(anyLong());
    }

    @Test
    @DisplayName("Buscar por DNI con más de 8 dígitos (pero no 11) - Error de validación")
    void testBuscarPorDniConMasDe8DigitosPeronLuego11() {
        // Arrange
        String dni = "123456789"; // 9 dígitos

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                padronSunatService.buscarPorDocumento(dni)
        );
        assertTrue(exception.getMessage().contains("8 dígitos (DNI) u 11 dígitos (RUC)"));
        verify(padronSunatRepository, never()).findByDni(anyLong());
    }

    // ========================= PRUEBAS DE VALIDACIÓN GENERAL =========================

    @Test
    @DisplayName("Documento nulo - Error de validación")
    void testBuscarPorDocumentoNulo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                padronSunatService.buscarPorDocumento(null)
        );
        assertTrue(exception.getMessage().contains("no puede estar vacío"));
        verify(padronSunatRepository, never()).findByRuc(anyLong());
        verify(padronSunatRepository, never()).findByDni(anyLong());
    }

    @Test
    @DisplayName("Documento vacío - Error de validación")
    void testBuscarPorDocumentoVacio() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                padronSunatService.buscarPorDocumento("")
        );
        assertTrue(exception.getMessage().contains("no puede estar vacío"));
        verify(padronSunatRepository, never()).findByRuc(anyLong());
        verify(padronSunatRepository, never()).findByDni(anyLong());
    }

    @Test
    @DisplayName("Documento con espacios en blanco - Error de validación")
    void testBuscarPorDocumentoConSoloEspacios() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                padronSunatService.buscarPorDocumento("   ")
        );
        assertTrue(exception.getMessage().contains("no puede estar vacío"));
        verify(padronSunatRepository, never()).findByRuc(anyLong());
        verify(padronSunatRepository, never()).findByDni(anyLong());
    }

    @Test
    @DisplayName("Documento con caracteres no numéricos - Error de validación")
    void testBuscarPorDocumentoConCaracteresNoNumericos() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                padronSunatService.buscarPorDocumento("ABC12345")
        );
        assertTrue(exception.getMessage().contains("solo dígitos"));
        verify(padronSunatRepository, never()).findByRuc(anyLong());
        verify(padronSunatRepository, never()).findByDni(anyLong());
    }

    @Test
    @DisplayName("Documento con espacios intermedios - Error de validación")
    void testBuscarPorDocumentoConEspaciosIntermedios() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                padronSunatService.buscarPorDocumento("2012 345 6789")
        );
        assertTrue(exception.getMessage().contains("solo dígitos"));
        verify(padronSunatRepository, never()).findByRuc(anyLong());
        verify(padronSunatRepository, never()).findByDni(anyLong());
    }

    @Test
    @DisplayName("Documento con espacios alrededor se limpia - Exitoso")
    void testBuscarPorDocumentoConEspaciosAlrededor() {
        // Arrange
        String documento = "  20123456789  "; // Con espacios alrededor
        when(padronSunatRepository.findByRuc(20123456789L))
                .thenReturn(Optional.of(padronSunatMock));

        // Act
        PadronSunatDTO resultado = padronSunatService.buscarPorDocumento(documento);

        // Assert
        assertNotNull(resultado);
        verify(padronSunatRepository, times(1)).findByRuc(20123456789L);
    }

    // ========================= PRUEBAS DE CONVERSIÓN A DTO =========================

    @Test
    @DisplayName("Conversión de Entity a DTO - Todos los campos")
    void testConversionADTO() {
        // Arrange
        String ruc = "20123456789";
        when(padronSunatRepository.findByRuc(20123456789L))
                .thenReturn(Optional.of(padronSunatMock));

        // Act
        PadronSunatDTO dto = padronSunatService.buscarPorDocumento(ruc);

        // Assert
        assertNotNull(dto);
        assertEquals(padronSunatMock.getRuc(), dto.getRuc());
        assertEquals(padronSunatMock.getDni(), dto.getDni());
        assertEquals(padronSunatMock.getNombreRazonSocial(), dto.getNombreRazonSocial());
        assertEquals(padronSunatMock.getEstadoContribuyente(), dto.getEstadoContribuyente());
        assertEquals(padronSunatMock.getCondicionDomicilio(), dto.getCondicionDomicilio());
        assertEquals(padronSunatMock.getUbigeo(), dto.getUbigeo());
        assertEquals(padronSunatMock.getDireccion(), dto.getDireccion());
        assertEquals(padronSunatMock.getAgente(), dto.getAgente());
    }

}
