package org.epam.gym_crm_system1.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.epam.gym_crm_system1.model.Trainee;
import org.epam.gym_crm_system1.model.Trainer;
import org.epam.gym_crm_system1.model.Training;
import org.epam.gym_crm_system1.model.TrainingType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TrainingDaoTest {

    @Autowired
    private TrainingDao trainingDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldSaveTraining() {

        TrainingType trainingType =
                new TrainingType("FitnessTrainingDao");

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

        trainingDao.saveTraining(training);

        Training savedTraining =
                trainingDao.getTrainingById(training.getTrainingId());

        assertNotNull(savedTraining);
        assertEquals("Morning Cardio", savedTraining.getTrainingName());
        assertEquals(60, savedTraining.getTrainingDurationMinutes());
        assertEquals(trainer.getId(), savedTraining.getTrainer().getId());
        assertEquals(trainee.getId(), savedTraining.getTrainee().getId());
    }
}