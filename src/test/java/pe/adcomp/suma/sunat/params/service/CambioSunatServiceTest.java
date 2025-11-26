package pe.adcomp.suma.sunat.params.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.adcomp.suma.sunat.params.dto.CambioSunatDTO;
import pe.adcomp.suma.sunat.params.entity.CambioSunat;
import pe.adcomp.suma.sunat.params.repository.CambioSunatRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para CambioSunatService usando Mockito
 * No levanta el servidor ni la BD
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias de CambioSunatService")
class CambioSunatServiceTest {

    @Mock
    private CambioSunatRepository cambioSunatRepository;

    @InjectMocks
    private CambioSunatService cambioSunatService;

    private CambioSunat cambioSunatMock;
    private List<CambioSunat> cambiosListMock;

    @BeforeEach
    void setUp() {
        // Crear datos de prueba para una fecha
        cambioSunatMock = new CambioSunat(
                LocalDate.of(2025, 11, 6),
                new BigDecimal("3.50"),
                new BigDecimal("3.52")
        );

        // Crear lista de cambios para pruebas de mes/año
        CambioSunat cambio1 = new CambioSunat(
                LocalDate.of(2025, 11, 6),
                new BigDecimal("3.50"),
                new BigDecimal("3.52")
        );
        CambioSunat cambio2 = new CambioSunat(
                LocalDate.of(2025, 11, 5),
                new BigDecimal("3.49"),
                new BigDecimal("3.51")
        );
        CambioSunat cambio3 = new CambioSunat(
                LocalDate.of(2025, 11, 4),
                new BigDecimal("3.48"),
                new BigDecimal("3.50")
        );
        cambiosListMock = Arrays.asList(cambio1, cambio2, cambio3);
    }

    // ========================= PRUEBAS DE BÚSQUEDA POR FECHA =========================

    @Test
    @DisplayName("Buscar por fecha válida (yyyy-MM-dd) - Exitoso")
    void testBuscarPorFechaValida() {
        // Arrange
        String fecha = "2025-11-06";
        when(cambioSunatRepository.findByFecha(LocalDate.of(2025, 11, 6)))
                .thenReturn(Optional.of(cambioSunatMock));

        // Act
        CambioSunatDTO resultado = cambioSunatService.buscarPorFecha(fecha);

        // Assert
        assertNotNull(resultado);
        assertEquals(LocalDate.of(2025, 11, 6), resultado.getFecha());
        assertEquals(new BigDecimal("3.50"), resultado.getCompra());
        assertEquals(new BigDecimal("3.52"), resultado.getVenta());
        verify(cambioSunatRepository, times(1)).findByFecha(LocalDate.of(2025, 11, 6));
    }

