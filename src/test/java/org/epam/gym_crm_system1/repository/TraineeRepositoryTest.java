package org.epam.gym_crm_system1.repository;

import org.epam.gym_crm_system1.model.Trainee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TraineeRepositoryTest {

    @Autowired
    private TraineeRepository traineeRepository;

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

        traineeRepository.saveTrainee(trainee);

        Trainee savedTrainee =
                traineeRepository.findTraineeById(trainee.getId());

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

        traineeRepository.saveTrainee(trainee);

        trainee.setAddress("Batumi");

        traineeRepository.updateTrainee(trainee);

        Trainee updatedTrainee =
                traineeRepository.findTraineeById(trainee.getId());

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

        traineeRepository.saveTrainee(trainee);

        int traineeId = trainee.getId();

        traineeRepository.deleteTraineeById(traineeId);

        assertNull(
                traineeRepository.findTraineeById(traineeId)
        );
    }
}