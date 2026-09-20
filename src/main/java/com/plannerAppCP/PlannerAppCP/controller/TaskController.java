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

    @Operation(summary = "Listar tareas", description = "Obtiene tareas. Si se envía deviceId, filtra por ese dispositivo")
    @GetMapping
    public List<Task> listarTodas(
            @RequestParam(required = false) String deviceId) {
        if (deviceId != null && !deviceId.isEmpty()) {
            return taskRepository.findByDeviceId(deviceId);
        }
        return taskRepository.findAll();
    }

    @Operation(summary = "Buscar tarea por ID")
    @GetMapping("/{id}")
    public Task buscarPorId(@PathVariable Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada con id: " + id));
    }

    @Operation(summary = "Crear una nueva tarea")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task crear(@RequestBody @Valid Task task) {
        task.setStatus(StatusTarea.PORHACER);
        return taskRepository.save(task);
    }

    @Operation(summary = "Actualizar una tarea")
    @PutMapping("/{id}")
    public Task actualizar(@PathVariable Long id, @RequestBody Task task) {
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

    @Operation(summary = "Eliminar una tarea")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        taskRepository.deleteById(id);
    }
}
