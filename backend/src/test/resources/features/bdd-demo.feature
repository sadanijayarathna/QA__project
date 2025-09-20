Feature: BDD Demo - Task Management
  As a user of the task management system
  I want to understand how BDD works
  So that I can see the difference between TDD and BDD

  Background:
    Given the task management system is running
    And I am a registered user

  @BDD-Demo
  Scenario: Create a simple task using BDD approach
    Given I want to create a new task
    When I provide task title "Learn BDD methodology"
    And I provide task description "Understanding Behavior Driven Development"
    Then the task should be created successfully
    And the task title should be "Learn BDD methodology"
    And the task description should be "Understanding Behavior Driven Development"

  @BDD-Demo @Validation
  Scenario: BDD validation demonstrates business rules
    Given I want to create a new task
    When I provide an empty task title
    Then I should see a validation error
    And the error message should be user-friendly
    And no task should be created

  @BDD-Demo @Security
  Scenario: BDD shows security requirements in business terms
    Given I want to create a new task
    When I provide a task title with malicious content "<script>alert('hack')</script>Dangerous Task"
    Then the system should sanitize the content
    And the task title should be "Dangerous Task"
    And no script tags should be present