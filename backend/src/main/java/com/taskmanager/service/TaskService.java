package com.taskmanager.service;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.model.Task;
import com.taskmanager.model.User;
import com.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    public List<Task> getAllTasksByUser(User user) {
        return taskRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }
    
    public Task createTask(TaskRequest taskRequest, User user) {
        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setUser(user);
        
        if (taskRequest.getStatus() != null) {
            task.setStatus(taskRequest.getStatus());
        }
        
        if (taskRequest.getPriority() != null) {
            task.setPriority(taskRequest.getPriority());
        }
        
        if (taskRequest.getDueDate() != null) {
            task.setDueDate(taskRequest.getDueDate());
        }
        
        return taskRepository.save(task);
    }
    
    public Task updateTask(Long id, TaskRequest taskRequest, User user) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            
            // Check if the task belongs to the user
            if (!task.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Access denied: Task does not belong to user");
            }
            
            task.setTitle(taskRequest.getTitle());
            task.setDescription(taskRequest.getDescription());
            
            if (taskRequest.getStatus() != null) {
                task.setStatus(taskRequest.getStatus());
            }
            
            if (taskRequest.getPriority() != null) {
                task.setPriority(taskRequest.getPriority());
            }
            
            if (taskRequest.getDueDate() != null) {
                task.setDueDate(taskRequest.getDueDate());
            }
            
            return taskRepository.save(task);
        }
        
        throw new RuntimeException("Task not found with id: " + id);
    }
    
    public void deleteTask(Long id, User user) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            
            // Check if the task belongs to the user
            if (!task.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Access denied: Task does not belong to user");
            }
            
            taskRepository.delete(task);
        } else {
            throw new RuntimeException("Task not found with id: " + id);
        }
    }
    
    public List<Task> getTasksByStatus(User user, Task.TaskStatus status) {
        return taskRepository.findByUserAndStatus(user, status);
    }
    
    // TDD: Feature 1 - Task Creation with Enhanced Validation
    public Task createTaskWithValidation(TaskRequest taskRequest, User user) {
        validateTaskRequest(taskRequest);
        return createTask(taskRequest, user);
    }
    
    // TDD: Feature 2 - Task Status Management with Business Rules
    public Task updateTaskStatus(Long id, Task.TaskStatus newStatus) {
        Task task = findTaskById(id);
        validateStatusTransition(task.getStatus(), newStatus);
        
        task.setStatus(newStatus);
        return taskRepository.save(task);
    }
    
    // REFACTORED: Extract validation methods for better maintainability
    private void validateTaskRequest(TaskRequest taskRequest) {
        validateTitle(taskRequest.getTitle());
        // Can add more validations here in the future
    }
    
    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be null or empty");
        }
        
        if (title.length() > 100) {
            throw new IllegalArgumentException("Task title cannot exceed 100 characters");
        }
    }
    
    private Task findTaskById(Long id) {
        return taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }
    
    private void validateStatusTransition(Task.TaskStatus current, Task.TaskStatus newStatus) {
        if (!isValidStatusTransition(current, newStatus)) {
            throw new IllegalStateException("Cannot transition from " + current + " to " + newStatus);
        }
    }
    
    // Helper method for status transition validation
    private boolean isValidStatusTransition(Task.TaskStatus current, Task.TaskStatus newStatus) {
        if (current == newStatus) {
            return true; // Same status is allowed
        }
        
        return switch (current) {
            case PENDING -> newStatus == Task.TaskStatus.IN_PROGRESS;
            case IN_PROGRESS -> newStatus == Task.TaskStatus.COMPLETED;
            case COMPLETED -> false; // Cannot transition from COMPLETED
        };
    }
}
