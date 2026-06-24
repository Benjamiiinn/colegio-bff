package com.proyecto.bff.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProxyService {

    private final RestTemplate restTemplate;
    private final String gatewayBaseUrl;

    public ProxyService(RestTemplate restTemplate,
                        @Value("${api.gateway.base-url}") String gatewayBaseUrl) {
        this.restTemplate = restTemplate;
        this.gatewayBaseUrl = gatewayBaseUrl;
    }

    public Map get(String path, String jwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(
                gatewayBaseUrl + path,
                HttpMethod.GET,
                entity,
                Map.class).getBody();
    }

    public Map[] getList(String path, String jwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(
                gatewayBaseUrl + path,
                HttpMethod.GET,
                entity,
                Map[].class).getBody();
    }

    public Map post(String path, Object body, String jwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(
                gatewayBaseUrl + path,
                HttpMethod.POST,
                entity,
                Map.class).getBody();
    }

    public Map put(String path, Object body, String jwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(
                gatewayBaseUrl + path,
                HttpMethod.PUT,
                entity,
                Map.class).getBody();
    }

    public void delete(String path, String jwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        restTemplate.exchange(
                gatewayBaseUrl + path,
                HttpMethod.DELETE,
                entity,
                Void.class);
    }
}
