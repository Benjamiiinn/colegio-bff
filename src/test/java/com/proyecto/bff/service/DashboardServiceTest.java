package com.proyecto.bff.service;

import com.proyecto.bff.security.BffUserPrincipal;
import com.proyecto.bff.service.strategy.AdminDashboardStrategy;
import com.proyecto.bff.service.strategy.DocenteDashboardStrategy;
import com.proyecto.bff.service.strategy.EstudianteDashboardStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private AdminDashboardStrategy adminStrategy;

    @Mock
    private DocenteDashboardStrategy docenteStrategy;

    @Mock
    private EstudianteDashboardStrategy estudianteStrategy;

    private DashboardService dashboardService;

    private static final String JWT = "test-jwt";

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardService(adminStrategy, docenteStrategy, estudianteStrategy);
    }

    @Test
    void obtenerDashboard_withAdminRole_callsAdminStrategy() {
        BffUserPrincipal principal = new BffUserPrincipal("admin@test.com", 1L, List.of("ROLE_ADMIN"));
        Map<String, Object> expected = Map.of("totalUsuarios", 10);
        when(adminStrategy.obtenerDatos(principal, JWT)).thenReturn(expected);

        Map<String, Object> result = dashboardService.obtenerDashboard(principal, JWT);

        assertEquals(expected, result);
        verify(adminStrategy).obtenerDatos(principal, JWT);
    }

    @Test
    void obtenerDashboard_withDocenteRole_callsDocenteStrategy() {
        BffUserPrincipal principal = new BffUserPrincipal("docente@test.com", 2L, List.of("ROLE_DOCENTE"));
        Map<String, Object> expected = Map.of("totalAsignaturas", 5);
        when(docenteStrategy.obtenerDatos(principal, JWT)).thenReturn(expected);

        Map<String, Object> result = dashboardService.obtenerDashboard(principal, JWT);

        assertEquals(expected, result);
        verify(docenteStrategy).obtenerDatos(principal, JWT);
    }

    @Test
    void obtenerDashboard_withEstudianteRole_callsEstudianteStrategy() {
        BffUserPrincipal principal = new BffUserPrincipal("estudiante@test.com", 3L, List.of("ROLE_ESTUDIANTE"));
        Map<String, Object> expected = Map.of("promedioGeneral", 6.5);
        when(estudianteStrategy.obtenerDatos(principal, JWT)).thenReturn(expected);

        Map<String, Object> result = dashboardService.obtenerDashboard(principal, JWT);

        assertEquals(expected, result);
        verify(estudianteStrategy).obtenerDatos(principal, JWT);
    }

    @Test
    void obtenerDashboard_withInvalidRole_throwsIllegalArgumentException() {
        BffUserPrincipal principal = new BffUserPrincipal("other@test.com", 4L, List.of("ROLE_OTRO"));

        assertThrows(IllegalArgumentException.class,
                () -> dashboardService.obtenerDashboard(principal, JWT));
    }

    @Test
    void obtenerDashboard_returnsDataFromStrategy() {
        BffUserPrincipal principal = new BffUserPrincipal("admin@test.com", 1L, List.of("ROLE_ADMIN"));
        Map<String, Object> strategyData = Map.of(
                "totalUsuarios", 10,
                "totalDocentes", 2,
                "totalEstudiantes", 5
        );
        when(adminStrategy.obtenerDatos(any(), anyString())).thenReturn(strategyData);

        Map<String, Object> result = dashboardService.obtenerDashboard(principal, JWT);

        assertEquals(strategyData, result);
    }
}
