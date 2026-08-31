package org.epam.trainerworkloadservice.component;

import io.cucumber.spring.CucumberContextConfiguration;
import org.epam.trainerworkloadservice.TrainerWorkloadServiceApplication;
import org.epam.trainerworkloadservice.repository.TrainerWorkloadRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@CucumberContextConfiguration
@SpringBootTest(
        classes = TrainerWorkloadServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@ActiveProfiles("component-test")
public class CucumberSpringConfiguration {

    @MockitoBean
    private TrainerWorkloadRepository trainerWorkloadRepository;
}