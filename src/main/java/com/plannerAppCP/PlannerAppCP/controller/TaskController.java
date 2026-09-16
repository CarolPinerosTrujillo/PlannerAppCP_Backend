package com.plannerAppCP.PlannerAppCP.controller;

import com.plannerAppCP.PlannerAppCP.model.StatusTarea;
import com.plannerAppCP.PlannerAppCP.model.Task;
import com.plannerAppCP.PlannerAppCP.repository.TaskRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
@Tag(name = "Tasks", description = "Gestión de tareas personales")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    // GET /api/tasks → Listar todas
    @Operation(summary = "Listar todas las tareas", description = "Obtiene la lista completa de tareas registradas")
    @ApiResponse(responseCode = "200", description = "Lista de tareas obtenida exitosamente")
    @GetMapping
    public List<Task> listarTodas() {
        return taskRepository.findAll();
    }

    // GET /api/tasks/{id} → Buscar por ID
    @Operation(summary = "Buscar tarea por ID", description = "Obtiene una tarea específica a partir de su identificador")
    @ApiResponse(responseCode = "200", description = "Tarea encontrada exitosamente")
    @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
    @GetMapping("/{id}")
    public Task buscarPorId(@Parameter(description = "ID de la tarea a buscar") @PathVariable Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada con id: " + id));
    }

    // POST /api/tasks → Crear
    @Operation(summary = "Crear una nueva tarea", description = "Registra una nueva tarea. El estado se establece por defecto en PORHACER")
    @ApiResponse(responseCode = "201", description = "Tarea creada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task crear(@RequestBody @Valid Task task) {
        task.setStatus(StatusTarea.PORHACER);  // Default
        return taskRepository.save(task);
    }

    // PUT /api/tasks/{id} → Actualizar
    @Operation(summary = "Actualizar una tarea", description = "Actualiza parcialmente una tarea existente. Solo se modifican los campos proporcionados")
    @ApiResponse(responseCode = "200", description = "Tarea actualizada exitosamente")
    @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
    @PutMapping("/{id}")
    public Task actualizar(@Parameter(description = "ID de la tarea a actualizar") @PathVariable Long id, @RequestBody Task task) {
        Task existente = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada con id: " + id));

        if (task.getNombre() != null) existente.setNombre(task.getNombre());
        if (task.getDescripcion() != null) existente.setDescripcion(task.getDescripcion());
        if (task.getFecha() != null) existente.setFecha(task.getFecha());
        if (task.getHora() != null) existente.setHora(task.getHora());
        if (task.getPrioridad() != null) existente.setPrioridad(task.getPrioridad());
        if (task.getCategoria() != null) existente.setCategoria(task.getCategoria());
        if (task.getStatus() != null) existente.setStatus(task.getStatus());

        return taskRepository.save(existente);
    }

    // DELETE /api/tasks/{id} → Eliminar
    @Operation(summary = "Eliminar una tarea", description = "Elimina permanentemente una tarea por su ID")
    @ApiResponse(responseCode = "204", description = "Tarea eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Tarea no encontrada")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@Parameter(description = "ID de la tarea a eliminar") @PathVariable Long id) {
        taskRepository.deleteById(id);
    }
}