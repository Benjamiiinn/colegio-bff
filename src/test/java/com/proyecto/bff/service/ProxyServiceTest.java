package com.proyecto.bff.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProxyServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Captor
    private ArgumentCaptor<HttpEntity<?>> entityCaptor;

    private ProxyService proxyService;

    private static final String GATEWAY_URL = "http://gateway:9090";
    private static final String JWT = "test-jwt";
    private static final String PATH = "/api/v1/usuarios";

    @BeforeEach
    void setUp() {
        proxyService = new ProxyService(restTemplate, GATEWAY_URL);
    }

    @Test
    void get_returnsMap() {
        Map<String, Object> expected = Map.of("id", 1, "nombre", "Test");
        when(restTemplate.exchange(
                eq(GATEWAY_URL + PATH), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(Map.class)
        )).thenReturn(ResponseEntity.ok(expected));

        Map result = proxyService.get(PATH, JWT);

        assertEquals(expected, result);
    }

    @Test
    void getList_returnsMapArray() {
        Map[] expected = new Map[]{Map.of("id", 1)};
        when(restTemplate.exchange(
                eq(GATEWAY_URL + PATH), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(Map[].class)
        )).thenReturn(ResponseEntity.ok(expected));

        Map[] result = proxyService.getList(PATH, JWT);

        assertArrayEquals(expected, result);
    }

    @Test
    void post_returnsMap() {
        Map<String, Object> body = Map.of("nombre", "Nuevo");
        Map<String, Object> expected = Map.of("id", 1, "nombre", "Nuevo");
        when(restTemplate.exchange(
                eq(GATEWAY_URL + PATH), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(Map.class)
        )).thenReturn(ResponseEntity.ok(expected));

        Map result = proxyService.post(PATH, body, JWT);

        assertEquals(expected, result);
    }

    @Test
    void post_setsContentTypeAndAuthHeaders() {
        when(restTemplate.exchange(
                eq(GATEWAY_URL + PATH), eq(HttpMethod.POST),
                entityCaptor.capture(), eq(Map.class)
        )).thenReturn(ResponseEntity.ok(Map.of()));

        proxyService.post(PATH, Map.of(), JWT);

        HttpEntity<?> captured = entityCaptor.getValue();
        assertEquals(MediaType.APPLICATION_JSON, captured.getHeaders().getContentType());
        assertEquals("Bearer " + JWT, captured.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
    }

    @Test
    void put_returnsMap() {
        Map<String, Object> body = Map.of("nombre", "Actualizado");
        Map<String, Object> expected = Map.of("id", 1, "nombre", "Actualizado");
        when(restTemplate.exchange(
                eq(GATEWAY_URL + PATH + "/1"), eq(HttpMethod.PUT),
                any(HttpEntity.class), eq(Map.class)
        )).thenReturn(ResponseEntity.ok(expected));

        Map result = proxyService.put(PATH + "/1", body, JWT);

        assertEquals(expected, result);
    }

    @Test
    void delete_sendsDeleteRequest() {
        when(restTemplate.exchange(
                eq(GATEWAY_URL + PATH + "/1"), eq(HttpMethod.DELETE),
                any(HttpEntity.class), eq(Void.class)
        )).thenReturn(ResponseEntity.noContent().build());

        proxyService.delete(PATH + "/1", JWT);

        verify(restTemplate).exchange(
                eq(GATEWAY_URL + PATH + "/1"), eq(HttpMethod.DELETE),
                any(HttpEntity.class), eq(Void.class));
    }

    @Test
    void getList_setsBearerAuth() {
        when(restTemplate.exchange(
                eq(GATEWAY_URL + PATH), eq(HttpMethod.GET),
                entityCaptor.capture(), eq(Map[].class)
        )).thenReturn(ResponseEntity.ok(new Map[0]));

        proxyService.getList(PATH, JWT);

        HttpEntity<?> captured = entityCaptor.getValue();
        assertNotNull(captured.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
        assertEquals("Bearer " + JWT, captured.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
    }
}
