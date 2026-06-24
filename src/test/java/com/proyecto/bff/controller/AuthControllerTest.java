package com.proyecto.bff.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.bff.dto.LoginRequest;
import com.proyecto.bff.dto.RegisterRequest;
import com.proyecto.bff.security.JwtTokenProvider;
import com.proyecto.bff.security.JwtValidationFilter;
import com.proyecto.bff.config.SecurityConfig;
import com.proyecto.bff.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtValidationFilter.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void authenticate_returns200() throws Exception {
        LoginRequest request = new LoginRequest("test@test.com", "password");
        when(authService.login(any())).thenReturn(Map.of("token", "jwt-value", "email", "test@test.com"));

        mockMvc.perform(post("/bff/auth/authenticate")

                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("jwt-cookie")));
    }

    @Test
    void register_returns200() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Nombre", "Apellido", "test@test.com", "pass123", "12345678-9", "ADMIN");
        when(authService.register(any())).thenReturn(Map.of("id", 1L));

        mockMvc.perform(post("/bff/auth/register")

                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void refreshToken_returns200() throws Exception {
        when(authService.refreshToken(any())).thenReturn(Map.of("token", "new-jwt"));

        mockMvc.perform(post("/bff/auth/refresh-token")

                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"my-refresh\"}"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Set-Cookie"));
    }

    @Test
    void logout_returns204() throws Exception {
        mockMvc.perform(post("/bff/auth/logout")

                        .cookie(new jakarta.servlet.http.Cookie("jwt-cookie", "some-jwt")))
                .andExpect(status().isNoContent())
                .andExpect(header().exists("Set-Cookie"));
    }

    @Test
    void authenticate_withInvalidEmail_returns400() throws Exception {
        mockMvc.perform(post("/bff/auth/authenticate")

                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"invalido\",\"password\":\"123\"}"))
                .andExpect(status().isBadRequest());
    }
}
