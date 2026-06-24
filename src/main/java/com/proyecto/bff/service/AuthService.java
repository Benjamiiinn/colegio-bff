package com.proyecto.bff.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.proyecto.bff.dto.LoginRequest;
import com.proyecto.bff.dto.RegisterRequest;

@Service
public class AuthService {

    private final RestTemplate restTemplate;
    private final String gatewayBaseUrl;

    public AuthService(RestTemplate restTemplate,
                       @Value("${api.gateway.base-url}") String gatewayBaseUrl) {
        this.restTemplate = restTemplate;
        this.gatewayBaseUrl = gatewayBaseUrl;
    }

    public Map login(LoginRequest request) {
        return restTemplate.postForObject(
                gatewayBaseUrl + "/api/v1/auth/authenticate",
                request,
                Map.class);
    }

    public Map register(RegisterRequest request) {
        return restTemplate.postForObject(
                gatewayBaseUrl + "/api/v1/auth/register",
                request,
                Map.class);
    }

    public Map refreshToken(String refreshToken) {
        return restTemplate.postForObject(
                gatewayBaseUrl + "/api/v1/auth/refresh-token",
                Map.of("refreshToken", refreshToken),
                Map.class);
    }

    public void logout(String jwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(Map.of("jwt", jwt), headers);
        restTemplate.postForEntity(
                gatewayBaseUrl + "/api/v1/auth/logout",
                entity,
                Void.class);
    }
}
