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
        validateTaskInput(taskRequest);
        return createTask(taskRequest, user);
    }
    
    // TDD: Feature 2 - Task Status Management with Business Rules
    public Task updateTaskStatus(Long id, Task.TaskStatus newStatus) {
        Task task = findTaskById(id);
        validateStatusTransition(task.getStatus(), newStatus);
        
        task.setStatus(newStatus);
        return taskRepository.save(task);
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
    
    // TDD Implementation: GREEN PHASE - Minimal code to make tests pass
    
    // TDD Implementation: RED → GREEN → REFACTOR CYCLE COMPLETE
    
    /**
     * TDD Method 1: Add Task with Enhanced Validation
     * REFACTOR PHASE: Improved implementation with better error handling
     */
    public Task addTask(TaskRequest taskRequest, User user) {
        // Delegate validation to specialized method for consistency
        validateTaskInput(taskRequest);
        
        // Create task with validated data
        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setUser(user);
        task.setStatus(taskRequest.getStatus() != null ? taskRequest.getStatus() : Task.TaskStatus.PENDING);
        task.setPriority(taskRequest.getPriority() != null ? taskRequest.getPriority() : Task.TaskPriority.MEDIUM);
        
        if (taskRequest.getDueDate() != null) {
            task.setDueDate(taskRequest.getDueDate());
        }
        
        return taskRepository.save(task);
    }
    
    /**
     * TDD Method 2: Comprehensive Task Input Validation
     * REFACTOR PHASE: Well-structured validation with clear error messages
     */
    public TaskRequest validateTaskInput(TaskRequest taskRequest) {
        // Title validation
        validateTitle(taskRequest.getTitle());
        
        // Description validation
        validateDescription(taskRequest.getDescription());
        
        // Due date validation
        validateDueDate(taskRequest.getDueDate());
        
        // Set defaults for null values
        setDefaultsIfNull(taskRequest);
        
        // Sanitize content for security
        sanitizeTaskContent(taskRequest);
        
        return taskRequest;
    }
    
    // REFACTOR: Extract validation methods for single responsibility
    
    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        
        if (title.length() > 200) {
            throw new IllegalArgumentException("Title cannot exceed 200 characters");
        }
    }
    
    private void validateDescription(String description) {
        if (description != null && description.length() > 500) {
            throw new IllegalArgumentException("Task description cannot exceed 500 characters");
        }
    }
    
    private void validateDueDate(java.time.LocalDateTime dueDate) {
        if (dueDate != null && dueDate.isBefore(java.time.LocalDateTime.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past");
        }
    }
    
    private void setDefaultsIfNull(TaskRequest taskRequest) {
        if (taskRequest.getPriority() == null) {
            taskRequest.setPriority(Task.TaskPriority.MEDIUM);
        }
        
        if (taskRequest.getStatus() == null) {
            taskRequest.setStatus(Task.TaskStatus.PENDING);
        }
    }
    
    private void sanitizeTaskContent(TaskRequest taskRequest) {
        if (taskRequest.getTitle() != null) {
            taskRequest.setTitle(sanitizeHtml(taskRequest.getTitle()));
        }
        
        if (taskRequest.getDescription() != null) {
            taskRequest.setDescription(sanitizeHtml(taskRequest.getDescription()));
        }
    }
    
    /**
     * Enhanced HTML sanitization with better security
     * REFACTOR: More comprehensive sanitization logic
     */
    private String sanitizeHtml(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        
        // Remove dangerous script tags and events
        String sanitized = input
            // Remove script tags (case insensitive, multiline)
            .replaceAll("(?i)<script[^>]*>.*?</script>", "")
            .replaceAll("(?i)<script[^>]*/>", "")
            // Remove javascript: protocols
            .replaceAll("(?i)javascript:", "")
            // Remove on* event handlers
            .replaceAll("(?i)\\s*on\\w+\\s*=\\s*[\"'][^\"']*[\"']", "")
            .replaceAll("(?i)\\s*on\\w+\\s*=\\s*[^\\s>]+", "");
        
        return sanitized.trim();
    }
}
