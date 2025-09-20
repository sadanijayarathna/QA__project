package com.taskmanager.ui.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Standalone Selenium UI Test Demonstration for Task Management System
 * 
 * This test demonstrates Selenium WebDriver capabilities for UI testing:
 * 1. Login Form Testing - Tests login form interactions and validation
 * 2. Task Creation Testing - Tests task form submission and validation
 * 
 * These tests run independently without requiring the Spring Boot application context
 * and demonstrate the complete Selenium testing approach for UI automation.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StandaloneSeleniumUITest {

    private static WebDriver driver;

    @BeforeAll
    static void setupWebDriver() {
        // Setup ChromeDriver using WebDriverManager
        WebDriverManager.chromedriver().setup();
        
        // Configure Chrome options for reliable testing
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run in headless mode for CI/CD
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        
        driver = new ChromeDriver(options);
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    @DisplayName("UI Test 1: Login Form - Valid credentials and form interactions")
    void testLoginFormInteraction() {
        System.out.println("\n🎯 SELENIUM UI TEST 1: Login Form Testing");
        System.out.println("=========================================");
        
        // Create and navigate to login page
        String loginPageHTML = createLoginPageHTML();
        driver.get("data:text/html;charset=utf-8," + loginPageHTML);
        
        System.out.println("GIVEN: User navigates to the Task Tracker login page");
        
        // Verify page title and branding
        WebElement titleElement = driver.findElement(By.tagName("h1"));
        Assertions.assertEquals("🚀 TASK TRACKER LOGIN", titleElement.getText());
        System.out.println("✅ Login page loaded with correct branding");
        
        // Locate form elements
        WebElement emailField = driver.findElement(By.id("email"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement rememberCheckbox = driver.findElement(By.id("remember"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));
        
        // Verify form elements are present and enabled
        Assertions.assertTrue(emailField.isDisplayed(), "Email field should be visible");
        Assertions.assertTrue(passwordField.isDisplayed(), "Password field should be visible");
        Assertions.assertTrue(loginButton.isDisplayed(), "Login button should be visible");
        System.out.println("✅ All login form elements are present and accessible");
        
        System.out.println("\nWHEN: User enters valid login credentials");
        
        // Test form input interactions
        emailField.clear();
        emailField.sendKeys("admin@tasktracker.com");
        passwordField.clear();
        passwordField.sendKeys("admin123");
        
        // Test checkbox interaction
        if (!rememberCheckbox.isSelected()) {
            rememberCheckbox.click();
        }
        
        // Verify input values
        Assertions.assertEquals("admin@tasktracker.com", emailField.getAttribute("value"));
        Assertions.assertEquals("admin123", passwordField.getAttribute("value"));
        Assertions.assertTrue(rememberCheckbox.isSelected(), "Remember me checkbox should be checked");
        System.out.println("✅ Form accepts user input correctly");
        System.out.println("   Email: " + emailField.getAttribute("value"));
        System.out.println("   Password: [PROTECTED]");
        System.out.println("   Remember Me: " + rememberCheckbox.isSelected());
        
        System.out.println("\nWHEN: User clicks the login button");
        loginButton.click();
        
        System.out.println("\nTHEN: Login process should be initiated");
        
        // Check for result message
        WebElement resultElement = driver.findElement(By.id("result"));
        Assertions.assertTrue(resultElement.isDisplayed(), "Result message should be visible");
        
        String resultText = resultElement.getText();
        Assertions.assertTrue(resultText.contains("Login successful"), 
            "Should show successful login message");
        System.out.println("✅ Login result: " + resultText);
        System.out.println("✅ LOGIN UI TEST COMPLETED SUCCESSFULLY");
    }

    @Test
    @Order(2)
    @DisplayName("UI Test 2: Task Creation Form - Add task functionality")
    void testTaskCreationForm() {
        System.out.println("\n🎯 SELENIUM UI TEST 2: Task Creation Form");
        System.out.println("=========================================");
        
        // Create and navigate to task creation page
        String taskPageHTML = createTaskCreationPageHTML();
        driver.get("data:text/html;charset=utf-8," + taskPageHTML);
        
        System.out.println("GIVEN: User is on the task creation page");
        
        // Verify page elements
        WebElement pageTitle = driver.findElement(By.tagName("h1"));
        Assertions.assertEquals("📋 TASK MANAGER", pageTitle.getText());
        System.out.println("✅ Task creation page loaded correctly");
        
        // Locate form elements
        WebElement taskTitleInput = driver.findElement(By.id("taskTitle"));
        WebElement taskDescInput = driver.findElement(By.id("taskDescription"));
        WebElement prioritySelect = driver.findElement(By.id("priority"));
        WebElement addTaskButton = driver.findElement(By.id("addTaskBtn"));
        
        System.out.println("✅ Task form elements located successfully");
        
        // Test form validation - empty title
        System.out.println("\nWHEN: User tries to submit empty task");
        
        addTaskButton.click();
        
        WebElement validationMsg = driver.findElement(By.id("validation"));
        Assertions.assertTrue(validationMsg.getText().contains("Task title is required"), 
            "Should show validation error for empty title");
        System.out.println("✅ Form validation works: " + validationMsg.getText());
        
        // Test successful task creation
        System.out.println("\nWHEN: User enters valid task information");
        
        String taskTitle = "Automated UI Test Task";
        String taskDescription = "This task was created by Selenium WebDriver to test the task creation functionality. Testing includes form validation, input handling, and submission.";
        
        taskTitleInput.clear();
        taskTitleInput.sendKeys(taskTitle);
        
        taskDescInput.clear();
        taskDescInput.sendKeys(taskDescription);
        
        // Test dropdown selection
        prioritySelect.sendKeys("High");
        
        // Verify inputs
        Assertions.assertEquals(taskTitle, taskTitleInput.getAttribute("value"));
        Assertions.assertEquals(taskDescription, taskDescInput.getAttribute("value"));
        Assertions.assertEquals("high", prioritySelect.getAttribute("value"));
        System.out.println("✅ Task form accepts all input correctly");
        System.out.println("   Title: " + taskTitle);
        System.out.println("   Description: " + taskDescription.substring(0, 50) + "...");
        System.out.println("   Priority: High");
        
        System.out.println("\nWHEN: User submits the task form");
        addTaskButton.click();
        
        System.out.println("\nTHEN: Task should be created successfully");
        
        // Check for success message
        WebElement successMsg = driver.findElement(By.id("taskResult"));
        Assertions.assertTrue(successMsg.isDisplayed(), "Success message should be visible");
        
        String successText = successMsg.getText();
        Assertions.assertTrue(successText.contains("Task created successfully"), 
            "Should show task creation success");
        Assertions.assertTrue(successText.contains(taskTitle), 
            "Success message should include task title");
        System.out.println("✅ Task creation result: " + successText);
        
        // Verify form was cleared after successful submission
        Assertions.assertEquals("", taskTitleInput.getAttribute("value"), 
            "Title field should be cleared after submission");
        Assertions.assertEquals("", taskDescInput.getAttribute("value"), 
            "Description field should be cleared after submission");
        System.out.println("✅ Form cleared after successful submission");
        
        // Test task appears in list
        WebElement taskList = driver.findElement(By.id("taskList"));
        Assertions.assertTrue(taskList.getText().contains(taskTitle), 
            "New task should appear in task list");
        System.out.println("✅ New task appears in task list");
        System.out.println("✅ TASK CREATION UI TEST COMPLETED SUCCESSFULLY");
    }

    /**
     * Create a comprehensive login page HTML for testing
     */
    private String createLoginPageHTML() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Task Tracker Login</title>
                <style>
                    body { 
                        font-family: 'Segoe UI', Arial, sans-serif; 
                        max-width: 400px; 
                        margin: 50px auto; 
                        padding: 30px; 
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: #333;
                    }
                    .login-container {
                        background: white;
                        padding: 40px;
                        border-radius: 12px;
                        box-shadow: 0 10px 25px rgba(0,0,0,0.1);
                    }
                    h1 { 
                        color: #007bff; 
                        text-align: center; 
                        margin-bottom: 30px;
                        font-size: 24px;
                    }
                    .form-group { 
                        margin: 20px 0; 
                    }
                    label {
                        display: block;
                        margin-bottom: 5px;
                        font-weight: bold;
                        color: #555;
                    }
                    input[type="email"], input[type="password"] { 
                        width: 100%; 
                        padding: 12px; 
                        border: 2px solid #ddd; 
                        border-radius: 6px; 
                        box-sizing: border-box;
                        font-size: 16px;
                    }
                    input[type="email"]:focus, input[type="password"]:focus {
                        border-color: #007bff;
                        outline: none;
                    }
                    .checkbox-group {
                        display: flex;
                        align-items: center;
                        margin: 15px 0;
                    }
                    input[type="checkbox"] {
                        margin-right: 8px;
                    }
                    button { 
                        width: 100%; 
                        padding: 14px; 
                        background: #007bff; 
                        color: white; 
                        border: none; 
                        border-radius: 6px; 
                        cursor: pointer; 
                        font-size: 16px;
                        font-weight: bold;
                        text-transform: uppercase;
                    }
                    button:hover { 
                        background: #0056b3; 
                    }
                    #result { 
                        margin-top: 20px; 
                        padding: 15px; 
                        background: #d4edda; 
                        border: 1px solid #c3e6cb; 
                        border-radius: 6px; 
                        color: #155724;
                        display: none;
                    }
                    .error {
                        background: #f8d7da !important;
                        border-color: #f5c6cb !important;
                        color: #721c24 !important;
                    }
                </style>
            </head>
            <body>
                <div class="login-container">
                    <h1>🚀 TASK TRACKER LOGIN</h1>
                    <form id="loginForm">
                        <div class="form-group">
                            <label for="email">Email Address</label>
                            <input type="email" id="email" placeholder="Enter your email" required>
                        </div>
                        <div class="form-group">
                            <label for="password">Password</label>
                            <input type="password" id="password" placeholder="Enter your password" required>
                        </div>
                        <div class="checkbox-group">
                            <input type="checkbox" id="remember">
                            <label for="remember">Remember me</label>
                        </div>
                        <button type="button" id="loginBtn" onclick="handleLogin()">LOGIN</button>
                    </form>
                    <div id="result"></div>
                </div>
                
                <script>
                    function handleLogin() {
                        var email = document.getElementById('email').value;
                        var password = document.getElementById('password').value;
                        var result = document.getElementById('result');
                        
                        if (!email || !password) {
                            result.innerHTML = '⚠️ Please enter both email and password';
                            result.className = 'error';
                            result.style.display = 'block';
                            return;
                        }
                        
                        // Simulate successful login
                        result.innerHTML = '✅ Login successful! Welcome to Task Tracker, ' + email;
                        result.className = '';
                        result.style.display = 'block';
                        
                        // Simulate loading state
                        var button = document.getElementById('loginBtn');
                        button.innerHTML = 'LOGGING IN...';
                        button.disabled = true;
                        
                        setTimeout(function() {
                            button.innerHTML = 'LOGIN';
                            button.disabled = false;
                        }, 1000);
                    }
                </script>
            </body>
            </html>
            """;
    }

    /**
     * Create a task creation page HTML for testing
     */
    private String createTaskCreationPageHTML() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Task Manager - Add Task</title>
                <style>
                    body { 
                        font-family: 'Segoe UI', Arial, sans-serif; 
                        max-width: 600px; 
                        margin: 30px auto; 
                        padding: 30px; 
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: #333;
                    }
                    .task-container {
                        background: white;
                        padding: 40px;
                        border-radius: 12px;
                        box-shadow: 0 10px 25px rgba(0,0,0,0.1);
                    }
                    h1 { 
                        color: #28a745; 
                        text-align: center; 
                        margin-bottom: 30px;
                        font-size: 28px;
                    }
                    .form-group { 
                        margin: 20px 0; 
                    }
                    label {
                        display: block;
                        margin-bottom: 8px;
                        font-weight: bold;
                        color: #555;
                    }
                    input, textarea, select { 
                        width: 100%; 
                        padding: 12px; 
                        border: 2px solid #ddd; 
                        border-radius: 6px; 
                        box-sizing: border-box;
                        font-size: 16px;
                    }
                    textarea { 
                        height: 120px; 
                        resize: vertical; 
                        font-family: inherit;
                    }
                    input:focus, textarea:focus, select:focus {
                        border-color: #28a745;
                        outline: none;
                    }
                    button { 
                        width: 100%; 
                        padding: 14px; 
                        background: #28a745; 
                        color: white; 
                        border: none; 
                        border-radius: 6px; 
                        cursor: pointer; 
                        font-size: 16px;
                        font-weight: bold;
                        text-transform: uppercase;
                        margin-top: 10px;
                    }
                    button:hover { 
                        background: #218838; 
                    }
                    .validation { 
                        color: #dc3545; 
                        font-size: 14px; 
                        margin-top: 5px; 
                        padding: 8px;
                        background: #f8d7da;
                        border: 1px solid #f5c6cb;
                        border-radius: 4px;
                        display: none;
                    }
                    .success { 
                        color: #155724; 
                        background: #d4edda; 
                        padding: 15px; 
                        border: 1px solid #c3e6cb;
                        border-radius: 6px; 
                        margin-top: 15px; 
                        display: none;
                    }
                    .task-list {
                        margin-top: 30px;
                        padding: 20px;
                        background: #f8f9fa;
                        border-radius: 6px;
                        border: 1px solid #dee2e6;
                    }
                    .task-item {
                        padding: 10px;
                        margin: 10px 0;
                        background: white;
                        border-left: 4px solid #28a745;
                        border-radius: 4px;
                    }
                </style>
            </head>
            <body>
                <div class="task-container">
                    <h1>📋 TASK MANAGER</h1>
                    <p style="text-align: center; color: #666; margin-bottom: 30px;">Create and manage your tasks efficiently</p>
                    
                    <form id="taskForm">
                        <div class="form-group">
                            <label for="taskTitle">Task Title *</label>
                            <input type="text" id="taskTitle" placeholder="Enter task title..." maxlength="100">
                        </div>
                        
                        <div class="form-group">
                            <label for="taskDescription">Task Description</label>
                            <textarea id="taskDescription" placeholder="Enter task description..." maxlength="500"></textarea>
                        </div>
                        
                        <div class="form-group">
                            <label for="priority">Priority Level</label>
                            <select id="priority">
                                <option value="low">Low Priority</option>
                                <option value="medium" selected>Medium Priority</option>
                                <option value="high">High Priority</option>
                            </select>
                        </div>
                        
                        <button type="button" id="addTaskBtn" onclick="handleAddTask()">Add Task</button>
                    </form>
                    
                    <div id="validation" class="validation"></div>
                    <div id="taskResult" class="success"></div>
                    
                    <div class="task-list">
                        <h3>📝 Task List</h3>
                        <div id="taskList">
                            <p style="color: #666; font-style: italic;">No tasks yet. Create your first task above!</p>
                        </div>
                    </div>
                </div>
                
                <script>
                    var taskCount = 0;
                    
                    function handleAddTask() {
                        var title = document.getElementById('taskTitle').value.trim();
                        var description = document.getElementById('taskDescription').value.trim();
                        var priority = document.getElementById('priority').value;
                        var validation = document.getElementById('validation');
                        var result = document.getElementById('taskResult');
                        var button = document.getElementById('addTaskBtn');
                        
                        // Clear previous messages
                        validation.style.display = 'none';
                        result.style.display = 'none';
                        
                        // Validate required fields
                        if (!title) {
                            validation.innerHTML = '⚠️ Task title is required';
                            validation.style.display = 'block';
                            return;
                        }
                        
                        // Simulate loading
                        button.innerHTML = 'ADDING TASK...';
                        button.disabled = true;
                        
                        setTimeout(function() {
                            // Add task to list
                            addTaskToList(title, description, priority);
                            
                            // Show success message
                            result.innerHTML = '✅ Task created successfully: "' + title + '" with ' + priority + ' priority';
                            result.style.display = 'block';
                            
                            // Clear form
                            document.getElementById('taskTitle').value = '';
                            document.getElementById('taskDescription').value = '';
                            document.getElementById('priority').value = 'medium';
                            
                            // Reset button
                            button.innerHTML = 'ADD TASK';
                            button.disabled = false;
                            
                            taskCount++;
                        }, 800);
                    }
                    
                    function addTaskToList(title, description, priority) {
                        var taskList = document.getElementById('taskList');
                        
                        if (taskCount === 0) {
                            taskList.innerHTML = '';
                        }
                        
                        var priorityColor = priority === 'high' ? '#dc3545' : priority === 'medium' ? '#ffc107' : '#28a745';
                        var taskItem = document.createElement('div');
                        taskItem.className = 'task-item';
                        taskItem.innerHTML = 
                            '<strong>' + title + '</strong> ' +
                            '<span style="background: ' + priorityColor + '; color: white; padding: 2px 8px; border-radius: 12px; font-size: 12px; margin-left: 10px;">' + 
                            priority.toUpperCase() + '</span>' +
                            (description ? '<br><small style="color: #666;">' + description + '</small>' : '') +
                            '<br><small style="color: #999;">Created: ' + new Date().toLocaleString() + '</small>';
                        
                        taskList.appendChild(taskItem);
                    }
                </script>
            </body>
            </html>
            """;
    }
}