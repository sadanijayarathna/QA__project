package com.taskmanager.bdd.steps;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.model.Task;
import com.taskmanager.model.User;
import com.taskmanager.service.TaskService;
import io.cucumber.java.en.*;

import static org.junit.jupiter.api.Assertions.*;

public class TaskSteps {

    private TaskService taskService;
    private TaskRequest taskRequest;
    private Task createdTask;
    private String errorMessage;

    // Initialize taskService and test user
    public TaskSteps() {
        taskService = new TaskService(); // Use dependency injection or mock in real cases
        taskRequest = new TaskRequest();
    }

    // Given: I am logged in as a valid user
    @Given("I am logged in as a valid user")
    public void i_am_logged_in_as_a_valid_user() {
        // For simplicity, we assume a valid user is already available
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        taskRequest.setUser(user); // Assuming the taskRequest is tied to the user
    }

    // When: I create a task with valid data
    @When("I create a task with title {string}, description {string}, and priority {string}")
    public void i_create_a_task_with_valid_data(String title, String description, String priority) {
        taskRequest.setTitle(title);
        taskRequest.setDescription(description);
        taskRequest.setPriority(Task.TaskPriority.valueOf(priority.toUpperCase()));

        try {
            createdTask = taskService.createTask(taskRequest, taskRequest.getUser());
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }
    }

    // Then: the task should be created successfully with title and description
    @Then("the task should be created successfully with title {string} and description {string}")
    public void the_task_should_be_created_successfully(String expectedTitle, String expectedDescription) {
        assertNotNull(createdTask);
        assertEquals(expectedTitle, createdTask.getTitle());
        assertEquals(expectedDescription, createdTask.getDescription());
    }

    // Then: I should see an error message
    @Then("I should see an error message {string}")
    public void i_should_see_an_error_message(String expectedErrorMessage) {
        assertEquals(expectedErrorMessage, errorMessage);
    }
}
