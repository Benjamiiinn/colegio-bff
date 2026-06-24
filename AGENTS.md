# BFF Service — Información del Proyecto

## Stack
- **Java 21** + Spring Boot 4.0.6 (WebMVC, Security, Validation, Cache, Actuator)
- **Maven** (Build + Dependencies)
- **JWT** (jjwt 0.11.5) para autenticación via cookie HttpOnly
- **Lombok** (optional, solo anotaciones)

## Puerto
- **9093** (Docker) / `server.port=9093` (local)

## Arquitectura
Backend For Frontend (BFF) que actúa como proxy entre el frontend React y un API Gateway. Todas las rutas públicas empiezan con `/bff/`.

## Estructura de paquetes

```
com.proyecto.bff
├── BffServiceApplication.java          # Entry point
├── config/
│   ├── CacheConfig.java                # Habilita @EnableCaching
│   ├── RestTemplateConfig.java         # RestTemplate bean (timeout 5s connect, 30s read)
│   └── SecurityConfig.java             # SecurityFilterChain stateless, JWT filter
├── controller/
│   ├── AuthController.java             # /bff/auth/* (login, register, refresh, logout)
│   ├── DashboardController.java        # GET /bff/dashboard
│   └── ProxyController.java            # /bff/{usuarios,cursos,asignaturas,matriculas,calificaciones,asistencias,anotaciones}
├── dto/
│   ├── DashboardResponse.java          # Record: rol + data
│   ├── LoginRequest.java               # Record: email + password (@NotBlank @Email)
│   └── RegisterRequest.java            # Record: nombres, apellidos, email, password, rut, rol
├── exception/
│   ├── BffException.java               # RuntimeException personalizada
│   └── GlobalExceptionHandler.java     # @RestControllerAdvice (BffException, HttpStatusCodeException, Validation, generic)
├── security/
│   ├── BffUserPrincipal.java           # Record: email, userId, roles — método getRole() extrae ROLE_
│   ├── JwtTokenProvider.java           # Parse/validate JWT, extrae claims (email, userId, roles)
│   └── JwtValidationFilter.java        # OncePerRequestFilter, lee jwt-cookie o Authorization Bearer
└── service/
    ├── AuthService.java                # Llama a /api/v1/auth/* en el gateway
    ├── DashboardService.java           # Strategy pattern: selecciona strategy según rol
    ├── ProxyService.java               # CRUD genérico via RestTemplate (get, getList, post, put, delete)
    └── strategy/
        ├── DashboardStrategy.java      # Interface: obtenerDatos(principal, jwt)
        ├── AdminDashboardStrategy.java # Estadísticas totales (usuarios, cursos, asistencias)
        ├── DocenteDashboardStrategy.java # Asignaturas, calificaciones, anotaciones del docente
        └── EstudianteDashboardStrategy.java # Notas, promedio, asistencias, anotaciones
```

## Endpoints del BFF

### Autenticación (`/bff/auth`)
| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/authenticate` | Login, setea jwt-cookie |
| POST | `/register` | Registro |
| POST | `/refresh-token` | Refresca JWT |
| POST | `/logout` | Invalida cookie |

### Dashboard
| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/bff/dashboard` | Dashboard según rol (ADMIN/DOCENTE/ESTUDIANTE) |

### Proxy (CRUD hacia gateway)
Todas requieren autenticación. El BFF redirige a `/api/v1/{recurso}` en el gateway.

| Recurso | Rutas disponibles |
|---------|------------------|
| usuarios | GET /bff/usuarios, GET /bff/usuarios/{id}, POST, PUT, DELETE |
| cursos | GET /bff/cursos, POST, PUT, DELETE |
| asignaturas | GET /bff/asignaturas, GET /bff/asignaturas/docente/{idDocente}, POST, PUT, DELETE |
| matriculas | GET /bff/matriculas, GET /bff/matriculas/curso/{cursoId}, POST, PUT, DELETE |
| calificaciones | GET /bff/calificaciones, GET /bff/calificaciones/estudiante/{estudianteId}, GET /bff/calificaciones/mis-calificaciones, POST, PUT, DELETE |
| asistencias | GET /bff/asistencias, GET /bff/asistencias/estado/{estado}, GET /bff/asistencias/fecha?inicio=&fin=, GET /bff/asistencias/docente/{idDocente}, POST, PUT, DELETE |
| anotaciones | GET /bff/anotaciones, GET /bff/anotaciones/tipo/{tipo}, GET /bff/anotaciones/estudiante/{idEstudiante}, GET /bff/anotaciones/docente/{idDocente}, POST, PUT, DELETE |

**Nota:** `GET /bff/calificaciones` NO acepta query params (ignora `estudianteId` y `asignaturaId`). Usar `GET /bff/calificaciones/estudiante/{estudianteId}` en su lugar.

## Seguridad
- **JWT en cookie HttpOnly** (`jwt-cookie`, 15 min, SameSite=Lax) o header `Authorization: Bearer <token>`
- `JwtValidationFilter` extrae el token, valida firma, crea `BffUserPrincipal` + authorities
- Endpoints públicos: `/bff/auth/register`, `/bff/auth/authenticate`, `/bff/auth/refresh-token`, `/bff/auth/logout`
- Todo lo demás requiere autenticación
- Sesión STATELESS, CSRF deshabilitado

## Estrategia Dashboard (Strategy Pattern)
- `DashboardService.obtenerDashboard()` selecciona strategy según `BffUserPrincipal.getRole()`
- `ADMIN` → `AdminDashboardStrategy`: totales de usuarios, docentes, estudiantes, cursos, asistencias
- `DOCENTE` → `DocenteDashboardStrategy`: asignaturas, calificaciones, anotaciones del docente
- `ESTUDIANTE` → `EstudianteDashboardStrategy`: notas, promedio general, asistencias presentes, anotaciones

## Propiedades requeridas (application.yml / application-docker.yml)
```yaml
server:
  port: 9093

api:
  gateway:
    base-url: ${API_GATEWAY_URL:http://localhost:8080}

application:
  security:
    jwt:
      secret-key: ${JWT_SECRET_KEY:base64encodedSecretKeyHere}
      cookie-name: jwt-cookie
```

## Docker
```dockerfile
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 9093
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]
```

## Ejecución local
```bash
mvn spring-boot:run
# o con perfil docker:
mvn spring-boot:run -Dspring-boot.run.profiles=docker
```

## Bugs conocidos (historial)
1. **CalificacionesModal.jsx** (frontend): `GET /bff/calificaciones` no filtra por estudiante/asignatura. Solución: usar `GET /bff/calificaciones/estudiante/{estudianteId}` y filtrar por `asignaturaId` client-side.