    @Test
    @DisplayName("Buscar por fecha no existente - Registro no encontrado")
    void testBuscarPorFechaNoEncontrada() {
        // Arrange
        String fecha = "2025-12-31";
        when(cambioSunatRepository.findByFecha(LocalDate.of(2025, 12, 31)))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                cambioSunatService.buscarPorFecha(fecha)
        );
        assertTrue(exception.getMessage().contains("No se encontró cambio SUNAT"));
        verify(cambioSunatRepository, times(1)).findByFecha(LocalDate.of(2025, 12, 31));
    }

    @Test
    @DisplayName("Buscar por fecha nula - Error de validación")
    void testBuscarPorFechaNula() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                cambioSunatService.buscarPorFecha(null)
        );
        assertTrue(exception.getMessage().contains("no puede estar vacía"));
        verify(cambioSunatRepository, never()).findByFecha(any());
    }

    @Test
    @DisplayName("Buscar por fecha vacía - Error de validación")
    void testBuscarPorFechaVacia() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                cambioSunatService.buscarPorFecha("")
        );
        assertTrue(exception.getMessage().contains("no puede estar vacía"));
        verify(cambioSunatRepository, never()).findByFecha(any());
    }

    @Test
    @DisplayName("Buscar por fecha con espacios - Se limpia correctamente")
    void testBuscarPorFechaConEspacios() {
        // Arrange
        String fecha = "  2025-11-06  ";
        when(cambioSunatRepository.findByFecha(LocalDate.of(2025, 11, 6)))
                .thenReturn(Optional.of(cambioSunatMock));

        // Act
        CambioSunatDTO resultado = cambioSunatService.buscarPorFecha(fecha);

        // Assert
        assertNotNull(resultado);
        verify(cambioSunatRepository, times(1)).findByFecha(LocalDate.of(2025, 11, 6));
    }

    @Test
    @DisplayName("Buscar por fecha con formato incorrecto (MM-dd-yyyy) - Error de validación")
    void testBuscarPorFechaFormatoIncorrecto1() {
        // Arrange
        String fecha = "11-06-2025";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                cambioSunatService.buscarPorFecha(fecha)
        );
        assertTrue(exception.getMessage().contains("formato yyyy-MM-dd"));
        verify(cambioSunatRepository, never()).findByFecha(any());
    }

    @Test
    @DisplayName("Buscar por fecha con formato incorrecto (dd/MM/yyyy) - Error de validación")
    void testBuscarPorFechaFormatoIncorrecto2() {
        // Arrange
        String fecha = "06/11/2025";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                cambioSunatService.buscarPorFecha(fecha)
        );
        assertTrue(exception.getMessage().contains("formato yyyy-MM-dd"));
        verify(cambioSunatRepository, never()).findByFecha(any());
    }

    @Test
    @DisplayName("Buscar por fecha inválida (2025-02-30) - Error de validación")
    void testBuscarPorFechaInvalida() {
        // Arrange
        String fecha = "2025-02-30";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                cambioSunatService.buscarPorFecha(fecha)
        );
        assertTrue(exception.getMessage().contains("Fecha inválida"));
        verify(cambioSunatRepository, never()).findByFecha(any());
    }

    // ========================= PRUEBAS DE BÚSQUEDA POR AÑO Y MES =========================

    @Test
    @DisplayName("Buscar por año y mes válidos - Retorna lista exitosamente")
    void testBuscarPorAnoYMesValidos() {
        // Arrange
        String ano = "2025";
        String mes = "11";
        when(cambioSunatRepository.findByAnoAndMes(2025, 11))
                .thenReturn(cambiosListMock);

        // Act
        List<CambioSunatDTO> resultado = cambioSunatService.buscarPorAnoYMes(ano, mes);

        // Assert
        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        assertEquals(LocalDate.of(2025, 11, 6), resultado.get(0).getFecha());
        assertEquals(LocalDate.of(2025, 11, 5), resultado.get(1).getFecha());
        assertEquals(LocalDate.of(2025, 11, 4), resultado.get(2).getFecha());
        verify(cambioSunatRepository, times(1)).findByAnoAndMes(2025, 11);
    }

    @Test
    @DisplayName("Buscar por año y mes sin registros - Lanza excepción")
    void testBuscarPorAnoYMesSinRegistros() {
        // Arrange
        String ano = "2025";
        String mes = "12";
        when(cambioSunatRepository.findByAnoAndMes(2025, 12))
                .thenReturn(Arrays.asList());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                cambioSunatService.buscarPorAnoYMes(ano, mes)
        );
        assertTrue(exception.getMessage().contains("No se encontraron registros"));
        verify(cambioSunatRepository, times(1)).findByAnoAndMes(2025, 12);
    }

    @Test
    @DisplayName("Buscar por año nulo - Error de validación")
    void testBuscarPorAnoNulo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                cambioSunatService.buscarPorAnoYMes(null, "11")
        );
        assertTrue(exception.getMessage().contains("año no puede estar vacío"));
        verify(cambioSunatRepository, never()).findByAnoAndMes(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Buscar por mes nulo - Error de validación")
    void testBuscarPorMesNulo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                cambioSunatService.buscarPorAnoYMes("2025", null)
        );
        assertTrue(exception.getMessage().contains("mes no puede estar vacío"));
        verify(cambioSunatRepository, never()).findByAnoAndMes(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Buscar por año con caracteres no numéricos - Error de validación")
    void testBuscarPorAnoNoNumerico() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                cambioSunatService.buscarPorAnoYMes("ABC", "11")
        );
        assertTrue(exception.getMessage().contains("solo dígitos"));
        verify(cambioSunatRepository, never()).findByAnoAndMes(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Buscar por mes con caracteres no numéricos - Error de validación")
    void testBuscarPorMesNoNumerico() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                cambioSunatService.buscarPorAnoYMes("2025", "XYZ")
        );
        assertTrue(exception.getMessage().contains("solo dígitos"));
        verify(cambioSunatRepository, never()).findByAnoAndMes(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Buscar por año fuera de rango (1999) - Error de validación")
    void testBuscarPorAnoFueraDeRangoMenor() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                cambioSunatService.buscarPorAnoYMes("1999", "11")
        );
        assertTrue(exception.getMessage().contains("entre 2000 y 2100"));
        verify(cambioSunatRepository, never()).findByAnoAndMes(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Buscar por año fuera de rango (2101) - Error de validación")
    void testBuscarPorAnoFueraDeRangoMayor() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                cambioSunatService.buscarPorAnoYMes("2101", "11")
        );
        assertTrue(exception.getMessage().contains("entre 2000 y 2100"));
        verify(cambioSunatRepository, never()).findByAnoAndMes(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Buscar por mes fuera de rango (0) - Error de validación")
    void testBuscarPorMesFueraDeRangoMenor() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                cambioSunatService.buscarPorAnoYMes("2025", "0")
        );
        assertTrue(exception.getMessage().contains("entre 1 y 12"));
        verify(cambioSunatRepository, never()).findByAnoAndMes(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Buscar por mes fuera de rango (13) - Error de validación")
    void testBuscarPorMesFueraDeRangoMayor() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                cambioSunatService.buscarPorAnoYMes("2025", "13")
        );
        assertTrue(exception.getMessage().contains("entre 1 y 12"));
        verify(cambioSunatRepository, never()).findByAnoAndMes(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Buscar por mes negativo - Error de validación")
    void testBuscarPorMesNegativo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                cambioSunatService.buscarPorAnoYMes("2025", "-1")
        );
        // -1 falla en validación de solo dígitos, no en rango
        assertTrue(exception.getMessage().contains("solo dígitos"));
        verify(cambioSunatRepository, never()).findByAnoAndMes(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Buscar por año y mes con espacios - Se limpian correctamente")
    void testBuscarPorAnoYMesConEspacios() {
        // Arrange
        String ano = "  2025  ";
        String mes = "  11  ";
        when(cambioSunatRepository.findByAnoAndMes(2025, 11))
                .thenReturn(cambiosListMock);

        // Act
        List<CambioSunatDTO> resultado = cambioSunatService.buscarPorAnoYMes(ano, mes);

        // Assert
        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        verify(cambioSunatRepository, times(1)).findByAnoAndMes(2025, 11);
    }

    // ========================= PRUEBAS DE CONVERSIÓN A DTO =========================

    @Test
    @DisplayName("Conversión de Entity a DTO - Todos los campos")
    void testConversionADTO() {
        // Arrange
        String fecha = "2025-11-06";
        when(cambioSunatRepository.findByFecha(LocalDate.of(2025, 11, 6)))
                .thenReturn(Optional.of(cambioSunatMock));

        // Act
        CambioSunatDTO dto = cambioSunatService.buscarPorFecha(fecha);

        // Assert
        assertNotNull(dto);
        assertEquals(cambioSunatMock.getFecha(), dto.getFecha());
        assertEquals(cambioSunatMock.getCompra(), dto.getCompra());
        assertEquals(cambioSunatMock.getVenta(), dto.getVenta());
    }

    // ========================= PRUEBAS DE EDGE CASES =========================

    @Test
    @DisplayName("Buscar por fecha al inicio del año")
    void testBuscarPorFechaInicioDelAno() {
        // Arrange
        String fecha = "2025-01-01";
        CambioSunat cambio = new CambioSunat(
                LocalDate.of(2025, 1, 1),
                new BigDecimal("3.25"),
                new BigDecimal("3.27")
        );
        when(cambioSunatRepository.findByFecha(LocalDate.of(2025, 1, 1)))
                .thenReturn(Optional.of(cambio));

        // Act
        CambioSunatDTO resultado = cambioSunatService.buscarPorFecha(fecha);

        // Assert
        assertNotNull(resultado);
        assertEquals(LocalDate.of(2025, 1, 1), resultado.getFecha());
        verify(cambioSunatRepository, times(1)).findByFecha(LocalDate.of(2025, 1, 1));
    }

    @Test
    @DisplayName("Buscar por mes enero (1)")
    void testBuscarPorMesEnero() {
        // Arrange
        String ano = "2025";
        String mes = "1";
        when(cambioSunatRepository.findByAnoAndMes(2025, 1))
                .thenReturn(cambiosListMock);

        // Act
        List<CambioSunatDTO> resultado = cambioSunatService.buscarPorAnoYMes(ano, mes);

        // Assert
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(cambioSunatRepository, times(1)).findByAnoAndMes(2025, 1);
    }

    @Test
    @DisplayName("Buscar por mes diciembre (12)")
    void testBuscarPorMesDiciembre() {
        // Arrange
        String ano = "2025";
        String mes = "12";
        when(cambioSunatRepository.findByAnoAndMes(2025, 12))
                .thenReturn(cambiosListMock);

        // Act
        List<CambioSunatDTO> resultado = cambioSunatService.buscarPorAnoYMes(ano, mes);

        // Assert
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(cambioSunatRepository, times(1)).findByAnoAndMes(2025, 12);
    }

}
