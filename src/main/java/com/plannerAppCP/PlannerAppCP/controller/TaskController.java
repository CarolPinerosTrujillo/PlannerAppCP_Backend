package com.plannerAppCP.PlannerAppCP.controller;

import com.plannerAppCP.PlannerAppCP.config.AuthFilter;
import com.plannerAppCP.PlannerAppCP.model.StatusTarea;
import com.plannerAppCP.PlannerAppCP.model.Task;
import com.plannerAppCP.PlannerAppCP.repository.TaskRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
@Tag(name = "Tasks", description = "Gestión de tareas personales")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    private String getUserEmail(HttpServletRequest request) {
        return (String) request.getAttribute(AuthFilter.ATTR_USER_EMAIL);
    }

    @Operation(summary = "Listar tareas del usuario autenticado")
    @GetMapping
    public List<Task> listarTodas(HttpServletRequest request) {
        String email = getUserEmail(request);
        return taskRepository.findByUserEmail(email);
    }

    @Operation(summary = "Buscar tarea por ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id, HttpServletRequest request) {
        String email = getUserEmail(request);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada con id: " + id));

        if (!email.equals(task.getUserEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("{\"error\":\"No tienes permiso para ver esta tarea\"}");
        }
        return ResponseEntity.ok(task);
    }

    @Operation(summary = "Crear una nueva tarea")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task crear(@RequestBody @Valid Task task, HttpServletRequest request) {
        String email = getUserEmail(request);
        task.setStatus(StatusTarea.PORHACER);
        task.setUserEmail(email);
        return taskRepository.save(task);
    }

    @Operation(summary = "Actualizar una tarea")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Task task,
                                        HttpServletRequest request) {
        String email = getUserEmail(request);
        Task existente = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada con id: " + id));

        if (!email.equals(existente.getUserEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("{\"error\":\"No tienes permiso para modificar esta tarea\"}");
        }

        if (task.getNombre() != null) existente.setNombre(task.getNombre());
        if (task.getDescripcion() != null) existente.setDescripcion(task.getDescripcion());
        if (task.getFecha() != null) existente.setFecha(task.getFecha());
        if (task.getHora() != null) existente.setHora(task.getHora());
        if (task.getPrioridad() != null) existente.setPrioridad(task.getPrioridad());
        if (task.getCategoria() != null) existente.setCategoria(task.getCategoria());
        if (task.getStatus() != null) existente.setStatus(task.getStatus());

        return ResponseEntity.ok(taskRepository.save(existente));
    }

    @Operation(summary = "Eliminar una tarea")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, HttpServletRequest request) {
        String email = getUserEmail(request);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada con id: " + id));

        if (!email.equals(task.getUserEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("{\"error\":\"No tienes permiso para eliminar esta tarea\"}");
        }

        taskRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
