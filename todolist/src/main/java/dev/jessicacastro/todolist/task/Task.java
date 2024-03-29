package dev.jessicacastro.todolist.task;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "tb_tasks")
public class Task {
  @Id
  @GeneratedValue(generator = "UUID")
  private UUID id;

  private UUID userId;

  private String description;

  @Column(length = 50)
  private String title;

  @Column(columnDefinition = "varchar(12) default 'PENDING'")
  @Enumerated(value = EnumType.STRING)
  private TaskStatus status = TaskStatus.PENDING;

  @Column(columnDefinition = "varchar(12) default 'LOW'")
  @Enumerated(value = EnumType.STRING)
  private TaskPriority priority = TaskPriority.LOW;

  private LocalDateTime startAt;
  private LocalDateTime endAt;

  @CreationTimestamp
  private LocalDateTime createdAt;

  public void setTitle(String title) throws Exception {
    if (title == null || title.isEmpty()) {
      throw new Exception("Title is required");
    }

    if (title.length() > 50) {
      throw new Exception("Title must have a maximum of 50 characters");
    }

    this.title = title;
  }
}