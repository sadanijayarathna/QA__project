package com.taskmanager.bdd.demo;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.model.Task;
import com.taskmanager.model.User;
import com.taskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BDD DEMONSTRATION: How BDD Works vs TDD
 * BDD focuses on BEHAVIOR from user perspective
 * Written in business language that stakeholders can understand
 */
@SpringBootTest
@DisplayName("BDD Demo: User Behavior Testing")
public class BDDMethodologyDemo {

    @Autowired
    private TaskService taskService;

    private User businessUser;
    private TaskRequest userRequest;

    @BeforeEach
    void setUp() {
        businessUser = new User();
        businessUser.setId(1L);
        businessUser.setUsername("business_user");
    }

    /**
     * BDD SCENARIO: As a business user, I want to create tasks
     * This test reads like a business requirement
     */
    @Test
    @DisplayName("BDD: As a user, I want to create a task so that I can track my work")
    void userWantsToCreateTask_SoTheyCanTrackWork() {
        System.out.println("\n🥒 BDD SCENARIO: User wants to create a task");
        
        // GIVEN: I am a user who wants to track my work
        System.out.println("GIVEN: I am a user who wants to track my work");
        userRequest = new TaskRequest();
        userRequest.setTitle("Complete quarterly report");
        userRequest.setDescription("Prepare and submit Q3 financial report");
        
        // WHEN: I create a new task with valid information
        System.out.println("WHEN: I create a new task with valid information");
        Task result = taskService.addTask(userRequest, businessUser);
        
        // THEN: The system should create my task successfully
        System.out.println("THEN: The system should create my task successfully");
        assertNotNull(result, "System should create the task");
        assertEquals("Complete quarterly report", result.getTitle(), "Task should have my title");
        assertEquals(businessUser, result.getUser(), "Task should belong to me");
        
        System.out.println("✅ BDD: User can successfully track their work!");
    }

    /**
     * BDD SCENARIO: As a user, I expect helpful error messages
     * BDD focuses on user experience and business value
     */
    @Test
    @DisplayName("BDD: As a user, I want clear error messages when I make mistakes")
    void userMakesMistake_SystemProvidesHelpfulGuidance() {
        System.out.println("\n🥒 BDD SCENARIO: User makes a mistake and needs guidance");
        
        // GIVEN: I am a user who accidentally provides invalid data
        System.out.println("GIVEN: I am a user who accidentally provides invalid data");
        userRequest = new TaskRequest();
        userRequest.setTitle(""); // Oops! Empty title
        userRequest.setDescription("I forgot to add a title");
        
        // WHEN: I try to create the task
        System.out.println("WHEN: I try to create the task with missing information");
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> taskService.addTask(userRequest, businessUser),
            "System should catch my mistake"
        );
        
        // THEN: The system should guide me with a helpful message
        System.out.println("THEN: The system should guide me with a helpful message");
        assertTrue(exception.getMessage().contains("Title cannot be null or empty"),
                  "Error message should be clear and helpful");
        
        System.out.println("✅ BDD: User receives helpful guidance: " + exception.getMessage());
    }

    /**
     * BDD SCENARIO: As a security-conscious user, I expect protection
     * BDD describes business value of security features
     */
    @Test
    @DisplayName("BDD: As a user, I want my data protected from security threats")
    void userProvidesUnsafeContent_SystemProtectsAutomatically() {
        System.out.println("\n🥒 BDD SCENARIO: User unknowingly provides unsafe content");
        
        // GIVEN: I am a user who unknowingly includes unsafe content
        System.out.println("GIVEN: I am a user who pastes content from an untrusted source");
        userRequest = new TaskRequest();
        userRequest.setTitle("<script>alert('dangerous')</script>My Important Task");
        userRequest.setDescription("This task is important for business");
        
        // WHEN: I create the task with this content
        System.out.println("WHEN: I create the task, unaware of the security risk");
        Task result = taskService.addTask(userRequest, businessUser);
        
        // THEN: The system should protect me automatically
        System.out.println("THEN: The system should protect me and other users automatically");
        assertNotNull(result, "My task should still be created");
        assertFalse(result.getTitle().contains("<script>"), "Dangerous content should be removed");
        assertTrue(result.getTitle().contains("My Important Task"), "My actual content should be preserved");
        
        System.out.println("✅ BDD: User is protected! Safe title: " + result.getTitle());
    }

    /**
     * BDD vs TDD COMPARISON
     * This method shows the difference in approach
     */
    @Test
    @DisplayName("BDD vs TDD: Same functionality, different perspectives")
    void demonstrateBDDvsTDDDifference() {
        System.out.println("\n🎯 COMPARISON: BDD vs TDD Approaches");
        
        System.out.println("\n🔴 TDD Approach (Developer Perspective):");
        System.out.println("- Test: addTask_WithValidData_ShouldReturnTask()");
        System.out.println("- Focus: Method behavior, return values, internal logic");
        System.out.println("- Language: Technical, code-focused");
        
        System.out.println("\n🥒 BDD Approach (Business Perspective):");
        System.out.println("- Scenario: As a user, I want to create tasks to track my work");
        System.out.println("- Focus: User behavior, business value, real-world usage");
        System.out.println("- Language: Business-friendly, user-focused");
        
        // Same underlying functionality, different perspectives
        userRequest = new TaskRequest();
        userRequest.setTitle("Demo Task");
        userRequest.setDescription("Shows BDD methodology");
        
        Task result = taskService.addTask(userRequest, businessUser);
        
        // TDD assertion style: Technical verification
        assertNotNull(result);
        assertEquals("Demo Task", result.getTitle());
        
        // BDD assertion style: Business value verification  
        assertTrue(result != null, "User should be able to create tasks for work tracking");
        assertTrue("Demo Task".equals(result.getTitle()), "User's task title should be preserved exactly");
        
        System.out.println("✅ Both approaches test the same code, but BDD focuses on USER VALUE!");
    }
}