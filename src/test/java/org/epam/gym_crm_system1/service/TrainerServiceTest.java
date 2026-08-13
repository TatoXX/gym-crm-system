package org.epam.gym_crm_system1.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.epam.gym_crm_system1.repository.TrainerRepository;
import org.epam.gym_crm_system1.model.Trainee;
import org.epam.gym_crm_system1.model.Training;
import org.epam.gym_crm_system1.model.TrainingType;
import org.epam.gym_crm_system1.model.Trainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.epam.gym_crm_system1.exception.EntityNotFoundException;
import org.epam.gym_crm_system1.exception.InvalidCredentialsException;
import org.epam.gym_crm_system1.exception.ProfileStatusException;
import org.epam.gym_crm_system1.exception.ValidationException;
import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TrainerServiceTest {

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private TrainerRepository trainerRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

        String plainPassword = trainerService.createTrainer(trainer);

        Trainer savedTrainer =
                trainerRepository.getTrainerById(trainer.getId());

        assertNotNull(savedTrainer);
        assertEquals("John.Smith", savedTrainer.getUserName());
        assertNotNull(savedTrainer.getPassword());
        assertEquals(10, plainPassword.length());
        assertTrue(passwordEncoder.matches(plainPassword, savedTrainer.getPassword()));
        assertNotEquals(plainPassword, savedTrainer.getPassword());
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

        String plainPassword = trainerService.createTrainer(trainer);

        boolean result =
                trainerService.isTrainerCredentialsValid(
                        trainer.getUserName(),
                        plainPassword
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

        String oldPassword = trainerService.createTrainer(trainer);

        trainerService.changeTrainerPassword(
                trainer.getUserName(),
                oldPassword,
                "newPassword123"
        );

        Trainer updatedTrainer =
                trainerRepository.getTrainerById(trainer.getId());

        assertTrue(passwordEncoder.matches(
                "newPassword123",
                updatedTrainer.getPassword()
        ));
        assertFalse(passwordEncoder.matches(
                oldPassword,
                updatedTrainer.getPassword()
        ));
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
                trainerRepository.getTrainerById(trainer.getId());

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
                trainerRepository.getTrainerById(trainer.getId());

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

    @Test
    void shouldGetTrainerTrainingsByCriteria() {

        TrainingType fitness =
                new TrainingType("FitnessTrainerCriteria");

        TrainingType yoga =
                new TrainingType("YogaTrainerCriteria");

        entityManager.persist(fitness);
        entityManager.persist(yoga);

        Trainer trainer =
                new Trainer(
                        "Alex",
                        "Stone",
                        fitness
                );

        trainerService.createTrainer(trainer);

        Trainee trainee1 =
                new Trainee(
                        "Anna",
                        "Brown",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        trainee1.setUserName("Anna.Brown");
        trainee1.setPassword("password123");
        trainee1.setIsActive(true);

        entityManager.persist(trainee1);

        Trainee trainee2 =
                new Trainee(
                        "Nino",
                        "Green",
                        "Batumi",
                        LocalDate.of(2001, 2, 2)
                );

        trainee2.setUserName("Nino.Green");
        trainee2.setPassword("password123");
        trainee2.setIsActive(true);

        entityManager.persist(trainee2);

        Training training1 =
                new Training(
                        "Morning Cardio",
                        fitness,
                        LocalDate.of(2026, 5, 10),
                        60,
                        trainer,
                        trainee1
                );

        Training training2 =
                new Training(
                        "Evening Yoga",
                        yoga,
                        LocalDate.of(2026, 5, 20),
                        45,
                        trainer,
                        trainee2
                );

        entityManager.persist(training1);
        entityManager.persist(training2);

        Collection<Training> trainings =
                trainerService.getTrainerTrainingsByCriteria(
                        trainer.getUserName(),
                        LocalDate.of(2026, 5, 1),
                        LocalDate.of(2026, 5, 15),
                        "Anna"
                );

        assertEquals(1, trainings.size());

        Training foundTraining =
                trainings.iterator().next();

        assertEquals("Morning Cardio",
                foundTraining.getTrainingName());

        assertEquals("Anna",
                foundTraining.getTrainee().getFirstName());
    }


    @Test
    void shouldThrowExceptionWhenTrainerTrainingFromDateIsAfterToDate() {

        assertThrows(
                ValidationException.class,
                () -> trainerService.getTrainerTrainingsByCriteria(
                        "Alex.Stone",
                        LocalDate.of(2026, 6, 1),
                        LocalDate.of(2026, 5, 1),
                        null
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenCreatingTrainerWithoutTrainingType() {

        Trainer trainer =
                new Trainer(
                        "John",
                        "Smith",
                        null
                );

        assertThrows(
                ValidationException.class,
                () -> trainerService.createTrainer(trainer)
        );
    }


    @Test
    void shouldThrowExceptionWhenUpdatingTrainerWithInvalidPassword() {

        TrainingType fitness =
                new TrainingType("FitnessTrainerValidation");

        entityManager.persist(fitness);

        Trainer trainer =
                new Trainer(
                        "John",
                        "Trainer",
                        fitness
                );

        trainerService.createTrainer(trainer);

        trainer.setPassword(" ");

        assertThrows(
                ValidationException.class,
                () -> trainerService.updateTrainer(trainer)
        );
    }

}