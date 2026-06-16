package org.epam.gym_crm_system1.dao;

import org.epam.gym_crm_system1.model.Trainee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TraineeDaoTest {

    @Autowired
    private TraineeDao traineeDao;

    @Test
    void shouldSaveTrainee() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        trainee.setUserName("John.Smith");
        trainee.setPassword("password123");
        trainee.setIsActive(true);

        traineeDao.saveTrainee(trainee);

        Trainee savedTrainee =
                traineeDao.findTraineeById(trainee.getId());

        assertNotNull(savedTrainee);

        assertEquals("John",
                savedTrainee.getFirstName());

        assertEquals("Smith",
                savedTrainee.getLastName());

        assertEquals("Tbilisi",
                savedTrainee.getAddress());
    }

    @Test
    void shouldUpdateTrainee() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        trainee.setUserName("John.Smith");
        trainee.setPassword("password123");
        trainee.setIsActive(true);

        traineeDao.saveTrainee(trainee);

        trainee.setAddress("Batumi");

        traineeDao.updateTrainee(trainee);

        Trainee updatedTrainee =
                traineeDao.findTraineeById(trainee.getId());

        assertNotNull(updatedTrainee);

        assertEquals("Batumi",
                updatedTrainee.getAddress());
    }

    @Test
    void shouldDeleteTrainee() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        trainee.setUserName("John.Smith");
        trainee.setPassword("password123");
        trainee.setIsActive(true);

        traineeDao.saveTrainee(trainee);

        int traineeId = trainee.getId();

        traineeDao.deleteTraineeById(traineeId);

        assertNull(
                traineeDao.findTraineeById(traineeId)
        );
    }
}