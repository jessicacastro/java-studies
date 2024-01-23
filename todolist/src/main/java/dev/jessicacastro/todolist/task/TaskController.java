package dev.jessicacastro.todolist.task;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/tasks")
public class TaskController {
  @Autowired
  private ITaskRepository taskRepository;

  @PostMapping()
  public ResponseEntity<?> create(@RequestBody Task task) {
    if (task.getTitle().isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Title is required");
    }

    if (task.getDescription().isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Description is required");
    }

    if (task.getStatus().equals(TaskStatus.DONE) && task.getEndAt() == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("EndAt is required");
    }

    if (task.getStartAt() != null && task.getEndAt() != null
        && task.getStartAt().isAfter(task.getEndAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("StartAt must be before EndAt");
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

    var createdTask = this.taskRepository.save(task);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
  }

}
