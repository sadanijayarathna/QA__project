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
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BDD Step Definitions for Task Creation and Validation Features
 * Integrates with TDD implementation to provide full coverage
 */
@SpringBootTest
@ActiveProfiles("test")
public class TaskCreationSteps {

    @Autowired
    private TaskService taskService;

    private User currentUser;
    private TaskRequest taskRequest;
    private Task createdTask;
    private Exception thrownException;

    // Background Steps
    @Given("I am a logged-in user")
    public void i_am_a_logged_in_user() {
        // Create a test user for BDD scenarios
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("bdd_test_user");
        currentUser.setEmail("bdd@test.com");
    }

    @And("I have access to the task management system")
    public void i_have_access_to_the_task_management_system() {
        // Verify system is accessible
        assertNotNull(taskService, "Task service should be available");
        assertNotNull(currentUser, "User should be logged in");
    }

    // Task Creation Scenarios
    @Given("I want to create a new task")
    public void i_want_to_create_a_new_task() {
        taskRequest = new TaskRequest();
        createdTask = null;
        thrownException = null;
    }

    @When("I provide a title {string}")
    public void i_provide_a_title(String title) {
        taskRequest.setTitle(title);
    }

    @And("I provide a description {string}")
    public void i_provide_a_description(String description) {
        taskRequest.setDescription(description);
    }

    @And("I set the priority to {string}")
    public void i_set_the_priority_to(String priority) {
        try {
            taskRequest.setPriority(Task.TaskPriority.valueOf(priority));
        } catch (IllegalArgumentException e) {
            // Handle invalid priority values in scenarios
            thrownException = e;
        }
    }

    @And("I set the due date to tomorrow")
    public void i_set_the_due_date_to_tomorrow() {
        taskRequest.setDueDate(LocalDateTime.now().plusDays(1));
    }

    @And("I set the due date to yesterday")
    public void i_set_the_due_date_to_yesterday() {
        taskRequest.setDueDate(LocalDateTime.now().minusDays(1));
    }

    @When("I provide an empty title")
    public void i_provide_an_empty_title() {
        taskRequest.setTitle("");
    }

    @When("I provide a title that exceeds 200 characters")
    public void i_provide_a_title_that_exceeds_200_characters() {
        StringBuilder longTitle = new StringBuilder();
        for (int i = 0; i < 201; i++) {
            longTitle.append("a");
        }
        taskRequest.setTitle(longTitle.toString());
    }

    @When("I provide a title containing script tags {string}")
    public void i_provide_a_title_containing_script_tags(String title) {
        taskRequest.setTitle(title);
    }

    @And("I provide a description with mixed HTML content {string}")
    public void i_provide_a_description_with_mixed_html_content(String description) {
        taskRequest.setDescription(description);
    }

    @When("I provide only a title {string}")
    public void i_provide_only_a_title(String title) {
        taskRequest.setTitle(title);
        // Leave other fields null to test defaults
        taskRequest.setPriority(null);
        taskRequest.setStatus(null);
    }

    @And("I leave priority and status empty")
    public void i_leave_priority_and_status_empty() {
        // Already handled in previous step
    }

    @When("I provide a title with {int} characters")
    public void i_provide_a_title_with_characters(int length) {
        if (length == 0) {
            taskRequest.setTitle("");
        } else {
            StringBuilder title = new StringBuilder();
            for (int i = 0; i < length; i++) {
                title.append("a");
            }
            taskRequest.setTitle(title.toString());
        }
    }

    @When("I create a task with title {string}")
    public void i_create_a_task_with_title(String title) {
        taskRequest.setTitle(title);
    }

