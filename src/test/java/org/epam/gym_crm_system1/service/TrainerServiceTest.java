package org.epam.gym_crm_system1.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.epam.gym_crm_system1.dao.TrainerDao;
import org.epam.gym_crm_system1.model.TrainingType;
import org.epam.gym_crm_system1.model.Trainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TrainerServiceTest {

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private TrainerDao trainerDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldCreateTrainer() {

        TrainingType trainingType =
                new TrainingType("FitnessTrainerService");

        entityManager.persist(trainingType);

        Trainer trainer =
                new Trainer(
                        "John",
                        "Smith",
                        trainingType
                );

        trainerService.createTrainer(trainer);

        Trainer savedTrainer =
                trainerDao.getTrainerById(trainer.getId());

        assertNotNull(savedTrainer);
        assertEquals("John.Smith", savedTrainer.getUserName());
        assertNotNull(savedTrainer.getPassword());
        assertEquals(10, savedTrainer.getPassword().length());
        assertTrue(savedTrainer.getIsActive());
        assertEquals("FitnessTrainerService",
                savedTrainer.getTrainingType().getName());
    }

    @Test
    void shouldValidateTrainerCredentials() {

        TrainingType trainingType =
                new TrainingType("FitnessCredentialTest");

        entityManager.persist(trainingType);

        Trainer trainer =
                new Trainer(
                        "John",
                        "Smith",
                        trainingType
                );

        trainerService.createTrainer(trainer);

        boolean result =
                trainerService.isTrainerCredentialsValid(
                        trainer.getUserName(),
                        trainer.getPassword()
                );

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenTrainerPasswordIsWrong() {

        TrainingType trainingType =
                new TrainingType("YogaCredentialTest");

        entityManager.persist(trainingType);

        Trainer trainer =
                new Trainer(
                        "John",
                        "Smith",
                        trainingType
                );

        trainerService.createTrainer(trainer);

        boolean result =
                trainerService.isTrainerCredentialsValid(
                        trainer.getUserName(),
                        "wrongPassword"
                );

        assertFalse(result);
    }
}