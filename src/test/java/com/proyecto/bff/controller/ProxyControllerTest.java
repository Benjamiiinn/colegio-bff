package com.proyecto.bff.controller;

import com.proyecto.bff.security.JwtTokenProvider;
import com.proyecto.bff.security.JwtValidationFilter;
import com.proyecto.bff.config.SecurityConfig;
import com.proyecto.bff.service.ProxyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static com.proyecto.bff.TestSecuritySupport.bffUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProxyController.class)
@Import({SecurityConfig.class, JwtValidationFilter.class})
class ProxyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProxyService proxyService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void getUsuarios_returns200() throws Exception {
        when(proxyService.getList(anyString(), anyString()))
                .thenReturn(new Map[]{Map.of("id", 1, "nombre", "Test")});

        mockMvc.perform(get("/bff/usuarios")
                        .with(bffUser("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void createUsuario_returns200() throws Exception {
        when(proxyService.post(anyString(), any(), anyString()))
                .thenReturn(Map.of("id", 1, "nombre", "Nuevo"));

        mockMvc.perform(post("/bff/usuarios")
                        .with(bffUser("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Nuevo\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUsuario_returns204() throws Exception {
        mockMvc.perform(delete("/bff/usuarios/1")
                        .with(bffUser("ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAnotaciones_returns200() throws Exception {
        when(proxyService.getList(anyString(), anyString()))
                .thenReturn(new Map[]{Map.of("id", 1, "tipo", "POSITIVA")});

        mockMvc.perform(get("/bff/anotaciones")
                        .with(bffUser("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void getUsuarios_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/bff/usuarios"))
                .andExpect(status().isForbidden());
    }
}
