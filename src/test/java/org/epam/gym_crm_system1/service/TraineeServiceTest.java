package org.epam.gym_crm_system1.service;

import org.epam.gym_crm_system1.dao.TraineeDao;
import org.epam.gym_crm_system1.model.Trainee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TraineeServiceTest {

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private TraineeDao traineeDao;

    @Test
    void shouldCreateTrainee() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        traineeService.createTrainee(trainee);

        Trainee savedTrainee =
                traineeDao.findTraineeById(trainee.getId());

        assertNotNull(savedTrainee);
        assertEquals("John.Smith", savedTrainee.getUserName());
        assertNotNull(savedTrainee.getPassword());
        assertEquals(10, savedTrainee.getPassword().length());
        assertTrue(savedTrainee.getIsActive());
    }
}