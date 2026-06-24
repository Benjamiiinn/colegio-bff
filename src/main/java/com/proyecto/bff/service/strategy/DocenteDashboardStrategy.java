package com.proyecto.bff.service.strategy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.proyecto.bff.security.BffUserPrincipal;
import com.proyecto.bff.service.ProxyService;

@Component
public class DocenteDashboardStrategy implements DashboardStrategy {

    private final ProxyService proxyService;

    public DocenteDashboardStrategy(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @Override
    public Map<String, Object> obtenerDatos(BffUserPrincipal principal, String jwt) {
        Map<String, Object> data = new HashMap<>();
        Long userId = principal.userId();

        try {
            Map[] asignaturas = proxyService.getList("/api/v1/asignaturas/docente/" + userId, jwt);
            Map[] calificaciones = proxyService.getList("/api/v1/calificaciones/mis-calificaciones", jwt);
            Map[] anotaciones = proxyService.getList("/api/v1/anotaciones/docente/" + userId, jwt);

            data.put("totalAsignaturas", asignaturas != null ? asignaturas.length : 0);
            data.put("totalCalificaciones", calificaciones != null ? calificaciones.length : 0);
            data.put("ultimasAnotaciones", anotaciones != null && anotaciones.length > 5
                    ? Arrays.copyOfRange(anotaciones, anotaciones.length - 5, anotaciones.length)
                    : anotaciones);
        } catch (Exception e) {
            data.put("error", "Error al obtener datos del dashboard: " + e.getMessage());
        }

        return data;
    }
}
