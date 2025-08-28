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
}
