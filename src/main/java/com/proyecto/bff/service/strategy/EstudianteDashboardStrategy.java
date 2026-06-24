package com.proyecto.bff.service.strategy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.proyecto.bff.security.BffUserPrincipal;
import com.proyecto.bff.service.ProxyService;

@Component
public class EstudianteDashboardStrategy implements DashboardStrategy {

    private final ProxyService proxyService;

    public EstudianteDashboardStrategy(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @Override
    public Map<String, Object> obtenerDatos(BffUserPrincipal principal, String jwt) {
        Map<String, Object> data = new HashMap<>();
        Long userId = principal.userId();

        try {
            Map[] calificaciones = proxyService.getList("/api/v1/calificaciones/estudiante/" + userId, jwt);
            Map[] anotaciones = proxyService.getList("/api/v1/anotaciones/estudiante/" + userId, jwt);
            Map[] asistencias = proxyService.getList("/api/v1/asistencias", jwt);

            double promedio = 0;
            for (Map c : calificaciones) {
                Object nota = c.get("nota");
                if (nota instanceof Number) {
                    promedio += ((Number) nota).doubleValue();
                }
            }
            if (calificaciones.length > 0) {
                promedio = Math.round((promedio / calificaciones.length) * 10.0) / 10.0;
            }

            long presentes = Arrays.stream(asistencias)
                    .filter(a -> userId.equals(a.get("idEstudiante")) && "PRESENTE".equals(a.get("estadoAsistencia")))
                    .count();

            data.put("totalNotas", calificaciones != null ? calificaciones.length : 0);
            data.put("promedioGeneral", promedio);
            data.put("asistenciasPresentes", presentes);
            data.put("ultimasAnotaciones", anotaciones != null && anotaciones.length > 5
                    ? Arrays.copyOfRange(anotaciones, anotaciones.length - 5, anotaciones.length)
                    : anotaciones);
        } catch (Exception e) {
            data.put("error", "Error al obtener datos del dashboard: " + e.getMessage());
        }

        return data;
    }
}
