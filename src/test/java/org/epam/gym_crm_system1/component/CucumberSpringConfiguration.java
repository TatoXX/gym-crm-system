package org.epam.gym_crm_system1.component;

import io.cucumber.spring.CucumberContextConfiguration;
import org.epam.gym_crm_system1.GymCrmSystem1Application;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(
        classes = GymCrmSystem1Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@AutoConfigureMockMvc
@ActiveProfiles("component-test")
public class CucumberSpringConfiguration {
}