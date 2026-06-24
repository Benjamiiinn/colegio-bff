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
class AdminDashboardStrategyTest {

    @Mock
    private ProxyService proxyService;

    private AdminDashboardStrategy strategy;

    private static final String JWT = "test-jwt";
    private final BffUserPrincipal principal = new BffUserPrincipal("admin@test.com", 1L, List.of("ROLE_ADMIN"));

    @BeforeEach
    void setUp() {
        strategy = new AdminDashboardStrategy(proxyService);
    }

    @Test
    void obtenerDatos_returnsAllCounts() {
        Map[] usuarios = new Map[]{
                Map.of("id", 1, "rol", "ROLE_ESTUDIANTE"),
                Map.of("id", 2, "rol", "ROLE_DOCENTE")
        };
        Map[] cursos = new Map[]{Map.of("id", 1), Map.of("id", 2)};
        Map[] asistencias = new Map[]{Map.of("id", 1)};

        when(proxyService.getList("/api/v1/usuarios", JWT)).thenReturn(usuarios);
        when(proxyService.getList("/api/v1/cursos", JWT)).thenReturn(cursos);
        when(proxyService.getList("/api/v1/asistencias", JWT)).thenReturn(asistencias);

        Map<String, Object> result = strategy.obtenerDatos(principal, JWT);

        assertEquals(2, result.get("totalUsuarios"));
        assertEquals(1, result.get("totalDocentes"));
        assertEquals(1, result.get("totalEstudiantes"));
        assertEquals(2, result.get("totalCursos"));
        assertEquals(1, result.get("totalAsistencias"));
    }

    @Test
    void obtenerDatos_countsRolesCorrectlyWithMultipleUsers() {
        Map[] usuarios = new Map[]{
                Map.of("id", 1, "rol", "ROLE_ESTUDIANTE"),
                Map.of("id", 2, "rol", "ROLE_ESTUDIANTE"),
                Map.of("id", 3, "rol", "ROLE_DOCENTE"),
                Map.of("id", 4, "rol", "ROLE_ESTUDIANTE"),
                Map.of("id", 5, "rol", "ROLE_ADMIN")
        };

        when(proxyService.getList("/api/v1/usuarios", JWT)).thenReturn(usuarios);
        when(proxyService.getList("/api/v1/cursos", JWT)).thenReturn(new Map[0]);
        when(proxyService.getList("/api/v1/asistencias", JWT)).thenReturn(new Map[0]);

        Map<String, Object> result = strategy.obtenerDatos(principal, JWT);

        assertEquals(5, result.get("totalUsuarios"));
        assertEquals(1, result.get("totalDocentes"));
        assertEquals(3, result.get("totalEstudiantes"));
    }

    @Test
    void obtenerDatos_includesUsuariosRecientes_whenMoreThan5() {
        Map[] usuarios = new Map[7];
        for (int i = 0; i < 7; i++) {
            usuarios[i] = Map.of("id", i + 1, "rol", "ROLE_ESTUDIANTE");
        }

        when(proxyService.getList("/api/v1/usuarios", JWT)).thenReturn(usuarios);
        when(proxyService.getList("/api/v1/cursos", JWT)).thenReturn(new Map[0]);
        when(proxyService.getList("/api/v1/asistencias", JWT)).thenReturn(new Map[0]);

        Map<String, Object> result = strategy.obtenerDatos(principal, JWT);

        Map[] recientes = (Map[]) result.get("usuariosRecientes");
        assertNotNull(recientes);
        assertEquals(5, recientes.length);
        assertEquals(1, recientes[0].get("id"));
        assertEquals(5, recientes[4].get("id"));
    }

    @Test
    void obtenerDatos_error_returnsErrorEntry() {
        when(proxyService.getList("/api/v1/usuarios", JWT))
                .thenThrow(new RuntimeException("Error de conexion"));

        Map<String, Object> result = strategy.obtenerDatos(principal, JWT);

        assertTrue(result.containsKey("error"));
        assertTrue(((String) result.get("error")).contains("Error de conexion"));
    }
}
