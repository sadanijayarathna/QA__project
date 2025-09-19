package com.taskmanager.ui;

import org.junit.jupiter.api.Test;

/**
 * Simple test runner for manual execution of UI tests
 */
public class UITestRunner {

    @Test
    void runUserRegistrationTest() {
        System.out.println("🚀 Starting User Registration UI Test...");
        
        UserRegistrationUITest test = new UserRegistrationUITest();
        
        try {
            // Setup the test environment
            test.setUp();
            
            // Run the test
            test.testUserRegistration_ValidData_ShouldSucceed();
            
            System.out.println("✅ User Registration Test PASSED");
            
        } catch (Exception e) {
            System.err.println("❌ User Registration Test FAILED: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                test.tearDown();
            } catch (Exception e) {
                System.err.println("Error during cleanup: " + e.getMessage());
            }
        }
    }
    
    @Test
    void runAddTaskTest() {
        System.out.println("🚀 Starting Add Task UI Test...");
        
        AddTaskUITest test = new AddTaskUITest();
        
        try {
            // Setup the test environment
            test.setUp();
            
            // Run the test
            test.testAddNewTask_ValidData_ShouldCreateTask();
            
            System.out.println("✅ Add Task Test PASSED");
            
        } catch (Exception e) {
            System.err.println("❌ Add Task Test FAILED: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                test.tearDown();
            } catch (Exception e) {
                System.err.println("Error during cleanup: " + e.getMessage());
            }
        }
    }
}
