package com.proyecto.bff.service.strategy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.proyecto.bff.security.BffUserPrincipal;
import com.proyecto.bff.service.ProxyService;

@Component
public class AdminDashboardStrategy implements DashboardStrategy {

    private final ProxyService proxyService;

    public AdminDashboardStrategy(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @Override
    public Map<String, Object> obtenerDatos(BffUserPrincipal principal, String jwt) {
        Map<String, Object> data = new HashMap<>();

        try {
            Map[] usuarios = proxyService.getList("/api/v1/usuarios", jwt);
            Map[] cursos = proxyService.getList("/api/v1/cursos", jwt);
            Map[] asistencias = proxyService.getList("/api/v1/asistencias", jwt);

            int estudiantes = 0;
            int docentes = 0;
            for (Map u : usuarios) {
                String rol = (String) u.get("rol");
                if (rol != null && rol.contains("ESTUDIANTE")) estudiantes++;
                if (rol != null && rol.contains("DOCENTE")) docentes++;
            }

            data.put("totalUsuarios", usuarios.length);
            data.put("totalDocentes", docentes);
            data.put("totalEstudiantes", estudiantes);
            data.put("totalCursos", cursos != null ? cursos.length : 0);
            data.put("totalAsistencias", asistencias != null ? asistencias.length : 0);
            data.put("usuariosRecientes", usuarios.length > 5 ? Arrays.copyOfRange(usuarios, 0, 5) : usuarios);
        } catch (Exception e) {
            data.put("error", "Error al obtener datos del dashboard: " + e.getMessage());
        }

        return data;
    }
}
