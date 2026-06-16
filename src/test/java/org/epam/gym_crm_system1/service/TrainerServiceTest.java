package org.epam.gym_crm_system1.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.epam.gym_crm_system1.dao.TrainerDao;
import org.epam.gym_crm_system1.exception.EntityNotFoundException;
import org.epam.gym_crm_system1.exception.InvalidCredentialsException;
import org.epam.gym_crm_system1.exception.ProfileStatusException;
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

    @Test
    void shouldChangeTrainerPassword() {

        TrainingType trainingType =
                new TrainingType("FitnessPasswordChange");

        entityManager.persist(trainingType);

        Trainer trainer =
                new Trainer(
                        "John",
                        "Smith",
                        trainingType
                );

        trainerService.createTrainer(trainer);

        String oldPassword = trainer.getPassword();

        trainerService.changeTrainerPassword(
                trainer.getUserName(),
                oldPassword,
                "newPassword123"
        );

        Trainer updatedTrainer =
                trainerDao.getTrainerById(trainer.getId());

        assertEquals("newPassword123",
                updatedTrainer.getPassword());
    }

    @Test
    void shouldThrowExceptionWhenOldTrainerPasswordIsWrong() {

        TrainingType trainingType =
                new TrainingType("YogaPasswordChange");

        entityManager.persist(trainingType);

        Trainer trainer =
                new Trainer(
                        "John",
                        "Smith",
                        trainingType
                );

        trainerService.createTrainer(trainer);

        assertThrows(
                InvalidCredentialsException.class,
                () -> trainerService.changeTrainerPassword(
                        trainer.getUserName(),
                        "wrongPassword",
                        "newPassword123"
                )
        );
    }

    @Test
    void shouldDeactivateTrainer() {

        TrainingType trainingType =
                new TrainingType("FitnessDeactivateTrainer");

        entityManager.persist(trainingType);

        Trainer trainer =
                new Trainer(
                        "John",
                        "Smith",
                        trainingType
                );

        trainerService.createTrainer(trainer);

        trainerService.deactivateTrainer(trainer.getUserName());

        Trainer updatedTrainer =
                trainerDao.getTrainerById(trainer.getId());

        assertFalse(updatedTrainer.getIsActive());
    }

    @Test
    void shouldActivateTrainer() {

        TrainingType trainingType =
                new TrainingType("FitnessActivateTrainer");

        entityManager.persist(trainingType);

        Trainer trainer =
                new Trainer(
                        "John",
                        "Smith",
                        trainingType
                );

        trainerService.createTrainer(trainer);

        trainerService.deactivateTrainer(trainer.getUserName());
        trainerService.activateTrainer(trainer.getUserName());

        Trainer updatedTrainer =
                trainerDao.getTrainerById(trainer.getId());

        assertTrue(updatedTrainer.getIsActive());
    }

    @Test
    void shouldThrowExceptionWhenTrainerAlreadyActive() {

        TrainingType trainingType =
                new TrainingType("FitnessAlreadyActiveTrainer");

        entityManager.persist(trainingType);

        Trainer trainer =
                new Trainer(
                        "John",
                        "Smith",
                        trainingType
                );

        trainerService.createTrainer(trainer);

        assertThrows(
                ProfileStatusException.class,
                () -> trainerService.activateTrainer(trainer.getUserName())
        );
    }

    @Test
    void shouldThrowExceptionWhenTrainerAlreadyInactive() {

        TrainingType trainingType =
                new TrainingType("FitnessAlreadyInactiveTrainer");

        entityManager.persist(trainingType);

        Trainer trainer =
                new Trainer(
                        "John",
                        "Smith",
                        trainingType
                );

        trainerService.createTrainer(trainer);

        trainerService.deactivateTrainer(trainer.getUserName());

        assertThrows(
                ProfileStatusException.class,
                () -> trainerService.deactivateTrainer(trainer.getUserName())
        );
    }


    @Test
    void shouldSelectTrainerByUsername() {

        TrainingType trainingType =
                new TrainingType("FitnessSelectTrainer");

        entityManager.persist(trainingType);

        Trainer trainer =
                new Trainer(
                        "John",
                        "Smith",
                        trainingType
                );

        trainerService.createTrainer(trainer);

        Trainer foundTrainer =
                trainerService.selectTrainerByUsername(
                        trainer.getUserName()
                );

        assertNotNull(foundTrainer);
        assertEquals(trainer.getUserName(),
                foundTrainer.getUserName());
        assertEquals("John",
                foundTrainer.getFirstName());
    }

    @Test
    void shouldThrowExceptionWhenTrainerUsernameNotFound() {

        assertThrows(
                EntityNotFoundException.class,
                () -> trainerService.selectTrainerByUsername("Unknown.User")
        );
    }
}