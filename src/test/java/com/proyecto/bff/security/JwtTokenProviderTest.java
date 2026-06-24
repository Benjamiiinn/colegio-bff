package com.proyecto.bff.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JwtTokenProviderTest {

    private static final String SECRET_KEY = "586B633834416E396D7436753879382F423F4428482B4C6250655367566B5970";

    private JwtTokenProvider provider;
    private String token;

    @BeforeEach
    void setUp() {
        provider = new JwtTokenProvider(SECRET_KEY);
        token = Jwts.builder()
                .setSubject("test@test.com")
                .claim("userId", 1L)
                .claim("roles", List.of("ROLE_ADMIN", "ROLE_DOCENTE"))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY)))
                .compact();
    }

    @Test
    void validateToken_returnsClaims() {
        var claims = provider.validateToken(token);
        assertNotNull(claims);
        assertEquals("test@test.com", claims.getSubject());
    }

    @Test
    void getEmailFromToken_returnsEmail() {
        String email = provider.getEmailFromToken(token);
        assertEquals("test@test.com", email);
    }

    @Test
    void getUserIdFromToken_returnsUserId() {
        Long userId = provider.getUserIdFromToken(token);
        assertEquals(1L, userId);
    }

    @Test
    void getRolesFromToken_returnsRoles() {
        List<String> roles = provider.getRolesFromToken(token);
        assertNotNull(roles);
        assertEquals(2, roles.size());
        assertEquals("ROLE_ADMIN", roles.get(0));
        assertEquals("ROLE_DOCENTE", roles.get(1));
    }
}
