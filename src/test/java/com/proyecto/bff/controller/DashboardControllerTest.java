package com.proyecto.bff.controller;

import com.proyecto.bff.config.SecurityConfig;
import com.proyecto.bff.security.JwtTokenProvider;
import com.proyecto.bff.security.JwtValidationFilter;
import com.proyecto.bff.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static com.proyecto.bff.TestSecuritySupport.bffUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@Import({SecurityConfig.class, JwtValidationFilter.class})
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void dashboard_withAdminRole_returns200() throws Exception {
        when(dashboardService.obtenerDashboard(any(), anyString()))
                .thenReturn(Map.of("totalUsuarios", 10));

        mockMvc.perform(get("/api/bff/dashboard")
                        .with(bffUser("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("ADMIN"))
                .andExpect(jsonPath("$.data.totalUsuarios").value(10));
    }

    @Test
    void dashboard_withDocenteRole_returns200() throws Exception {
        when(dashboardService.obtenerDashboard(any(), anyString()))
                .thenReturn(Map.of("totalAsignaturas", 5));

        mockMvc.perform(get("/api/bff/dashboard")
                        .with(bffUser("DOCENTE")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("DOCENTE"));
    }

    @Test
    void dashboard_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/api/bff/dashboard"))
                .andExpect(status().isForbidden());
    }
}
