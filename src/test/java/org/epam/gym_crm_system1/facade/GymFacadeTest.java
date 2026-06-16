package org.epam.gym_crm_system1.facade;

import org.epam.gym_crm_system1.model.Trainee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class GymFacadeTest {

    @Autowired
    private GymFacade facade;

    @Test
    void shouldCreateTraineeThroughFacade() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        facade.createTrainee(trainee);

        assertNotNull(
                facade.selectTraineeById(trainee.getId())
        );
    }
}