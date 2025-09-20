Feature: Task Creation and Validation
  As a task manager user
  I want to create tasks with proper validation
  So that I can manage my work efficiently and securely

  Background:
    Given I am a logged-in user
    And I have access to the task management system

  @BDD @TDD-Integration
  Scenario: Successfully create a task with valid data
    Given I want to create a new task
    When I provide a title "Complete project documentation"
    And I provide a description "Write comprehensive documentation for the project"
    And I set the priority to "HIGH"
    And I set the due date to tomorrow
    Then the task should be created successfully
    And the task should have status "PENDING"
    And the task should be assigned to my user account

  @BDD @Validation
  Scenario: Fail to create task with invalid title
    Given I want to create a new task
    When I provide an empty title
    Then the system should reject the task creation
    And I should see an error message "Title cannot be null or empty"

  @BDD @Validation
  Scenario: Fail to create task with title too long
    Given I want to create a new task
    When I provide a title that exceeds 200 characters
    Then the system should reject the task creation
    And I should see an error message containing "Title cannot exceed 200 characters"

  @BDD @Security
  Scenario: Sanitize dangerous HTML content in task data
    Given I want to create a new task
    When I provide a title containing script tags "<script>alert('xss')</script>Clean Title"
    And I provide a description with mixed HTML content "<b>Bold</b> text with <script>dangerous script</script>"
    Then the system should sanitize the dangerous content
    And the title should not contain script tags
    And the description should preserve safe HTML formatting
    And the task should be created with sanitized content

  @BDD @DefaultValues
  Scenario: Create task with default values when optional fields are empty
    Given I want to create a new task
    When I provide only a title "Simple task"
    And I leave priority and status empty
    Then the task should be created successfully
    And the priority should default to "MEDIUM"
    And the status should default to "PENDING"

  @BDD @DateValidation
  Scenario: Reject task with past due date
    Given I want to create a new task
    When I provide a valid title "Future task"
    And I set the due date to yesterday
    Then the system should reject the task creation
    And I should see an error message containing "Due date cannot be in the past"

  @BDD @EdgeCases
  Scenario Outline: Validate different title lengths
    Given I want to create a new task
    When I provide a title with <length> characters
    Then the result should be <outcome>
    And if rejected, the error should mention <error_type>

    Examples:
      | length | outcome  | error_type     |
      | 0      | rejected | empty title    |
      | 1      | accepted | none           |
      | 100    | accepted | none           |
      | 200    | accepted | none           |
      | 201    | rejected | length limit   |

  @BDD @BusinessRules
  Scenario: Apply business rules during task creation
    Given I want to create a new task
    When I create a task with title "Important meeting"
    And I set priority to "HIGH"
    Then the system should apply business validation rules
    And the task should be created with proper audit information
    And the creation timestamp should be recorded
    And the task should be linked to my user account