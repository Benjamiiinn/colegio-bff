package com.proyecto.bff.security;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BffUserPrincipalTest {

    @Test
    void getRole_removesRolePrefix() {
        BffUserPrincipal principal = new BffUserPrincipal("admin@test.com", 1L, List.of("ROLE_ADMIN"));
        assertEquals("ADMIN", principal.getRole());
    }

    @Test
    void getRole_returnsNull_whenRolesNull() {
        BffUserPrincipal principal = new BffUserPrincipal("test@test.com", 1L, null);
        assertNull(principal.getRole());
    }

    @Test
    void getRole_returnsNull_whenRolesEmpty() {
        BffUserPrincipal principal = new BffUserPrincipal("test@test.com", 1L, Collections.emptyList());
        assertNull(principal.getRole());
    }
}
