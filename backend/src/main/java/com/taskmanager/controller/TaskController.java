package com.taskmanager.controller;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.model.Task;
import com.taskmanager.model.User;
import com.taskmanager.repository.UserRepository;
import com.taskmanager.service.TaskService;
import com.taskmanager.service.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private UserRepository userRepository;
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userRepository.findByUsername(userPrincipal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        try {
            User user = getCurrentUser();
            List<Task> tasks = taskService.getAllTasksByUser(user);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            System.err.println("Error getting tasks: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        try {
            User user = getCurrentUser();
            Optional<Task> task = taskService.getTaskById(id);
            
            if (task.isPresent() && task.get().getUser().getId().equals(user.getId())) {
                return ResponseEntity.ok(task.get());
            }
            
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Error getting task: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskRequest taskRequest) {
        try {
            User user = getCurrentUser();
            Task createdTask = taskService.createTask(taskRequest, user);
            return ResponseEntity.ok(createdTask);
        } catch (Exception e) {
            System.err.println("Error creating task: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest taskRequest) {
        try {
            User user = getCurrentUser();
            Task updatedTask = taskService.updateTask(id, taskRequest, user);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            System.err.println("Error updating task: " + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Error updating task: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            User user = getCurrentUser();
            taskService.deleteTask(id, user);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            System.err.println("Error deleting task: " + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Error deleting task: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable Task.TaskStatus status) {
        try {
            User user = getCurrentUser();
            List<Task> tasks = taskService.getTasksByStatus(user, status);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            System.err.println("Error getting tasks by status: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
