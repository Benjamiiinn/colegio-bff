package com.proyecto.bff.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.proyecto.bff.security.BffUserPrincipal;
import com.proyecto.bff.service.strategy.AdminDashboardStrategy;
import com.proyecto.bff.service.strategy.DashboardStrategy;
import com.proyecto.bff.service.strategy.DocenteDashboardStrategy;
import com.proyecto.bff.service.strategy.EstudianteDashboardStrategy;

@Service
public class DashboardService {

    private final AdminDashboardStrategy adminStrategy;
    private final DocenteDashboardStrategy docenteStrategy;
    private final EstudianteDashboardStrategy estudianteStrategy;

    public DashboardService(AdminDashboardStrategy adminStrategy,
                            DocenteDashboardStrategy docenteStrategy,
                            EstudianteDashboardStrategy estudianteStrategy) {
        this.adminStrategy = adminStrategy;
        this.docenteStrategy = docenteStrategy;
        this.estudianteStrategy = estudianteStrategy;
    }

    public Map<String, Object> obtenerDashboard(BffUserPrincipal principal, String jwt) {
        DashboardStrategy strategy = selectStrategy(principal);
        return strategy.obtenerDatos(principal, jwt);
    }

    private DashboardStrategy selectStrategy(BffUserPrincipal principal) {
        String role = principal.getRole();
        return switch (role) {
            case "ADMIN" -> adminStrategy;
            case "DOCENTE" -> docenteStrategy;
            case "ESTUDIANTE" -> estudianteStrategy;
            default -> throw new IllegalArgumentException("Rol no soportado: " + role);
        };
    }
}
