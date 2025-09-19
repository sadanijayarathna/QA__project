package com.taskmanager.dto;

import com.taskmanager.model.Task;
import com.taskmanager.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class TaskRequest {

    @NotBlank
    @Size(max = 100)
    private String title;

    @Size(max = 500)
    private String description;

    private Task.TaskStatus status;

    private Task.TaskPriority priority;

    private LocalDateTime dueDate;

    private User user;  // Add the user field

    // Default constructor
    public TaskRequest() {}

    // Constructor with title and description
    public TaskRequest(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // Getters and Setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Task.TaskStatus getStatus() {
        return status;
    }

    public void setStatus(Task.TaskStatus status) {
        this.status = status;
    }

    public Task.TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(Task.TaskPriority priority) {
        this.priority = priority;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public User getUser() {
        return user;  // Getter for user field
    }

    public void setUser(User user) {
        this.user = user;  // Setter for user field
    }
}
