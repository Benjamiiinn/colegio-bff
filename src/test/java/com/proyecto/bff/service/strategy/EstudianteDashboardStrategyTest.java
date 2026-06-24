package com.proyecto.bff.service.strategy;

import com.proyecto.bff.security.BffUserPrincipal;
import com.proyecto.bff.service.ProxyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudianteDashboardStrategyTest {

    @Mock
    private ProxyService proxyService;

    private EstudianteDashboardStrategy strategy;

    private static final String JWT = "test-jwt";
    private static final Long USER_ID = 5L;
    private final BffUserPrincipal principal = new BffUserPrincipal("estudiante@test.com", USER_ID, List.of("ROLE_ESTUDIANTE"));

    @BeforeEach
    void setUp() {
        strategy = new EstudianteDashboardStrategy(proxyService);
    }

    @Test
    void obtenerDatos_returnsData() {
        Map[] calificaciones = new Map[]{
                Map.of("id", 1, "nota", 7.0),
                Map.of("id", 2, "nota", 8.0)
        };
        Map[] anotaciones = new Map[]{
                Map.of("id", 1, "descripcion", "Anotacion 1")
        };
        Map[] asistencias = new Map[]{
                Map.of("idEstudiante", USER_ID, "estadoAsistencia", "PRESENTE"),
                Map.of("idEstudiante", USER_ID, "estadoAsistencia", "PRESENTE"),
                Map.of("idEstudiante", USER_ID, "estadoAsistencia", "AUSENTE")
        };

        when(proxyService.getList("/api/v1/calificaciones/estudiante/" + USER_ID, JWT)).thenReturn(calificaciones);
        when(proxyService.getList("/api/v1/anotaciones/estudiante/" + USER_ID, JWT)).thenReturn(anotaciones);
        when(proxyService.getList("/api/v1/asistencias", JWT)).thenReturn(asistencias);

        Map<String, Object> result = strategy.obtenerDatos(principal, JWT);

        assertEquals(2, result.get("totalNotas"));
        assertEquals(2L, result.get("asistenciasPresentes"));
        assertNotNull(result.get("ultimasAnotaciones"));
    }

    @Test
    void obtenerDatos_calculaPromedioCorrectamente() {
        Map[] calificaciones = new Map[]{
                Map.of("id", 1, "nota", 70),
                Map.of("id", 2, "nota", 80)
        };

        when(proxyService.getList("/api/v1/calificaciones/estudiante/" + USER_ID, JWT)).thenReturn(calificaciones);
        when(proxyService.getList("/api/v1/anotaciones/estudiante/" + USER_ID, JWT)).thenReturn(new Map[0]);
        when(proxyService.getList("/api/v1/asistencias", JWT)).thenReturn(new Map[0]);

        Map<String, Object> result = strategy.obtenerDatos(principal, JWT);

        assertEquals(75.0, result.get("promedioGeneral"));
    }

    @Test
    void obtenerDatos_cuentaSoloAsistenciasPresentesDelUsuario() {
        Map[] asistencias = new Map[]{
                Map.of("idEstudiante", USER_ID, "estadoAsistencia", "PRESENTE"),
                Map.of("idEstudiante", USER_ID, "estadoAsistencia", "AUSENTE"),
                Map.of("idEstudiante", 99, "estadoAsistencia", "PRESENTE"),
                Map.of("idEstudiante", USER_ID, "estadoAsistencia", "PRESENTE")
        };

        when(proxyService.getList("/api/v1/calificaciones/estudiante/" + USER_ID, JWT)).thenReturn(new Map[0]);
        when(proxyService.getList("/api/v1/anotaciones/estudiante/" + USER_ID, JWT)).thenReturn(new Map[0]);
        when(proxyService.getList("/api/v1/asistencias", JWT)).thenReturn(asistencias);

        Map<String, Object> result = strategy.obtenerDatos(principal, JWT);

        assertEquals(2L, result.get("asistenciasPresentes"));
    }

    @Test
    void obtenerDatos_handlesEmptyArrays() {
        when(proxyService.getList("/api/v1/calificaciones/estudiante/" + USER_ID, JWT)).thenReturn(new Map[0]);
        when(proxyService.getList("/api/v1/anotaciones/estudiante/" + USER_ID, JWT)).thenReturn(new Map[0]);
        when(proxyService.getList("/api/v1/asistencias", JWT)).thenReturn(new Map[0]);

        Map<String, Object> result = strategy.obtenerDatos(principal, JWT);

        assertEquals(0, result.get("totalNotas"));
        assertEquals(0.0, result.get("promedioGeneral"));
        assertEquals(0L, result.get("asistenciasPresentes"));
    }

    @Test
    void obtenerDatos_error_returnsErrorEntry() {
        when(proxyService.getList("/api/v1/calificaciones/estudiante/" + USER_ID, JWT))
                .thenThrow(new RuntimeException("Error de conexion"));

        Map<String, Object> result = strategy.obtenerDatos(principal, JWT);

        assertTrue(result.containsKey("error"));
        assertTrue(((String) result.get("error")).contains("Error de conexion"));
    }
}
