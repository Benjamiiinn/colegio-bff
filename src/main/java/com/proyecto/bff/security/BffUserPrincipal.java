package com.proyecto.bff.security;

import java.util.List;

public record BffUserPrincipal(
        String email,
        Long userId,
        List<String> roles
) {

    public String getRole() {
        if (roles == null || roles.isEmpty()) return null;
        return roles.get(0).replace("ROLE_", "");
    }
}
