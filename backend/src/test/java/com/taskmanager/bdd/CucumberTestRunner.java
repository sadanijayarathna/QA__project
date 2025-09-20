package com.taskmanager.bdd;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "com.taskmanager.bdd.steps",
        plugin = {"pretty", "html:target/cucumber-reports", "json:target/cucumber-reports/Cucumber.json"},
        tags = "@BDD"
)
public class CucumberTestRunner {
}