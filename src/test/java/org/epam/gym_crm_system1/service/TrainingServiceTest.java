package org.epam.gym_crm_system1.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.epam.gym_crm_system1.repository.TrainingRepository;
import org.epam.gym_crm_system1.model.Trainee;
import org.epam.gym_crm_system1.model.Trainer;
import org.epam.gym_crm_system1.model.Training;
import org.epam.gym_crm_system1.model.TrainingType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.epam.gym_crm_system1.exception.ValidationException;
import java.time.LocalDate;
import org.epam.gym_crm_system1.client.TrainerWorkloadClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TrainingServiceTest {

    @MockitoBean
    private TrainerWorkloadClient trainerWorkloadClient;

    @Autowired
    private TrainingService trainingService;

    @Autowired
    private TrainingRepository TrainingRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldCreateTraining() {

        TrainingType trainingType =
                new TrainingType("FitnessTrainingService");

        entityManager.persist(trainingType);

        Trainer trainer =
                new Trainer(
                        "John",
                        "Smith",
                        trainingType
                );

        trainer.setUserName("John.Smith");
        trainer.setPassword("password123");
        trainer.setIsActive(true);

        entityManager.persist(trainer);

        Trainee trainee =
                new Trainee(
                        "Anna",
                        "Brown",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        trainee.setUserName("Anna.Brown");
        trainee.setPassword("password123");
        trainee.setIsActive(true);

        entityManager.persist(trainee);

        Training training =
                new Training(
                        "Morning Cardio",
                        trainingType,
                        LocalDate.of(2026, 5, 11),
                        60,
                        trainer,
                        trainee
                );

        trainingService.createTraining(training);

        Training savedTraining =
                TrainingRepository.getTrainingById(training.getTrainingId());

        assertNotNull(savedTraining);
        assertEquals("Morning Cardio", savedTraining.getTrainingName());
        assertEquals(60, savedTraining.getTrainingDurationMinutes());
        assertEquals(trainer.getId(), savedTraining.getTrainer().getId());
        assertEquals(trainee.getId(), savedTraining.getTrainee().getId());
    }

    @Test
    void shouldThrowExceptionWhenTrainingNameIsBlank() {

        Training training =
                new Training(
                        " ",
                        null,
                        LocalDate.of(2026, 5, 11),
                        60,
                        null,
                        null
                );

        assertThrows(
                ValidationException.class,
                () -> trainingService.createTraining(training)
        );
    }

    @Test
    void shouldThrowExceptionWhenTrainingDateIsNull() {

        Training training =
                new Training(
                        "Morning Cardio",
                        null,
                        null,
                        60,
                        null,
                        null
                );

        assertThrows(
                ValidationException.class,
                () -> trainingService.createTraining(training)
        );
    }

    @Test
    void shouldThrowExceptionWhenTrainingDurationIsNotPositive() {

        Training training =
                new Training(
                        "Morning Cardio",
                        null,
                        LocalDate.of(2026, 5, 11),
                        0,
                        null,
                        null
                );

        assertThrows(
                ValidationException.class,
                () -> trainingService.createTraining(training)
        );
    }


}