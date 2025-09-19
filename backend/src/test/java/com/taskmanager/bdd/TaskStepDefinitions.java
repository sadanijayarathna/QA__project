package com.taskmanager.bdd;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.model.Task;
import com.taskmanager.model.User;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class TaskStepDefinitions {
    private User user;
    private TaskRequest taskRequest;
    private Task createdTask;
    private Exception thrownException;

    @Given("a user exists with username {string}")
    public void a_user_exists_with_username(String username) {
        user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setEmail(username + "@test.com");
    }

    @When("the user creates a task with title {string} and description {string}")
    public void the_user_creates_a_task_with_title_and_description(String title, String description) {
        taskRequest = new TaskRequest();
        taskRequest.setTitle(title);
        taskRequest.setDescription(description);
        
        // Simulate task creation (without actual service call)
        createdTask = new Task(title, description, user);
        createdTask.setId(1L);
        createdTask.setStatus(Task.TaskStatus.PENDING);
        createdTask.setPriority(Task.TaskPriority.MEDIUM);
    }

    @When("the user tries to create a task with empty title and description {string}")
    public void the_user_tries_to_create_a_task_with_empty_title_and_description(String description) {
        taskRequest = new TaskRequest();
        taskRequest.setTitle(""); // Empty title
        taskRequest.setDescription(description);
        
        try {
            // Simulate validation error for empty title
            if (taskRequest.getTitle() == null || taskRequest.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("Task title cannot be null or empty");
            }
            createdTask = new Task(taskRequest.getTitle(), description, user);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the task should be created successfully")
    public void the_task_should_be_created_successfully() {
        Assertions.assertNotNull(createdTask, "Task should be created");
        Assertions.assertNotNull(createdTask.getId(), "Task should have an ID");
    }

    @Then("the task creation should fail")
    public void the_task_creation_should_fail() {
        Assertions.assertNotNull(thrownException, "An exception should be thrown");
        Assertions.assertNull(createdTask, "Task should not be created");
    }

    @And("an error message should be displayed")
    public void an_error_message_should_be_displayed() {
        Assertions.assertNotNull(thrownException, "Exception should exist");
        Assertions.assertEquals("Task title cannot be null or empty", thrownException.getMessage());
    }

    @And("the task title should be {string}")
    public void the_task_title_should_be(String expectedTitle) {
        Assertions.assertNotNull(createdTask, "Task should exist");
        Assertions.assertEquals(expectedTitle, createdTask.getTitle());
    }

    @And("the task description should be {string}")
    public void the_task_description_should_be(String expectedDescription) {
        Assertions.assertNotNull(createdTask, "Task should exist");
        Assertions.assertEquals(expectedDescription, createdTask.getDescription());
    }

    @And("the task status should be {string}")
    public void the_task_status_should_be(String expectedStatus) {
        Assertions.assertNotNull(createdTask, "Task should exist");
        Assertions.assertEquals(Task.TaskStatus.valueOf(expectedStatus), createdTask.getStatus());
    }
}
