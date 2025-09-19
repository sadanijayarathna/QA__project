package com.taskmanager.integration;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.model.Task;
import com.taskmanager.model.User;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import com.taskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Tests for Task Management
 * Tests the integration between Service, Repository, and Database layers
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TaskIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Clean up database
        taskRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = new User();
        testUser.setUsername("integrationtestuser");
        testUser.setEmail("integration@test.com");
        testUser.setPassword("password123");
        testUser = userRepository.save(testUser);
    }

    @Test
    void testCreateTask_IntegrationFlow() {
        // Given: Valid task request
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTitle("Integration Test Task");
        taskRequest.setDescription("Testing full integration flow");
        taskRequest.setStatus(Task.TaskStatus.PENDING);
        taskRequest.setPriority(Task.TaskPriority.HIGH);

        // When: Creating task through service layer
        Task createdTask = taskService.createTask(taskRequest, testUser);

        // Then: Task should be persisted in database
        assertNotNull(createdTask);
        assertNotNull(createdTask.getId());
        assertEquals("Integration Test Task", createdTask.getTitle());
        assertEquals("Testing full integration flow", createdTask.getDescription());
        assertEquals(Task.TaskStatus.PENDING, createdTask.getStatus());
        assertEquals(Task.TaskPriority.HIGH, createdTask.getPriority());
        assertEquals(testUser.getId(), createdTask.getUser().getId());

        // Verify task exists in database
        Task foundTask = taskRepository.findById(createdTask.getId()).orElse(null);
        assertNotNull(foundTask);
        assertEquals(createdTask.getTitle(), foundTask.getTitle());
    }

    @Test
    void testGetAllTasksByUser_IntegrationFlow() {
        // Given: Multiple tasks for the user
        createTestTask("Task 1", "Description 1", Task.TaskStatus.PENDING);
        createTestTask("Task 2", "Description 2", Task.TaskStatus.IN_PROGRESS);
        createTestTask("Task 3", "Description 3", Task.TaskStatus.COMPLETED);

        // When: Retrieving all tasks for user
        List<Task> userTasks = taskService.getAllTasksByUser(testUser);

        // Then: All tasks should be returned
        assertNotNull(userTasks);
        assertEquals(3, userTasks.size());

        // Verify tasks are ordered by creation date (most recent first)
        assertTrue(userTasks.get(0).getCreatedAt().isAfter(userTasks.get(1).getCreatedAt()) ||
                   userTasks.get(0).getCreatedAt().equals(userTasks.get(1).getCreatedAt()));
    }

    @Test
    void testUpdateTask_IntegrationFlow() {
        // Given: Existing task
        Task originalTask = createTestTask("Original Title", "Original Description", Task.TaskStatus.PENDING);

        // When: Updating task
        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setDescription("Updated Description");
        updateRequest.setStatus(Task.TaskStatus.IN_PROGRESS);
        updateRequest.setPriority(Task.TaskPriority.LOW);

        Task updatedTask = taskService.updateTask(originalTask.getId(), updateRequest, testUser);

        // Then: Task should be updated in database
        assertNotNull(updatedTask);
        assertEquals("Updated Title", updatedTask.getTitle());
        assertEquals("Updated Description", updatedTask.getDescription());
        assertEquals(Task.TaskStatus.IN_PROGRESS, updatedTask.getStatus());
        assertEquals(Task.TaskPriority.LOW, updatedTask.getPriority());

        // Verify changes are persisted
        Task persistedTask = taskRepository.findById(originalTask.getId()).orElse(null);
        assertNotNull(persistedTask);
        assertEquals("Updated Title", persistedTask.getTitle());
        assertEquals(Task.TaskStatus.IN_PROGRESS, persistedTask.getStatus());
    }

    @Test
    void testDeleteTask_IntegrationFlow() {
        // Given: Existing task
        Task taskToDelete = createTestTask("Task to Delete", "This will be deleted", Task.TaskStatus.PENDING);
        Long taskId = taskToDelete.getId();

        // Verify task exists
        assertTrue(taskRepository.existsById(taskId));

        // When: Deleting task
        taskService.deleteTask(taskId, testUser);

        // Then: Task should be removed from database
        assertFalse(taskRepository.existsById(taskId));
    }

    @Test
    void testGetTasksByStatus_IntegrationFlow() {
        // Given: Tasks with different statuses
        createTestTask("Pending Task 1", "Description", Task.TaskStatus.PENDING);
        createTestTask("Pending Task 2", "Description", Task.TaskStatus.PENDING);
        createTestTask("In Progress Task", "Description", Task.TaskStatus.IN_PROGRESS);
        createTestTask("Completed Task", "Description", Task.TaskStatus.COMPLETED);

        // When: Getting tasks by status
        List<Task> pendingTasks = taskService.getTasksByStatus(testUser, Task.TaskStatus.PENDING);
        List<Task> inProgressTasks = taskService.getTasksByStatus(testUser, Task.TaskStatus.IN_PROGRESS);
        List<Task> completedTasks = taskService.getTasksByStatus(testUser, Task.TaskStatus.COMPLETED);

        // Then: Correct tasks should be returned
        assertEquals(2, pendingTasks.size());
        assertEquals(1, inProgressTasks.size());
        assertEquals(1, completedTasks.size());

        // Verify status filtering
        pendingTasks.forEach(task -> assertEquals(Task.TaskStatus.PENDING, task.getStatus()));
        inProgressTasks.forEach(task -> assertEquals(Task.TaskStatus.IN_PROGRESS, task.getStatus()));
        completedTasks.forEach(task -> assertEquals(Task.TaskStatus.COMPLETED, task.getStatus()));
    }

    // Helper method to create test tasks
    private Task createTestTask(String title, String description, Task.TaskStatus status) {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTitle(title);
        taskRequest.setDescription(description);
        taskRequest.setStatus(status);
        taskRequest.setPriority(Task.TaskPriority.MEDIUM);

        return taskService.createTask(taskRequest, testUser);
    }
}
