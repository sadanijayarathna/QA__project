package com.taskmanager.ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Base class for Selenium UI tests
 * Sets up WebDriver configuration and common utilities
 */
public abstract class BaseUITest {

    protected WebDriver browser;
    protected WebDriverWait wait;
    protected static final String BASE_URL = "http://localhost:5173";
    protected static final Duration TIMEOUT = Duration.ofSeconds(10);

    @BeforeAll
    static void setupWebDriverManager() {
        // Setup WebDriverManager to automatically manage ChromeDriver
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        // Configure Chrome options for testing
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        // Keep browser visible for screenshots and demonstration
        // options.addArguments("--headless"); // Commented out to see browser

        // Initialize WebDriver
        browser = new ChromeDriver(options);
        wait = new WebDriverWait(browser, TIMEOUT);
        
        // Maximize browser window
        browser.manage().window().maximize();
        
        // Set implicit wait
        browser.manage().timeouts().implicitlyWait(TIMEOUT);
    }

    @AfterEach
    void tearDown() {
        // Close browser after each test
        if (browser != null) {
            browser.quit();
        }
    }

    /**
     * Navigate to the base URL
     */
    protected void navigateToHomePage() {
        browser.get(BASE_URL);
    }

    /**
     * Navigate to a specific path
     */
    protected void navigateToPath(String path) {
        browser.get(BASE_URL + path);
    }
}
