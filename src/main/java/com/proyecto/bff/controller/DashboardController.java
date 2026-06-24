package com.proyecto.bff.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.bff.dto.DashboardResponse;
import com.proyecto.bff.security.BffUserPrincipal;
import com.proyecto.bff.service.DashboardService;

@RestController
@RequestMapping("/bff")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> dashboard(Authentication auth) {
        BffUserPrincipal principal = (BffUserPrincipal) auth.getPrincipal();
        String jwt = auth.getCredentials().toString();
        String role = principal.getRole();

        return ResponseEntity.ok(
                new DashboardResponse(role, dashboardService.obtenerDashboard(principal, jwt)));
    }
}
