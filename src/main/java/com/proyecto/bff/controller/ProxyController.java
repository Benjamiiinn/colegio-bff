package com.proyecto.bff.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.bff.service.ProxyService;

@RestController
@RequestMapping("/api/bff")
public class ProxyController {

    private final ProxyService proxyService;

    public ProxyController(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @GetMapping("/usuarios")
    public ResponseEntity<Map[]> getUsuarios(Authentication auth) {
        return ResponseEntity.ok(proxyService.getList("/api/v1/usuarios", extractJwt(auth)));
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<Map> getUsuario(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(proxyService.get("/api/v1/usuarios/" + id, extractJwt(auth)));
    }

    @PostMapping("/usuarios")
    public ResponseEntity<Map> createUsuario(@RequestBody Map body, Authentication auth) {
        return ResponseEntity.ok(proxyService.post("/api/v1/usuarios", body, extractJwt(auth)));
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<Map> updateUsuario(@PathVariable Long id, @RequestBody Map body, Authentication auth) {
        return ResponseEntity.ok(proxyService.put("/api/v1/usuarios/" + id, body, extractJwt(auth)));
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id, Authentication auth) {
        proxyService.delete("/api/v1/usuarios/" + id, extractJwt(auth));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cursos")
    public ResponseEntity<Map[]> getCursos(Authentication auth) {
        return ResponseEntity.ok(proxyService.getList("/api/v1/cursos", extractJwt(auth)));
    }

    @PostMapping("/cursos")
    public ResponseEntity<Map> createCurso(@RequestBody Map body, Authentication auth) {
        return ResponseEntity.ok(proxyService.post("/api/v1/cursos", body, extractJwt(auth)));
    }

    @PutMapping("/cursos/{id}")
    public ResponseEntity<Map> updateCurso(@PathVariable Long id, @RequestBody Map body, Authentication auth) {
        return ResponseEntity.ok(proxyService.put("/api/v1/cursos/" + id, body, extractJwt(auth)));
    }

    @DeleteMapping("/cursos/{id}")
    public ResponseEntity<Void> deleteCurso(@PathVariable Long id, Authentication auth) {
        proxyService.delete("/api/v1/cursos/" + id, extractJwt(auth));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/asignaturas")
    public ResponseEntity<Map[]> getAsignaturas(Authentication auth) {
        return ResponseEntity.ok(proxyService.getList("/api/v1/asignaturas", extractJwt(auth)));
    }

    @GetMapping("/asignaturas/docente/{idDocente}")
    public ResponseEntity<Map[]> getAsignaturasByDocente(@PathVariable Long idDocente, Authentication auth) {
        return ResponseEntity.ok(proxyService.getList("/api/v1/asignaturas/docente/" + idDocente, extractJwt(auth)));
    }

    @PostMapping("/asignaturas")
    public ResponseEntity<Map> createAsignatura(@RequestBody Map body, Authentication auth) {
        return ResponseEntity.ok(proxyService.post("/api/v1/asignaturas", body, extractJwt(auth)));
    }

    @PutMapping("/asignaturas/{id}")
    public ResponseEntity<Map> updateAsignatura(@PathVariable Long id, @RequestBody Map body, Authentication auth) {
        return ResponseEntity.ok(proxyService.put("/api/v1/asignaturas/" + id, body, extractJwt(auth)));
    }

    @DeleteMapping("/asignaturas/{id}")
    public ResponseEntity<Void> deleteAsignatura(@PathVariable Long id, Authentication auth) {
        proxyService.delete("/api/v1/asignaturas/" + id, extractJwt(auth));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/matriculas")
    public ResponseEntity<Map[]> getMatriculas(Authentication auth) {
        return ResponseEntity.ok(proxyService.getList("/api/v1/matriculas", extractJwt(auth)));
    }

    @GetMapping("/matriculas/curso/{cursoId}")
    public ResponseEntity<Map[]> getMatriculasByCurso(@PathVariable Long cursoId, Authentication auth) {
        return ResponseEntity.ok(proxyService.getList("/api/v1/matriculas/curso/" + cursoId, extractJwt(auth)));
    }

    @PostMapping("/matriculas")
    public ResponseEntity<Map> createMatricula(@RequestBody Map body, Authentication auth) {
        return ResponseEntity.ok(proxyService.post("/api/v1/matriculas", body, extractJwt(auth)));
    }

    @PutMapping("/matriculas/{id}")
    public ResponseEntity<Map> updateMatricula(@PathVariable Long id, @RequestBody Map body, Authentication auth) {
        return ResponseEntity.ok(proxyService.put("/api/v1/matriculas/" + id, body, extractJwt(auth)));
    }

    @DeleteMapping("/matriculas/{id}")
    public ResponseEntity<Void> deleteMatricula(@PathVariable Long id, Authentication auth) {
        proxyService.delete("/api/v1/matriculas/" + id, extractJwt(auth));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/calificaciones")
    public ResponseEntity<Map[]> getCalificaciones(Authentication auth) {
        return ResponseEntity.ok(proxyService.getList("/api/v1/calificaciones", extractJwt(auth)));
    }

    @GetMapping("/calificaciones/estudiante/{estudianteId}")
    public ResponseEntity<Map[]> getCalificacionesByEstudiante(@PathVariable Long estudianteId, Authentication auth) {
        return ResponseEntity.ok(proxyService.getList("/api/v1/calificaciones/estudiante/" + estudianteId, extractJwt(auth)));
    }

    @GetMapping("/calificaciones/mis-calificaciones")
    public ResponseEntity<Map[]> getMisCalificaciones(Authentication auth) {
        return ResponseEntity.ok(proxyService.getList("/api/v1/calificaciones/mis-calificaciones", extractJwt(auth)));
    }

    @PostMapping("/calificaciones")
    public ResponseEntity<Map> createCalificacion(@RequestBody Map body, Authentication auth) {
        return ResponseEntity.ok(proxyService.post("/api/v1/calificaciones", body, extractJwt(auth)));
    }

    @PutMapping("/calificaciones/{id}")
    public ResponseEntity<Map> updateCalificacion(@PathVariable Long id, @RequestBody Map body, Authentication auth) {
        return ResponseEntity.ok(proxyService.put("/api/v1/calificaciones/" + id, body, extractJwt(auth)));
    }

    @DeleteMapping("/calificaciones/{id}")
    public ResponseEntity<Void> deleteCalificacion(@PathVariable Long id, Authentication auth) {
        proxyService.delete("/api/v1/calificaciones/" + id, extractJwt(auth));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/asistencias")
    public ResponseEntity<Map[]> getAsistencias(Authentication auth) {
        return ResponseEntity.ok(proxyService.getList("/api/v1/asistencias", extractJwt(auth)));
    }

    @GetMapping("/asistencias/estado/{estado}")
    public ResponseEntity<Map[]> getAsistenciasByEstado(@PathVariable String estado, Authentication auth) {
        return ResponseEntity.ok(proxyService.getList("/api/v1/asistencias/estado/" + estado, extractJwt(auth)));
    }

    @GetMapping("/asistencias/fecha")
    public ResponseEntity<Map[]> getAsistenciasByFecha(@RequestParam String inicio,
                                                       @RequestParam String fin,
                                                       Authentication auth) {
        return ResponseEntity.ok(proxyService.getList(
                "/api/v1/asistencias/fecha?inicio=" + inicio + "&fin=" + fin, extractJwt(auth)));
    }

    @GetMapping("/asistencias/docente/{idDocente}")
    public ResponseEntity<Map[]> getAsistenciasByDocente(@PathVariable Long idDocente, Authentication auth) {
        return ResponseEntity.ok(proxyService.getList("/api/v1/asistencias/docente/" + idDocente, extractJwt(auth)));
    }

    @PostMapping("/asistencias")
    public ResponseEntity<Map> createAsistencia(@RequestBody Map body, Authentication auth) {
        return ResponseEntity.ok(proxyService.post("/api/v1/asistencias", body, extractJwt(auth)));
    }

    @PutMapping("/asistencias/{id}")
    public ResponseEntity<Map> updateAsistencia(@PathVariable Long id, @RequestBody Map body, Authentication auth) {
        return ResponseEntity.ok(proxyService.put("/api/v1/asistencias/" + id, body, extractJwt(auth)));
    }

    @DeleteMapping("/asistencias/{id}")
    public ResponseEntity<Void> deleteAsistencia(@PathVariable Long id, Authentication auth) {
        proxyService.delete("/api/v1/asistencias/" + id, extractJwt(auth));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/anotaciones")
    public ResponseEntity<Map[]> getAnotaciones(Authentication auth) {
        return ResponseEntity.ok(proxyService.getList("/api/v1/anotaciones", extractJwt(auth)));
    }

    @GetMapping("/anotaciones/tipo/{tipo}")
    public ResponseEntity<Map[]> getAnotacionesByTipo(@PathVariable String tipo, Authentication auth) {
        return ResponseEntity.ok(proxyService.getList("/api/v1/anotaciones/tipo/" + tipo, extractJwt(auth)));
    }

    @GetMapping("/anotaciones/estudiante/{idEstudiante}")
    public ResponseEntity<Map[]> getAnotacionesByEstudiante(@PathVariable Long idEstudiante, Authentication auth) {
        return ResponseEntity.ok(proxyService.getList("/api/v1/anotaciones/estudiante/" + idEstudiante, extractJwt(auth)));
    }

    @GetMapping("/anotaciones/docente/{idDocente}")
    public ResponseEntity<Map[]> getAnotacionesByDocente(@PathVariable Long idDocente, Authentication auth) {
        return ResponseEntity.ok(proxyService.getList("/api/v1/anotaciones/docente/" + idDocente, extractJwt(auth)));
    }

    @PostMapping("/anotaciones")
    public ResponseEntity<Map> createAnotacion(@RequestBody Map body, Authentication auth) {
        return ResponseEntity.ok(proxyService.post("/api/v1/anotaciones", body, extractJwt(auth)));
    }

    @PutMapping("/anotaciones/{id}")
    public ResponseEntity<Map> updateAnotacion(@PathVariable Long id, @RequestBody Map body, Authentication auth) {
        return ResponseEntity.ok(proxyService.put("/api/v1/anotaciones/" + id, body, extractJwt(auth)));
    }

    @DeleteMapping("/anotaciones/{id}")
    public ResponseEntity<Void> deleteAnotacion(@PathVariable Long id, Authentication auth) {
        proxyService.delete("/api/v1/anotaciones/" + id, extractJwt(auth));
        return ResponseEntity.noContent().build();
    }

    private String extractJwt(Authentication auth) {
        if (auth == null || auth.getCredentials() == null) return "";
        return auth.getCredentials().toString();
    }
}
