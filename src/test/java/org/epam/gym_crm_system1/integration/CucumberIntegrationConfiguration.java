package org.epam.gym_crm_system1.integration;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@ContextConfiguration(
        classes = CucumberIntegrationConfiguration.TestConfiguration.class
)
public class CucumberIntegrationConfiguration {

    @Configuration
    static class TestConfiguration {
    }
}