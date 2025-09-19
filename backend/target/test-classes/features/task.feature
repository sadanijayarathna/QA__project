Feature: Task management
  As a user, I want to add a new task so that I can track my work

  Scenario: Add a new task with valid details
    Given a user exists with username "testuser"
    When the user creates a task with title "Write BDD tests" and description "Add Cucumber BDD tests to the project"
    Then the task should be created successfully
    And the task title should be "Write BDD tests"
    And the task description should be "Add Cucumber BDD tests to the project"
    And the task status should be "PENDING"

  Scenario: Validate task creation with empty title
    Given a user exists with username "testuser"
    When the user tries to create a task with empty title and description "Some description"
    Then the task creation should fail
    And an error message should be displayed