    // Execution Steps
    private void executeTaskCreation() {
        try {
            createdTask = taskService.addTask(taskRequest, currentUser);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    // Assertion Steps
    @Then("the task should be created successfully")
    public void the_task_should_be_created_successfully() {
        executeTaskCreation();
        assertNull(thrownException, "No exception should be thrown");
        assertNotNull(createdTask, "Task should be created");
        assertNotNull(createdTask.getId(), "Created task should have an ID");
    }

    @And("the task should have status {string}")
    public void the_task_should_have_status(String expectedStatus) {
        Task.TaskStatus expectedTaskStatus = Task.TaskStatus.valueOf(expectedStatus);
        assertEquals(expectedTaskStatus, createdTask.getStatus(), "Task should have correct status");
    }

    @And("the task should be assigned to my user account")
    public void the_task_should_be_assigned_to_my_user_account() {
        assertEquals(currentUser.getId(), createdTask.getUser().getId(), "Task should belong to current user");
    }

    @Then("the system should reject the task creation")
    public void the_system_should_reject_the_task_creation() {
        executeTaskCreation();
        assertNotNull(thrownException, "An exception should be thrown");
        assertNull(createdTask, "No task should be created");
    }

    @And("I should see an error message {string}")
    public void i_should_see_an_error_message(String expectedMessage) {
        assertNotNull(thrownException, "Exception should have been thrown");
        assertEquals(expectedMessage, thrownException.getMessage(), "Error message should match expected");
    }

    @And("I should see an error message containing {string}")
    public void i_should_see_an_error_message_containing(String messageFragment) {
        assertNotNull(thrownException, "Exception should have been thrown");
        assertTrue(thrownException.getMessage().contains(messageFragment), 
                   "Error message should contain: " + messageFragment);
    }

    @Then("the system should sanitize the dangerous content")
    public void the_system_should_sanitize_the_dangerous_content() {
        executeTaskCreation();
        assertNotNull(createdTask, "Task should be created despite HTML content");
    }

    @And("the title should not contain script tags")
    public void the_title_should_not_contain_script_tags() {
        assertFalse(createdTask.getTitle().contains("<script>"), 
                    "Title should not contain script tags");
    }

    @And("the description should preserve safe HTML formatting")
    public void the_description_should_preserve_safe_html_formatting() {
        assertTrue(createdTask.getDescription().contains("Bold"), 
                   "Safe HTML should be preserved");
        assertFalse(createdTask.getDescription().contains("<script>"), 
                    "Dangerous scripts should be removed");
    }

    @And("the task should be created with sanitized content")
    public void the_task_should_be_created_with_sanitized_content() {
        assertEquals("Clean Title", createdTask.getTitle().trim(), 
                     "Title should be properly sanitized");
    }

    @And("the priority should default to {string}")
    public void the_priority_should_default_to(String expectedPriority) {
        Task.TaskPriority expected = Task.TaskPriority.valueOf(expectedPriority);
        assertEquals(expected, createdTask.getPriority(), 
                     "Priority should default to " + expectedPriority);
    }

    @And("the status should default to {string}")
    public void the_status_should_default_to(String expectedStatus) {
        Task.TaskStatus expected = Task.TaskStatus.valueOf(expectedStatus);
        assertEquals(expected, createdTask.getStatus(), 
                     "Status should default to " + expectedStatus);
    }

    @Then("the result should be {string}")
    public void the_result_should_be(String outcome) {
        executeTaskCreation();
        if ("accepted".equals(outcome)) {
            assertNull(thrownException, "Task creation should succeed");
            assertNotNull(createdTask, "Task should be created");
        } else if ("rejected".equals(outcome)) {
            assertNotNull(thrownException, "Task creation should fail");
            assertNull(createdTask, "No task should be created");
        }
    }

    @And("if rejected, the error should mention {string}")
    public void if_rejected_the_error_should_mention(String errorType) {
        if (thrownException != null) {
            String message = thrownException.getMessage().toLowerCase();
            switch (errorType) {
                case "empty title" -> assertTrue(message.contains("empty") || message.contains("null"));
                case "length limit" -> assertTrue(message.contains("exceed") || message.contains("200"));
                case "none" -> { /* No error expected */ }
            }
        }
    }

    @Then("the system should apply business validation rules")
    public void the_system_should_apply_business_validation_rules() {
        executeTaskCreation();
        assertNotNull(createdTask, "Task should be created with business rules applied");
    }

    @And("the task should be created with proper audit information")
    public void the_task_should_be_created_with_proper_audit_information() {
        assertNotNull(createdTask.getCreatedAt(), "Created timestamp should be set");
        assertNotNull(createdTask.getUser(), "User association should be set");
    }

    @And("the creation timestamp should be recorded")
    public void the_creation_timestamp_should_be_recorded() {
        assertNotNull(createdTask.getCreatedAt(), "Creation timestamp should not be null");
        assertTrue(createdTask.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)), 
                   "Creation timestamp should be recent");
    }

    @And("the task should be linked to my user account")
    public void the_task_should_be_linked_to_my_user_account() {
        assertEquals(currentUser.getId(), createdTask.getUser().getId(), 
                     "Task should be properly linked to user");
    }
}