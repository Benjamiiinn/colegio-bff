package com.proyecto.bff;

import com.proyecto.bff.security.BffUserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

public class TestSecuritySupport {

    public static RequestPostProcessor bffUser(String role) {
        BffUserPrincipal principal = new BffUserPrincipal(
                "test@test.com", 1L, List.of("ROLE_" + role));
        return authentication(new UsernamePasswordAuthenticationToken(
                principal, "jwt-token",
                List.of(new SimpleGrantedAuthority("ROLE_" + role))));
    }
}
