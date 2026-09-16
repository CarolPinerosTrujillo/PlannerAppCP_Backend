# PlannerAppCP - Backend

API REST para la gestión de tareas personales. Construida con Spring Boot y PostgreSQL.

## Stack

- **Java 25** + **Spring Boot 4.1.1**
- **Spring Data JPA** + **PostgreSQL** (Supabase)
- **Swagger/OpenAPI** para documentación
- **Lombok** para boilerplate
- **Maven** como build tool

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/api/tasks` | Listar todas las tareas |
| `GET` | `/api/tasks/{id}` | Buscar tarea por ID |
| `POST` | `/api/tasks` | Crear tarea |
| `PUT` | `/api/tasks/{id}` | Actualizar tarea |
| `DELETE` | `/api/tasks/{id}` | Eliminar tarea |

## Ejecutar localmente

```bash
# Clonar
git clone https://github.com/CarolPinerosTrujillo/PlannerAppCP_Backend.git
cd PlannerAppCP_Backend

# Ejecutar
./mvnw spring-boot:run
```

La app estará en `http://localhost:8080`

## Swagger UI

Accede a la documentación interactiva en:
```
http://localhost:8080/swagger-ui.html
```

## Deploy en Render

1. Conectar el repo en Render
2. Configurar variables de entorno:
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
3. Deploy automático

## Frontend

Repositorio: [Web_planificadorTareas](https://github.com/CarolPinerosTrujillo/Web_planificadorTareas)
