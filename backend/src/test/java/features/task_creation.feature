@task_creation
Feature: Task Creation

  Scenario: Create a task with valid data
    Given the user provides valid task data
    When the user submits the task creation form
    Then the task is successfully created
