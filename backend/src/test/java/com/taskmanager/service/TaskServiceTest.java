    package com.taskmanager.service;

    import com.taskmanager.dto.TaskRequest;
    import com.taskmanager.model.Task;
    import com.taskmanager.model.User;
    import com.taskmanager.repository.TaskRepository;
    import org.junit.jupiter.api.*;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mockito.InjectMocks;
    import org.mockito.Mock;
    import org.mockito.junit.jupiter.MockitoExtension;

    import java.util.Optional;

    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.ArgumentMatchers.any;
    import static org.mockito.Mockito.*;

    /**
     * TDD Test Class for TaskService
     * Testing core features: Task Creation and Status Management
     */
    @ExtendWith(MockitoExtension.class)
    class TaskServiceTest {

        @Mock
        private TaskRepository taskRepository;

        @InjectMocks
        private TaskService taskService;

        private User testUser;
        private TaskRequest validTaskRequest;

        @BeforeEach
        void setUp() {
            // Initialize test data before each test
            testUser = new User();
            testUser.setId(1L);
            testUser.setUsername("testuser");
            testUser.setEmail("test@example.com");

            validTaskRequest = new TaskRequest();
            validTaskRequest.setTitle("Test Task");
            validTaskRequest.setDescription("Test Description");
            validTaskRequest.setStatus(Task.TaskStatus.PENDING);
            validTaskRequest.setPriority(Task.TaskPriority.MEDIUM);
        }


        // ===== FEATURE 1: TASK CREATION TESTS (RED PHASE) =====

        @Test
        void testCreateTask_WithValidData_ShouldReturnTask() {
            // Given: A valid task request
            validTaskRequest.setTitle("Test Task");
            validTaskRequest.setDescription("Test Description");
            validTaskRequest.setStatus(Task.TaskStatus.PENDING);
            validTaskRequest.setPriority(Task.TaskPriority.MEDIUM);

            Task expectedTask = new Task("Test Task", "Test Description", testUser);
            expectedTask.setId(1L);

            // Mock repository behavior to return the expected task
            when(taskRepository.save(any(Task.class))).thenReturn(expectedTask);

            // When: Creating the task with valid data
            Task actualTask = taskService.createTask(validTaskRequest, testUser);

            // Then: The task should be created successfully
            assertNotNull(actualTask);
            assertEquals("Test Task", actualTask.getTitle());
            assertEquals("Test Description", actualTask.getDescription());
            assertEquals(testUser, actualTask.getUser());
            assertEquals(Task.TaskStatus.PENDING, actualTask.getStatus());
            verify(taskRepository, times(1)).save(any(Task.class));
        }


        @Test
        void testCreateTask_WithNullTitle_ShouldThrowException() {
            // Given: A task request with null title
            validTaskRequest.setTitle(null);

            // When & Then: Should throw IllegalArgumentException
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> taskService.createTaskWithValidation(validTaskRequest, testUser)
            );

            // Assert: The exception message should be as expected
            assertEquals("Title cannot be null or empty", exception.getMessage());
            verify(taskRepository, never()).save(any(Task.class));
        }


        @Test
        void testCreateTask_WithEmptyTitle_ShouldThrowException() {
            // Given: Task request with empty title
            validTaskRequest.setTitle("");

            // When & Then: Should throw IllegalArgumentException
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> taskService.createTaskWithValidation(validTaskRequest, testUser)
            );

            assertEquals("Title cannot be null or empty", exception.getMessage());
            verify(taskRepository, never()).save(any(Task.class));
        }

        @Test
        void testCreateTask_WithTitleTooLong_ShouldThrowException() {
            // Given: Task request with title longer than 200 characters
            String longTitle = "a".repeat(201);
            validTaskRequest.setTitle(longTitle);

            // When & Then: Should throw IllegalArgumentException
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> taskService.createTaskWithValidation(validTaskRequest, testUser)
            );

            assertEquals("Title cannot exceed 200 characters", exception.getMessage());
            verify(taskRepository, never()).save(any(Task.class));
        }

        // ===== FEATURE 2: TASK STATUS MANAGEMENT TESTS (RED PHASE) =====

        @Test
        void testUpdateTaskStatus_FromPendingToInProgress_ShouldSucceed() {
            // Given: Existing task with PENDING status
            Task existingTask = new Task("Test Task", "Description", testUser);
            existingTask.setId(1L);
            existingTask.setStatus(Task.TaskStatus.PENDING);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
            when(taskRepository.save(any(Task.class))).thenReturn(existingTask);

            // When: Updating status to IN_PROGRESS
            Task updatedTask = taskService.updateTaskStatus(1L, Task.TaskStatus.IN_PROGRESS);

            // Then: Status should be updated
            assertNotNull(updatedTask);
            assertEquals(Task.TaskStatus.IN_PROGRESS, updatedTask.getStatus());
            verify(taskRepository, times(1)).save(existingTask);
        }

        @Test
        void testUpdateTaskStatus_FromCompletedToPending_ShouldThrowException() {
            // Given: Existing task with COMPLETED status
            Task existingTask = new Task("Test Task", "Description", testUser);
            existingTask.setId(1L);
            existingTask.setStatus(Task.TaskStatus.COMPLETED);

            when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));

            // When & Then: Should throw IllegalStateException for invalid transition
            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> taskService.updateTaskStatus(1L, Task.TaskStatus.PENDING)
            );

            assertEquals("Cannot transition from COMPLETED to PENDING", exception.getMessage());
            verify(taskRepository, never()).save(any(Task.class));
        }

        @Test
        void testUpdateTaskStatus_WithNonExistentTask_ShouldThrowException() {
            // Given: Non-existent task ID
            when(taskRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then: Should throw RuntimeException
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> taskService.updateTaskStatus(999L, Task.TaskStatus.IN_PROGRESS)
            );

            assertEquals("Task not found with id: 999", exception.getMessage());
            verify(taskRepository, never()).save(any(Task.class));
        }

        @AfterEach
        void tearDown() {
            // Clean up the test data after each test
            testUser = null;
            validTaskRequest = null;
        }
    }
