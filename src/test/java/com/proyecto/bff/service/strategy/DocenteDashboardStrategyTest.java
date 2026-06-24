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
class DocenteDashboardStrategyTest {

    @Mock
    private ProxyService proxyService;

    private DocenteDashboardStrategy strategy;

    private static final String JWT = "test-jwt";
    private static final Long USER_ID = 10L;
    private final BffUserPrincipal principal = new BffUserPrincipal("docente@test.com", USER_ID, List.of("ROLE_DOCENTE"));

    @BeforeEach
    void setUp() {
        strategy = new DocenteDashboardStrategy(proxyService);
    }

    @Test
    void obtenerDatos_returnsData() {
        Map[] asignaturas = new Map[]{
                Map.of("id", 1, "nombre", "Matematicas"),
                Map.of("id", 2, "nombre", "Lenguaje")
        };
        Map[] calificaciones = new Map[]{
                Map.of("id", 1, "nota", 6.5),
                Map.of("id", 2, "nota", 7.0)
        };
        Map[] anotaciones = new Map[]{
                Map.of("id", 1, "descripcion", "Anotacion 1")
        };

        when(proxyService.getList("/api/v1/asignaturas/docente/" + USER_ID, JWT)).thenReturn(asignaturas);
        when(proxyService.getList("/api/v1/calificaciones/mis-calificaciones", JWT)).thenReturn(calificaciones);
        when(proxyService.getList("/api/v1/anotaciones/docente/" + USER_ID, JWT)).thenReturn(anotaciones);

        Map<String, Object> result = strategy.obtenerDatos(principal, JWT);

        assertEquals(2, result.get("totalAsignaturas"));
        assertEquals(2, result.get("totalCalificaciones"));
        assertNotNull(result.get("ultimasAnotaciones"));
    }

    @Test
    void obtenerDatos_limitsAnotacionesTo5() {
        Map[] anotaciones = new Map[10];
        for (int i = 0; i < 10; i++) {
            anotaciones[i] = Map.of("id", i + 1, "descripcion", "A" + (i + 1));
        }

        when(proxyService.getList("/api/v1/asignaturas/docente/" + USER_ID, JWT)).thenReturn(new Map[0]);
        when(proxyService.getList("/api/v1/calificaciones/mis-calificaciones", JWT)).thenReturn(new Map[0]);
        when(proxyService.getList("/api/v1/anotaciones/docente/" + USER_ID, JWT)).thenReturn(anotaciones);

        Map<String, Object> result = strategy.obtenerDatos(principal, JWT);

        Map[] ultimas = (Map[]) result.get("ultimasAnotaciones");
        assertNotNull(ultimas);
        assertEquals(5, ultimas.length);
        assertEquals(6, ultimas[0].get("id"));
        assertEquals(10, ultimas[4].get("id"));
    }

    @Test
    void obtenerDatos_handlesNullLists() {
        when(proxyService.getList("/api/v1/asignaturas/docente/" + USER_ID, JWT)).thenReturn(null);
        when(proxyService.getList("/api/v1/calificaciones/mis-calificaciones", JWT)).thenReturn(null);
        when(proxyService.getList("/api/v1/anotaciones/docente/" + USER_ID, JWT)).thenReturn(null);

        Map<String, Object> result = strategy.obtenerDatos(principal, JWT);

        assertEquals(0, result.get("totalAsignaturas"));
        assertEquals(0, result.get("totalCalificaciones"));
    }

    @Test
    void obtenerDatos_error_returnsErrorEntry() {
        when(proxyService.getList("/api/v1/asignaturas/docente/" + USER_ID, JWT))
                .thenThrow(new RuntimeException("Error de conexion"));

        Map<String, Object> result = strategy.obtenerDatos(principal, JWT);

        assertTrue(result.containsKey("error"));
        assertTrue(((String) result.get("error")).contains("Error de conexion"));
    }
}
