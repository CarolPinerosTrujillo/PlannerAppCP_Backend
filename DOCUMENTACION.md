# PlannerAppCP - Documentación

## Swagger (OpenAPI)

### ¿Qué es Swagger?

Swagger es una herramienta que genera **documentación interactiva** de tu API REST automáticamente. Te permite ver y probar todos los endpoints desde el navegador sin necesidad de Postman ni curl.

### ¿Cómo funciona?

1. **Escanea tu código** (controllers, modelos, enums)
2. **Genera una interfaz web** donde puedes:
   - Ver todos los endpoints (GET, POST, PUT, DELETE)
   - Ver qué parámetros acepta cada endpoint
   - Ver qué datos devuelve cada respuesta
   - **Hacer pruebas directamente desde el navegador**

### Configuración

#### Dependencia en `pom.xml`

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.6.0</version>
</dependency>
```

#### Clase de configuración

`src/main/java/com/plannerAppCP/PlannerAppCP/config/OpenApiConfig.java`

Define:
- Nombre de la API: "PlannerAppCP API"
- Versión: "1.0"
- Descripción: API REST para gestión de tareas personales
- Servidor: localhost:8080 (desarrollo)

### URLs disponibles

| URL | Contenido |
|-----|-----------|
| `http://localhost:8080/swagger-ui.html` | Interfaz interactiva de Swagger |
| `http://localhost:8080/swagger-ui/index.html` | Interfaz alternativa |
| `http://localhost:8080/v3/api-docs` | Documentación OpenAPI en JSON |

### Endpoints documentados

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/api/tasks` | Listar todas las tareas |
| `GET` | `/api/tasks/{id}` | Buscar tarea por ID |
| `POST` | `/api/tasks` | Crear nueva tarea |
| `PUT` | `/api/tasks/{id}` | Actualizar tarea |
| `DELETE` | `/api/tasks/{id}` | Eliminar tarea |

### Cómo probar la API

1. Ejecuta la aplicación:
   ```bash
   ./mvnw spring-boot:run
   ```

2. Abre en tu navegador:
   ```
   http://localhost:8080/swagger-ui.html
   ```

3. Haz clic en el endpoint que quieras probar

4. Clic en "Try it out"

5. Ingresa los datos de prueba

6. Clic en "Execute"

7. Ve la respuesta

### Ejemplo: Crear una tarea

En Swagger UI, ve a `POST /api/tasks`, haz clic en "Try it out" y envía:

```json
{
    "nombre": "Estudiar Spring Boot",
    "descripcion": "Completar el módulo de REST API",
    "fecha": "2026-09-16",
    "hora": "14:00",
    "prioridad": "ALTA",
    "categoria": "ESTUDIO"
}
```

### Deploy en Render

Swagger se incluye automáticamente al hacer deploy. La URL será:
```
https://tu-app.onrender.com/swagger-ui.html
```

### Desactivar Swagger en producción

En `application.properties`:

```properties
springdoc.api-docs.enabled=false
springdoc.swagger-ui.enabled=false
```

## Estructura del proyecto

```
src/main/java/com/plannerAppCP/PlannerAppCP/
├── PlannerAppCpApplication.java    # Punto de entrada
├── config/
│   └── OpenApiConfig.java          # Configuración Swagger
├── controller/
│   └── TaskController.java         # Endpoints REST
├── model/
│   ├── Task.java                   # Entidad JPA
│   ├── Prioridad.java              # Enum: ALTA, MEDIA, BAJA
│   ├── Categoria.java              # Enum: CASA, TRABAJO, ESTUDIO, etc.
│   └── StatusTarea.java            # Enum: PORHACER, ENPROCESO, COMPLETADA
└── repository/
    └── TaskRepository.java         # Interfaz Spring Data JPA
```
