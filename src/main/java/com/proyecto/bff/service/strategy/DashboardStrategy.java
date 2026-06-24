package com.proyecto.bff.service.strategy;

import java.util.Map;

import com.proyecto.bff.security.BffUserPrincipal;

public interface DashboardStrategy {

    Map<String, Object> obtenerDatos(BffUserPrincipal principal, String jwt);
}
