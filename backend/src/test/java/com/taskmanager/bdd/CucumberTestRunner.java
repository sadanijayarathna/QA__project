package com.taskmanager.bdd;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "com.taskmanager.bdd.stepdefinitions", // More specific path
        plugin = {"pretty", "summary"}
)
public class CucumberTestRunner {
}