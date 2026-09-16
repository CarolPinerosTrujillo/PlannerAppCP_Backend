package com.plannerAppCP.PlannerAppCP.controller;

import com.plannerAppCP.PlannerAppCP.model.StatusTarea;
import com.plannerAppCP.PlannerAppCP.model.Task;
import com.plannerAppCP.PlannerAppCP.repository.TaskRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    // GET /api/tasks → Listar todas
    @GetMapping
    public List<Task> listarTodas() {
        return taskRepository.findAll();
    }

    // GET /api/tasks/{id} → Buscar por ID
    @GetMapping("/{id}")
    public Task buscarPorId(@PathVariable Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada con id: " + id));
    }

    // POST /api/tasks → Crear
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task crear(@RequestBody @Valid Task task) {
        task.setStatus(StatusTarea.PORHACER);  // Default
        return taskRepository.save(task);
    }

    // PUT /api/tasks/{id} → Actualizar
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

    // DELETE /api/tasks/{id} → Eliminar
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        taskRepository.deleteById(id);
    }
}