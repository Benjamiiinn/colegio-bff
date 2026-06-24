package com.proyecto.bff.service;

import com.proyecto.bff.dto.LoginRequest;
import com.proyecto.bff.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Captor
    private ArgumentCaptor<HttpEntity<Map<String, String>>> httpEntityCaptor;

    private AuthService authService;

    private static final String GATEWAY_URL = "http://gateway:9090";

    @BeforeEach
    void setUp() {
        authService = new AuthService(restTemplate, GATEWAY_URL);
    }

    @Test
    void login_returnsMap() {
        LoginRequest request = new LoginRequest("test@test.com", "password");
        Map<String, Object> expected = Map.of("token", "jwt-token");
        when(restTemplate.postForObject(
                eq(GATEWAY_URL + "/api/v1/auth/authenticate"),
                eq(request), eq(Map.class)
        )).thenReturn(expected);

        Map result = authService.login(request);

        assertEquals(expected, result);
    }

    @Test
    void register_returnsMap() {
        RegisterRequest request = new RegisterRequest(
                "Nombre", "Apellido", "test@test.com", "pass123", "12345678-9", "ADMIN");
        Map<String, Object> expected = Map.of("id", 1L);
        when(restTemplate.postForObject(
                eq(GATEWAY_URL + "/api/v1/auth/register"),
                eq(request), eq(Map.class)
        )).thenReturn(expected);

        Map result = authService.register(request);

        assertEquals(expected, result);
    }

    @Test
    void refreshToken_returnsMap() {
        Map<String, Object> expected = Map.of("token", "new-jwt");
        when(restTemplate.postForObject(
                eq(GATEWAY_URL + "/api/v1/auth/refresh-token"),
                any(Map.class), eq(Map.class)
        )).thenReturn(expected);

        Map result = authService.refreshToken("my-refresh-token");

        assertEquals(expected, result);
    }

    @Test
    void refreshToken_sendsRefreshTokenInBody() {
        ArgumentCaptor<Map> bodyCaptor = ArgumentCaptor.forClass(Map.class);
        when(restTemplate.postForObject(
                eq(GATEWAY_URL + "/api/v1/auth/refresh-token"),
                bodyCaptor.capture(), eq(Map.class)
        )).thenReturn(Map.of());

        authService.refreshToken("my-refresh-token");

        assertEquals("my-refresh-token", bodyCaptor.getValue().get("refreshToken"));
    }

    @Test
    void logout_callsPostForEntity() {
        authService.logout("jwt-token");

        verify(restTemplate).postForEntity(
                eq(GATEWAY_URL + "/api/v1/auth/logout"),
                any(HttpEntity.class), eq(Void.class));
    }

    @Test
    void logout_setsBearerAuthInHeaders() {
        authService.logout("jwt-token");

        verify(restTemplate).postForEntity(
                eq(GATEWAY_URL + "/api/v1/auth/logout"),
                httpEntityCaptor.capture(), eq(Void.class));

        HttpEntity<Map<String, String>> captured = httpEntityCaptor.getValue();
        assertNotNull(captured.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
        assertEquals("Bearer jwt-token", captured.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
        assertEquals(MediaType.APPLICATION_JSON, captured.getHeaders().getContentType());
    }

    @Test
    void logout_sendsJwtInBody() {
        authService.logout("jwt-token");

        verify(restTemplate).postForEntity(
                eq(GATEWAY_URL + "/api/v1/auth/logout"),
                httpEntityCaptor.capture(), eq(Void.class));

        Map<String, String> body = httpEntityCaptor.getValue().getBody();
        assertNotNull(body);
        assertEquals("jwt-token", body.get("jwt"));
    }
}
