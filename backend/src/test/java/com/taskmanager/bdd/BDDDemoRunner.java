package com.taskmanager.bdd;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/bdd-demo.feature",
        glue = "com.taskmanager.bdd.steps",
        plugin = {
            "pretty",
            "html:target/cucumber-reports/bdd-demo",
            "json:target/cucumber-reports/bdd-demo.json"
        },
        tags = "@BDD-Demo"
)
public class BDDDemoRunner {
}