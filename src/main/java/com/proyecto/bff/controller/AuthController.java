package com.proyecto.bff.controller;

import java.time.Duration;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.bff.dto.LoginRequest;
import com.proyecto.bff.dto.RegisterRequest;
import com.proyecto.bff.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/bff/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Map> authenticate(@Valid @RequestBody LoginRequest request) {
        Map response = authService.login(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, createJwtCookie(response).toString())
                .body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map> refreshToken(@RequestBody Map<String, String> body) {
        Map response = authService.refreshToken(body.get("refreshToken"));
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, createJwtCookie(response).toString())
                .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = "jwt-cookie", required = false) String jwt) {
        if (jwt != null && !jwt.isEmpty()) {
            authService.logout(jwt);
        }
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE,
                        ResponseCookie.from("jwt-cookie", "")
                                .httpOnly(true)
                                .path("/")
                                .maxAge(Duration.ZERO)
                                .sameSite("Lax")
                                .build().toString())
                .build();
    }

    private ResponseCookie createJwtCookie(Map response) {
        String token = (String) response.get("token");
        if (token == null) token = (String) response.get("jwt");
        if (token == null) token = (String) response.get("access_token");
        return ResponseCookie.from("jwt-cookie", token != null ? token : "")
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .sameSite("Lax")
                .build();
    }
}
