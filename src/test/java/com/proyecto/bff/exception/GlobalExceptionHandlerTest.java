package com.proyecto.bff.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleBffException_returns400() {
        ResponseEntity<Map<String, Object>> response = handler.handleBffException(new BffException("Error de negocio"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error", response.getBody().get("error"));
        assertEquals("Error de negocio", response.getBody().get("message"));
    }

    @Test
    void handleHttpStatusCodeException_returns400() {
        HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.BAD_REQUEST);

        ResponseEntity<Map<String, Object>> response = handler.handleHttpStatusCode(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Upstream Error", response.getBody().get("error"));
    }

    @Test
    void handleHttpStatusCodeException_returns404() {
        HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.NOT_FOUND);

        ResponseEntity<Map<String, Object>> response = handler.handleHttpStatusCode(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Upstream Error", response.getBody().get("error"));
    }

    @Test
    void handleGenericException_returns500() {
        ResponseEntity<Map<String, Object>> response = handler.handleGeneric(new Exception("Error inesperado"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal Error", response.getBody().get("error"));
        assertEquals("Error inesperado", response.getBody().get("message"));
    }
}
