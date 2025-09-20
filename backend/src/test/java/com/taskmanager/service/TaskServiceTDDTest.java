package com.taskmanager.service;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.model.Task;
import com.taskmanager.model.User;
import com.taskmanager.model.Task.TaskPriority;
import com.taskmanager.model.Task.TaskStatus;
import com.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Task Service TDD Tests")
public class TaskServiceTDDTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private User testUser;
    private TaskRequest validTaskRequest;
    private Task savedTask;

    @BeforeEach
    void setUp() {
        // Set up test data
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        validTaskRequest = new TaskRequest();
        validTaskRequest.setTitle("Test Task");
        validTaskRequest.setDescription("Test Description");
        validTaskRequest.setPriority(TaskPriority.MEDIUM);
        validTaskRequest.setDueDate(LocalDateTime.now().plusDays(1));

        savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setTitle("Test Task");
        savedTask.setDescription("Test Description");
        savedTask.setPriority(TaskPriority.MEDIUM);
        savedTask.setStatus(TaskStatus.PENDING);
        savedTask.setUser(testUser);
    }

    // ============================================
    // TDD FEATURE 1: ADD TASK FUNCTIONALITY
    // ============================================

    @Test
    @DisplayName("RED: Should fail when adding task with valid data - test written first")
    void addTask_WithValidData_ShouldCreateTask() {
        // RED PHASE: This test will initially fail because we haven't implemented the specific validation logic
        
        // Arrange
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // Act
        Task result = taskService.addTask(validTaskRequest, testUser);

        // Assert - These assertions will initially fail (RED phase)
        assertNotNull(result, "Task should not be null");
        assertEquals("Test Task", result.getTitle(), "Task title should match");
        assertEquals("Test Description", result.getDescription(), "Task description should match");
        assertEquals(TaskPriority.MEDIUM, result.getPriority(), "Task priority should match");
        assertEquals(TaskStatus.PENDING, result.getStatus(), "Default status should be PENDING");
        assertEquals(testUser, result.getUser(), "User should be assigned correctly");
        
        // Verify repository interaction
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("RED: Should fail validation for null title - test written first")
    void addTask_WithNullTitle_ShouldThrowException() {
        // RED PHASE: This test will fail initially
        
        // Arrange
        validTaskRequest.setTitle(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> taskService.addTask(validTaskRequest, testUser),
            "Should throw exception for null title"
        );
        
        assertEquals("Title cannot be null or empty", exception.getMessage());
        
        // Verify repository was not called
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("RED: Should fail validation for empty title - test written first")
    void addTask_WithEmptyTitle_ShouldThrowException() {
        // RED PHASE: This test will fail initially
        
        // Arrange
        validTaskRequest.setTitle("");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> taskService.addTask(validTaskRequest, testUser),
            "Should throw exception for empty title"
        );
        
        assertEquals("Title cannot be null or empty", exception.getMessage());
        verify(taskRepository, never()).save(any(Task.class));
    }

    // ============================================
    // TDD FEATURE 2: VALIDATE USER INPUT
    // ============================================

    @Test
    @DisplayName("RED: Should fail validation for title too long - test written first")
    void validateTaskInput_WithLongTitle_ShouldThrowException() {
        // RED PHASE: This test will fail initially
        
        // Arrange
        String longTitle = "A".repeat(201); // Assuming max length is 200
        validTaskRequest.setTitle(longTitle);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> taskService.validateTaskInput(validTaskRequest),
            "Should throw exception for title too long"
        );
        
        assertTrue(exception.getMessage().contains("Title cannot exceed 200 characters"));
    }

    @Test
    @DisplayName("RED: Should fail validation for invalid priority - test written first")
    void validateTaskInput_WithNullPriority_ShouldSetDefaultPriority() {
        // RED PHASE: This test will fail initially
        
        // Arrange
        validTaskRequest.setPriority(null);

        // Act
        TaskRequest result = taskService.validateTaskInput(validTaskRequest);

        // Assert
        assertEquals(TaskPriority.MEDIUM, result.getPriority(), "Should set default priority to MEDIUM");
    }

    @Test
    @DisplayName("RED: Should fail validation for past due date - test written first")
    void validateTaskInput_WithPastDueDate_ShouldThrowException() {
        // RED PHASE: This test will fail initially
        
        // Arrange
        validTaskRequest.setDueDate(LocalDateTime.now().minusDays(1));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> taskService.validateTaskInput(validTaskRequest),
            "Should throw exception for past due date"
        );
        
        assertTrue(exception.getMessage().contains("Due date cannot be in the past"));
    }

    @Test
    @DisplayName("RED: Should pass validation with all valid data - test written first")
    void validateTaskInput_WithValidData_ShouldReturnValidatedRequest() {
        // RED PHASE: This test will fail initially
        
        // Act
        TaskRequest result = taskService.validateTaskInput(validTaskRequest);

        // Assert
        assertNotNull(result, "Validated request should not be null");
        assertEquals(validTaskRequest.getTitle(), result.getTitle());
        assertEquals(validTaskRequest.getDescription(), result.getDescription());
        assertEquals(validTaskRequest.getPriority(), result.getPriority());
        assertEquals(validTaskRequest.getDueDate(), result.getDueDate());
    }

    @Test
    @DisplayName("RED: Should sanitize HTML in title and description - test written first")
    void validateTaskInput_WithHTMLContent_ShouldSanitizeInput() {
        // RED PHASE: This test will fail initially
        
        // Arrange
        validTaskRequest.setTitle("<script>alert('xss')</script>Clean Title");
        validTaskRequest.setDescription("<b>Bold</b> description with <script>bad script</script>");

        // Act
        TaskRequest result = taskService.validateTaskInput(validTaskRequest);

        // Assert
        assertFalse(result.getTitle().contains("<script>"), "Title should not contain script tags");
        assertFalse(result.getDescription().contains("<script>"), "Description should not contain script tags");
        assertEquals("Clean Title", result.getTitle().trim(), "Title should be sanitized");
        assertTrue(result.getDescription().contains("Bold"), "Safe HTML should be preserved");
    }
}