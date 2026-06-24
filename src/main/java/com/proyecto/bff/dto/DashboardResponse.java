package com.proyecto.bff.dto;

import java.util.Map;

public record DashboardResponse(
        String rol,
        Map<String, Object> data
) {
}
