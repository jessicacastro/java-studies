package dev.jessicacastro.todolist.task;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ITaskRepository extends JpaRepository<Task, UUID> {
  List<Task> findByUserId(UUID userId);
}
