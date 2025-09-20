package com.taskmanager.bdd.steps;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.model.Task;
import com.taskmanager.model.User;
import com.taskmanager.service.TaskService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BDD Step Definitions Demo
 * Shows how BDD describes system behavior in business language
 */
@SpringBootTest
public class BDDDemoSteps {

    @Autowired
    private TaskService taskService;

    private User currentUser;
    private TaskRequest taskRequest;
    private Task createdTask;
    private Exception lastException;

    // Background Steps
    @Given("the task management system is running")
    public void the_task_management_system_is_running() {
        // Verify system is operational
        assertNotNull(taskService, "Task service should be available");
        System.out.println("✅ BDD: Task management system is running");
    }

    @And("I am a registered user")
    public void i_am_a_registered_user() {
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("bdd_demo_user");
        currentUser.setEmail("bdd@demo.com");
        System.out.println("✅ BDD: User is registered and logged in");
    }

    // Scenario Steps
    @Given("I want to create a new task")
    public void i_want_to_create_a_new_task() {
        taskRequest = new TaskRequest();
        createdTask = null;
        lastException = null;
        System.out.println("🎯 BDD: Starting new task creation process");
    }

    @When("I provide task title {string}")
    public void i_provide_task_title(String title) {
        taskRequest.setTitle(title);
        System.out.println("📝 BDD: Task title set to: " + title);
    }

    @And("I provide task description {string}")
    public void i_provide_task_description(String description) {
        taskRequest.setDescription(description);
        System.out.println("📄 BDD: Task description set to: " + description);
    }

    @Then("the task should be created successfully")
    public void the_task_should_be_created_successfully() {
        try {
            createdTask = taskService.addTask(taskRequest, currentUser);
            assertNotNull(createdTask, "Task should be created");
            System.out.println("✅ BDD: Task created successfully with ID: " + createdTask.getId());
        } catch (Exception e) {
            fail("Task creation should succeed, but got exception: " + e.getMessage());
        }
    }

    @And("the task title should be {string}")
    public void the_task_title_should_be(String expectedTitle) {
        assertNotNull(createdTask, "Task should exist");
        assertEquals(expectedTitle, createdTask.getTitle(), "Task title should match expected");
        System.out.println("✅ BDD: Task title verified: " + createdTask.getTitle());
    }

    @And("the task description should be {string}")
    public void the_task_description_should_be(String expectedDescription) {
        assertNotNull(createdTask, "Task should exist");
        assertEquals(expectedDescription, createdTask.getDescription(), "Task description should match expected");
        System.out.println("✅ BDD: Task description verified: " + createdTask.getDescription());
    }

    // Validation Scenario Steps
    @When("I provide an empty task title")
    public void i_provide_an_empty_task_title() {
        taskRequest.setTitle("");
        System.out.println("❌ BDD: Attempting to create task with empty title");
    }

    @Then("I should see a validation error")
    public void i_should_see_a_validation_error() {
        try {
            createdTask = taskService.addTask(taskRequest, currentUser);
            fail("Should have thrown validation exception for empty title");
        } catch (IllegalArgumentException e) {
            lastException = e;
            System.out.println("✅ BDD: Validation error caught: " + e.getMessage());
        }
    }

    @And("the error message should be user-friendly")
    public void the_error_message_should_be_user_friendly() {
        assertNotNull(lastException, "Exception should have been thrown");
        assertTrue(lastException.getMessage().contains("Title cannot be null or empty"),
                "Error message should be user-friendly");
        System.out.println("✅ BDD: Error message is user-friendly");
    }

    @And("no task should be created")
    public void no_task_should_be_created() {
        assertNull(createdTask, "No task should be created when validation fails");
        System.out.println("✅ BDD: Confirmed no task was created");
    }

    // Security Scenario Steps
    @When("I provide a task title with malicious content {string}")
    public void i_provide_a_task_title_with_malicious_content(String maliciousTitle) {
        taskRequest.setTitle(maliciousTitle);
        System.out.println("🔒 BDD: Testing security with malicious input");
    }

    @Then("the system should sanitize the content")
    public void the_system_should_sanitize_the_content() {
        try {
            createdTask = taskService.addTask(taskRequest, currentUser);
            assertNotNull(createdTask, "Task should still be created after sanitization");
            System.out.println("✅ BDD: Content was sanitized and task created");
        } catch (Exception e) {
            fail("Task creation should succeed with sanitized content: " + e.getMessage());
        }
    }

    @And("no script tags should be present")
    public void no_script_tags_should_be_present() {
        assertNotNull(createdTask, "Task should exist");
        assertFalse(createdTask.getTitle().contains("<script>"), 
                   "Task title should not contain script tags");
        System.out.println("✅ BDD: Security verified - no script tags in title: " + createdTask.getTitle());
    }
}