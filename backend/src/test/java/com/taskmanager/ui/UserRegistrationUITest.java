package com.taskmanager.ui;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Selenium UI Test for User Registration Scenario
 * Tests the complete user signup flow
 */
public class UserRegistrationUITest extends BaseUITest {

    @Test
    void testUserRegistration_ValidData_ShouldSucceed() {
        // Step 1: Navigate to the application home page
        browser.get(BASE_URL);
        
        // Step 2: Click on Signup/Register link
        WebElement signupLink = wait.until(
            ExpectedConditions.elementToBeClickable(By.linkText("Sign Up"))
        );
        signupLink.click();
        
        // Step 3: Verify we're on the signup page
        wait.until(ExpectedConditions.urlContains("signup"));
        String currentUrl = browser.getCurrentUrl();
        assertTrue(currentUrl.contains("signup"), "Should navigate to signup page");
        
        // Step 4: Fill in the registration form
        // Find and fill username field
        WebElement usernameField = browser.findElement(By.name("username"));
        usernameField.clear();
        usernameField.sendKeys("testuser123");
        
        // Find and fill email field
        WebElement emailField = browser.findElement(By.name("email"));
        emailField.clear();
        emailField.sendKeys("testuser123@example.com");
        
        // Find and fill password field
        WebElement passwordField = browser.findElement(By.name("password"));
        passwordField.clear();
        passwordField.sendKeys("testpassword123");
        
        // Find and fill confirm password field (if exists)
        try {
            WebElement confirmPasswordField = browser.findElement(By.name("confirmPassword"));
            confirmPasswordField.clear();
            confirmPasswordField.sendKeys("testpassword123");
        } catch (Exception e) {
            // Confirm password field might not exist
            System.out.println("Confirm password field not found - continuing");
        }
        
        // Step 5: Submit the registration form
        WebElement submitButton = browser.findElement(By.xpath("//button[@type='submit']"));
        submitButton.click();
        
        // Step 6: Verify successful registration
        // Wait for either redirect to login page or success message
        try {
            // Option 1: Check if redirected to login page
            wait.until(ExpectedConditions.urlContains("login"));
            String redirectUrl = browser.getCurrentUrl();
            assertTrue(redirectUrl.contains("login"), "Should redirect to login page after signup");
        } catch (Exception e) {
            // Option 2: Check for success message on the same page
            WebElement successMessage = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[contains(text(), 'Registration successful') or contains(text(), 'Account created')]")
                )
            );
            assertNotNull(successMessage, "Should display success message");
        }
        
        System.out.println("✅ User Registration Test Completed Successfully");
    }

    @Test
    void testUserRegistration_EmptyFields_ShouldShowValidation() {
        // Step 1: Navigate to signup page
        browser.get(BASE_URL);
        
        // Click signup link
        WebElement signupLink = wait.until(
            ExpectedConditions.elementToBeClickable(By.linkText("Sign Up"))
        );
        signupLink.click();
        
        // Step 2: Try to submit empty form
        WebElement submitButton = browser.findElement(By.xpath("//button[@type='submit']"));
        submitButton.click();
        
        // Step 3: Verify validation messages appear
        try {
            WebElement validationMessage = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[contains(text(), 'required') or contains(text(), 'Please') or contains(text(), 'fill')]")
                )
            );
            assertNotNull(validationMessage, "Should show validation message for empty fields");
        } catch (Exception e) {
            // Check if browser's built-in validation prevents submission
            WebElement usernameField = browser.findElement(By.name("username"));
            String validationMessage = usernameField.getAttribute("validationMessage");
            assertNotNull(validationMessage, "Should show browser validation for required fields");
        }
        
        System.out.println("✅ Empty Fields Validation Test Completed Successfully");
    }
}
