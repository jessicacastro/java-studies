package dev.jessicacastro.todolist.task;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.jessicacastro.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/tasks")
public class TaskController {
  @Autowired
  private ITaskRepository taskRepository;

  @PostMapping()
  public ResponseEntity<?> create(@RequestBody Task task, HttpServletRequest request) {
    if (task.getDescription().isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Description is required");
    }

    if (task.getStatus().equals(TaskStatus.DONE) && task.getEndAt() == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("End date is required");
    }

    if (task.getStartAt() != null && task.getEndAt() != null
        && task.getStartAt().isAfter(task.getEndAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date must be before end date");
    }

    if (LocalDateTime.now().isAfter(task.getStartAt())
        || LocalDateTime.now().isAfter(task.getEndAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date and end date must be in the future");
    }

    if (task.getStatus() != null
        && !task.getStatus().equals(TaskStatus.PENDING)
        && !task.getStatus().equals(TaskStatus.DONE)
        && !task.getStatus().equals(TaskStatus.IN_PROGRESS)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Status is invalid");
    }

    if (task.getPriority() != null
        && !task.getPriority().equals(TaskPriority.LOW)
        && !task.getPriority().equals(TaskPriority.MEDIUM)
        && !task.getPriority().equals(TaskPriority.HIGH)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Priority is invalid");
    }

    UUID userId = (UUID) request.getAttribute("userId");
    task.setUserId(userId);
    Task createdTask = this.taskRepository.save(task);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
  }

  @GetMapping()
  public List<Task> index(HttpServletRequest request) {
    UUID userId = (UUID) request.getAttribute("userId");

    List<Task> tasks = this.taskRepository.findByUserId(userId);

    return tasks;
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> update(@RequestBody Task task, HttpServletRequest request, @PathVariable UUID id) {
    UUID userId = (UUID) request.getAttribute("userId");

    if (id == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID is required");
    }

    Task taskToUpdate = this.taskRepository.findById(id).orElse(null);

    if (taskToUpdate == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
    }

    if (!taskToUpdate.getUserId().equals(userId)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You don't have permission to update this task");
    }

    if (task.getStartAt() != null && task.getEndAt() != null
        && task.getStartAt().isAfter(task.getEndAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date must be before end date");
    }

    if (LocalDateTime.now().isAfter(task.getStartAt())
        || LocalDateTime.now().isAfter(task.getEndAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date and end date must be in the future");
    }

    if (task.getStatus() != null
        && !task.getStatus().equals(TaskStatus.PENDING)
        && !task.getStatus().equals(TaskStatus.DONE)
        && !task.getStatus().equals(TaskStatus.IN_PROGRESS)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Status is invalid");
    }

    if (task.getPriority() != null
        && !task.getPriority().equals(TaskPriority.LOW)
        && !task.getPriority().equals(TaskPriority.MEDIUM)
        && !task.getPriority().equals(TaskPriority.HIGH)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Priority is invalid");
    }

    Utils.copyNonNullProperties(task, taskToUpdate);

    Task updatedTask = this.taskRepository.save(taskToUpdate);

    return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
  }

  @PatchMapping("/{id}")
  public Task updateStatus(@RequestBody Task task, HttpServletRequest request, @PathVariable UUID id) {
    UUID userId = (UUID) request.getAttribute("userId");

    if (id == null) {
      throw new Error("ID is required");
    }

    Task taskToUpdate = this.taskRepository.findById(id).get();

    if (!taskToUpdate.getUserId().equals(userId)) {
      throw new Error("You don't have permission to update this task");
    }

    taskToUpdate.setStatus(task.getStatus());

    Task updatedTask = this.taskRepository.save(taskToUpdate);

    return updatedTask;
  }
}