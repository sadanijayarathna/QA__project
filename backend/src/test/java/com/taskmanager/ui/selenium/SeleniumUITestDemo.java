package com.taskmanager.ui.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Simplified Selenium UI Test Demonstration
 * 
 * This test demonstrates Selenium WebDriver capabilities by:
 * 1. Testing against a simple HTML page (created dynamically)
 * 2. Showing login form interactions
 * 3. Demonstrating form validation and submission
 * 
 * Note: In a real environment, these tests would run against your actual frontend.
 * This demo shows the testing framework and approach without requiring the full application stack.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SeleniumUITestDemo {

    private WebDriver driver;

    @BeforeAll
    static void setupWebDriver() {
        // Setup ChromeDriver using WebDriverManager
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        // Configure Chrome options for reliable testing
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run in headless mode for CI/CD
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--window-size=1920,1080");
        
        driver = new ChromeDriver(options);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Selenium Demo: Basic Web Page Interaction")
    void testBasicWebPageInteraction() {
        System.out.println("\n🎯 SELENIUM UI TEST DEMO: Basic Web Page Interaction");
        
        // Create a simple HTML page for testing
        String htmlContent = createLoginPageHTML();
        
        // Navigate to the HTML content
        driver.get("data:text/html;charset=utf-8," + htmlContent);
        
        System.out.println("GIVEN: User navigates to the login page");
        
        // Find and verify page elements
        WebElement titleElement = driver.findElement(By.tagName("h1"));
        Assertions.assertEquals("🚀 TASK TRACKER LOGIN", titleElement.getText());
        System.out.println("✅ Page title verified: " + titleElement.getText());
        
        // Test form elements
        WebElement emailField = driver.findElement(By.id("email"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginBtn"));
        
        System.out.println("WHEN: User interacts with form elements");
        
        // Test input field interactions
        emailField.sendKeys("test@example.com");
        passwordField.sendKeys("password123");
        
        // Verify input values
        Assertions.assertEquals("test@example.com", emailField.getAttribute("value"));
        Assertions.assertEquals("password123", passwordField.getAttribute("value"));
        System.out.println("✅ Input fields accept user input correctly");
        
        // Test button click
        loginButton.click();
        
        System.out.println("THEN: Form submission should be handled");
        
        // Check for result message
        WebElement resultElement = driver.findElement(By.id("result"));
        Assertions.assertTrue(resultElement.getText().contains("Login attempted"));
        System.out.println("✅ Form submission result: " + resultElement.getText());
    }

    @Test
    @Order(2)
    @DisplayName("Selenium Demo: Form Validation Testing")
    void testFormValidation() {
        System.out.println("\n🎯 SELENIUM UI TEST DEMO: Form Validation");
        
        // Create HTML page with validation
        String htmlContent = createTaskFormHTML();
        driver.get("data:text/html;charset=utf-8," + htmlContent);
        
        System.out.println("GIVEN: User is on the task creation form");
        
        WebElement titleField = driver.findElement(By.id("taskTitle"));
        WebElement descriptionField = driver.findElement(By.id("taskDescription"));
        WebElement addButton = driver.findElement(By.id("addTaskBtn"));
        
        System.out.println("WHEN: User submits form with empty required field");
        
        // Leave title empty and try to submit
        descriptionField.sendKeys("This is a test description");
        addButton.click();
        
        System.out.println("THEN: Validation should prevent submission");
        
        // Check validation message
        WebElement validationMsg = driver.findElement(By.id("validation"));
        Assertions.assertTrue(validationMsg.getText().contains("Title is required"));
        System.out.println("✅ Validation message: " + validationMsg.getText());
        
        System.out.println("WHEN: User provides valid input");
        
        // Fill in required field
        titleField.sendKeys("My Test Task");
        addButton.click();
        
        // Check success message
        WebElement resultMsg = driver.findElement(By.id("taskResult"));
        Assertions.assertTrue(resultMsg.getText().contains("Task added successfully"));
        System.out.println("✅ Success message: " + resultMsg.getText());
    }

    @Test
    @Order(3)
    @DisplayName("Selenium Demo: Element Location and Interaction")
    void testElementLocationStrategies() {
        System.out.println("\n🎯 SELENIUM UI TEST DEMO: Element Location Strategies");
        
        String htmlContent = createComplexFormHTML();
        driver.get("data:text/html;charset=utf-8," + htmlContent);
        
        System.out.println("GIVEN: Page with various element types loaded");
        
        // Demonstrate different element location strategies
        
        // 1. By ID
        WebElement byId = driver.findElement(By.id("uniqueId"));
        Assertions.assertEquals("Found by ID", byId.getText());
        System.out.println("✅ Located element by ID: " + byId.getText());
        
        // 2. By Class Name
        WebElement byClass = driver.findElement(By.className("test-class"));
        Assertions.assertEquals("Found by Class", byClass.getText());
        System.out.println("✅ Located element by Class: " + byClass.getText());
        
        // 3. By XPath
        WebElement byXPath = driver.findElement(By.xpath("//button[contains(text(), 'Click Me')]"));
        Assertions.assertTrue(byXPath.isDisplayed());
        System.out.println("✅ Located element by XPath: " + byXPath.getText());
        
        // 4. By CSS Selector
        WebElement byCss = driver.findElement(By.cssSelector("input[type='text']"));
        Assertions.assertTrue(byCss.isEnabled());
        System.out.println("✅ Located element by CSS Selector");
        
        System.out.println("WHEN: User interacts with different element types");
        
        // Test checkbox
        WebElement checkbox = driver.findElement(By.id("testCheckbox"));
        checkbox.click();
        Assertions.assertTrue(checkbox.isSelected());
        System.out.println("✅ Checkbox interaction successful");
        
        // Test dropdown
        WebElement dropdown = driver.findElement(By.id("testDropdown"));
        dropdown.sendKeys("Option 2");
        Assertions.assertEquals("option2", dropdown.getAttribute("value"));
        System.out.println("✅ Dropdown selection successful");
        
        System.out.println("THEN: All elements should respond correctly to interactions");
    }

    /**
     * Create a simple login page HTML for testing
     */
    private String createLoginPageHTML() {
        return """
            <html>
            <head>
                <title>Task Tracker Login Test</title>
                <style>
                    body { font-family: Arial, sans-serif; max-width: 400px; margin: 50px auto; padding: 20px; }
                    h1 { color: #007bff; text-align: center; }
                    .form-group { margin: 15px 0; }
                    input { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; }
                    button { width: 100%; padding: 12px; background: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; }
                    button:hover { background: #0056b3; }
                    #result { margin-top: 20px; padding: 10px; background: #d4edda; border: 1px solid #c3e6cb; border-radius: 4px; color: #155724; }
                </style>
            </head>
            <body>
                <h1>🚀 TASK TRACKER LOGIN</h1>
                <form id="loginForm">
                    <div class="form-group">
                        <input type="email" id="email" placeholder="Email" required>
                    </div>
                    <div class="form-group">
                        <input type="password" id="password" placeholder="Password" required>
                    </div>
                    <button type="button" id="loginBtn" onclick="handleLogin()">LOGIN</button>
                </form>
                <div id="result" style="display:none;"></div>
                
                <script>
                    function handleLogin() {
                        var email = document.getElementById('email').value;
                        var password = document.getElementById('password').value;
                        var result = document.getElementById('result');
                        
                        if (email && password) {
                            result.innerHTML = 'Login attempted with: ' + email;
                            result.style.display = 'block';
                        } else {
                            result.innerHTML = 'Please enter both email and password';
                            result.style.display = 'block';
                            result.style.background = '#f8d7da';
                            result.style.color = '#721c24';
                        }
                    }
                </script>
            </body>
            </html>
            """;
    }

    /**
     * Create a task creation form HTML for testing
     */
    private String createTaskFormHTML() {
        return """
            <html>
            <head>
                <title>Add Task Test</title>
                <style>
                    body { font-family: Arial, sans-serif; max-width: 500px; margin: 50px auto; padding: 20px; }
                    h1 { color: #28a745; text-align: center; }
                    .form-group { margin: 15px 0; }
                    input, textarea { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; }
                    textarea { height: 100px; resize: vertical; }
                    button { width: 100%; padding: 12px; background: #28a745; color: white; border: none; border-radius: 4px; cursor: pointer; }
                    .validation { color: #dc3545; font-size: 14px; margin-top: 5px; }
                    .success { color: #155724; background: #d4edda; padding: 10px; border-radius: 4px; margin-top: 15px; }
                </style>
            </head>
            <body>
                <h1>📋 ADD NEW TASK</h1>
                <form id="taskForm">
                    <div class="form-group">
                        <input type="text" id="taskTitle" placeholder="Enter task title..." />
                    </div>
                    <div class="form-group">
                        <textarea id="taskDescription" placeholder="Enter task description..."></textarea>
                    </div>
                    <button type="button" id="addTaskBtn" onclick="handleAddTask()">Add Task</button>
                </form>
                <div id="validation" class="validation"></div>
                <div id="taskResult" class="success" style="display:none;"></div>
                
                <script>
                    function handleAddTask() {
                        var title = document.getElementById('taskTitle').value.trim();
                        var description = document.getElementById('taskDescription').value.trim();
                        var validation = document.getElementById('validation');
                        var result = document.getElementById('taskResult');
                        
                        validation.innerHTML = '';
                        result.style.display = 'none';
                        
                        if (!title) {
                            validation.innerHTML = 'Title is required';
                            return;
                        }
                        
                        result.innerHTML = 'Task added successfully: "' + title + '"';
                        result.style.display = 'block';
                        
                        // Clear form
                        document.getElementById('taskTitle').value = '';
                        document.getElementById('taskDescription').value = '';
                    }
                </script>
            </body>
            </html>
            """;
    }

    /**
     * Create a complex form with various elements for testing different location strategies
     */
    private String createComplexFormHTML() {
        return """
            <html>
            <head>
                <title>Complex Form Test</title>
                <style>
                    body { font-family: Arial, sans-serif; max-width: 600px; margin: 30px auto; padding: 20px; }
                    .form-section { margin: 20px 0; padding: 15px; border: 1px solid #ddd; border-radius: 5px; }
                    input, select { padding: 8px; margin: 5px; border: 1px solid #ccc; border-radius: 3px; }
                    button { padding: 10px 15px; margin: 5px; background: #007bff; color: white; border: none; border-radius: 3px; cursor: pointer; }
                </style>
            </head>
            <body>
                <h1>🧪 SELENIUM ELEMENT LOCATION TEST</h1>
                
                <div class="form-section">
                    <h3>Element Location Strategies</h3>
                    <p id="uniqueId">Found by ID</p>
                    <span class="test-class">Found by Class</span>
                    <button onclick="alert('Clicked!')">Click Me</button>
                </div>
                
                <div class="form-section">
                    <h3>Form Controls</h3>
                    <input type="text" placeholder="Text Input" />
                    <br>
                    <input type="checkbox" id="testCheckbox"> <label for="testCheckbox">Test Checkbox</label>
                    <br>
                    <select id="testDropdown">
                        <option value="option1">Option 1</option>
                        <option value="option2">Option 2</option>
                        <option value="option3">Option 3</option>
                    </select>
                </div>
            </body>
            </html>
            """;
    }
}