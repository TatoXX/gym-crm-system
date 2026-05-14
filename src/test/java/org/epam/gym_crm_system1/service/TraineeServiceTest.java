package org.epam.gym_crm_system1.service;

import org.epam.gym_crm_system1.dao.TraineeDao;
import org.epam.gym_crm_system1.dao.TrainerDao;
import org.epam.gym_crm_system1.helper.UserCredentialsGenerator;
import org.epam.gym_crm_system1.model.Trainee;
import org.epam.gym_crm_system1.storage.Storage;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TraineeServiceTest {

    @Test
    void shouldCreateTrainee() {
        Storage storage = new Storage();

        TraineeDao traineeDao = new TraineeDao(storage);
        TrainerDao trainerDao = new TrainerDao(storage);

        UserCredentialsGenerator generator =
                new UserCredentialsGenerator(traineeDao, trainerDao);

        TraineeService traineeService =
                new TraineeService(traineeDao, generator);

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1),
                        1
                );

        traineeService.createTrainee(trainee);

        Trainee savedTrainee =
                traineeDao.findTraineeById(1);

        assertNotNull(savedTrainee);
        assertEquals("John.Smith", savedTrainee.getUserName());
        assertNotNull(savedTrainee.getPassword());
        assertEquals(10, savedTrainee.getPassword().length());
        assertTrue(savedTrainee.getIsActive());
    }
}