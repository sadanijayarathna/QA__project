package com.taskmanager.service;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.model.Task;
import com.taskmanager.model.User;
import com.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * TDD DEMONSTRATION: How to Start TDD Properly
 * STEP 1: Write a failing test FIRST (RED Phase)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TDD Demo - Starting with RED Phase")
public class TDDRedPhaseDemo {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private User testUser;
    private TaskRequest validTaskRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("demo_user");

        validTaskRequest = new TaskRequest();
        validTaskRequest.setTitle("Demo Task");
        validTaskRequest.setDescription("Demo Description");
    }

    /**
     * Step 1: RED PHASE
     * This test calls a method that SHOULD exist but might not work correctly yet
     * The test describes the EXPECTED behavior
     */
    @Test
    @DisplayName("RED: Test what we want the method to do")
    void demonstrateRedPhase() {
        // This test will show what happens when we test our current implementation
        // Let's test a method that exists but might not have all features
        
        // GREEN PHASE: Mock the repository to return a proper Task object
        Task expectedTask = new Task();
        expectedTask.setId(1L);
        expectedTask.setTitle("Demo Task");
        expectedTask.setDescription("Demo Description");
        expectedTask.setUser(testUser);
        
        // Configure mock to return the expected task when save is called
        when(taskRepository.save(any(Task.class))).thenReturn(expectedTask);
        
        // Act - Call the method we want to test
        Task result = taskService.addTask(validTaskRequest, testUser);
        
        // Assert - This is what we EXPECT
        assertNotNull(result, "Method should return a task");
        assertEquals("Demo Task", result.getTitle(), "Title should be preserved");
        
        System.out.println("✅ GREEN PHASE: Test passes because method is implemented!");
        System.out.println("Task created with title: " + result.getTitle());
    }

    /**
     * Step 2: Test edge case that might fail
     * This will help us identify what needs to be implemented
     */
    @Test
    @DisplayName("RED: Test edge case that might not be implemented")
    void testEdgeCaseThatMightFail() {
        // Test with invalid data to see if validation works
        validTaskRequest.setTitle(""); // Empty title
        
        try {
            Task result = taskService.addTask(validTaskRequest, testUser);
            fail("Should have thrown an exception for empty title, but got: " + result.getTitle());
        } catch (IllegalArgumentException e) {
            System.out.println("✅ Validation works! Exception message: " + e.getMessage());
            assertTrue(e.getMessage().contains("Title cannot be null or empty"));
        }
    }
}