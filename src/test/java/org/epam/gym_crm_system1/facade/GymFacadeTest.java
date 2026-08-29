package org.epam.gym_crm_system1.facade;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.epam.gym_crm_system1.exception.InvalidCredentialsException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class GymFacadeTest {

    private static final String PASSWORD = "password123";

    @Autowired
    private GymFacade facade;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

        trainee.setPassword(passwordEncoder.encode(PASSWORD));

        entityManager.merge(trainee);

        entityManager.flush();

        Trainee foundTrainee =
                facade.selectTraineeByUsername(
                        trainee.getUserName(),
                        PASSWORD
                );

        assertNotNull(foundTrainee);

        assertEquals(
                trainee.getUserName(),
                foundTrainee.getUserName()
        );
    }

    @Test
    void shouldCreateTrainerThroughFacade() {

        TrainingType fitness =
                new TrainingType("FitnessCreateTrainerFacade");

        entityManager.persist(fitness);

        Trainer trainer =
                new Trainer(
                        "John",
                        "Trainer",
                        fitness
                );

        facade.createTrainer(trainer);

        trainer.setPassword(passwordEncoder.encode(PASSWORD));

        entityManager.merge(trainer);

        entityManager.flush();

        Trainer foundTrainer =
                facade.selectTrainerByUsername(
                        trainer.getUserName(),
                        PASSWORD
                );

        assertNotNull(foundTrainer);

        assertEquals(
                trainer.getUserName(),
                foundTrainer.getUserName()
        );
    }

    @Test
    void shouldCheckTraineeCredentialsThroughFacade() {

        Trainee trainee =
                new Trainee(
                        "Credentials",
                        "Trainee",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        facade.createTrainee(trainee);

        trainee.setPassword(passwordEncoder.encode(PASSWORD));

        entityManager.merge(trainee);

        entityManager.flush();

        assertTrue(
                facade.isTraineeCredentialsValid(
                        trainee.getUserName(),
                        PASSWORD
                )
        );

        assertFalse(
                facade.isTraineeCredentialsValid(
                        trainee.getUserName(),
                        "wrongPassword"
                )
        );
    }

    @Test
    void shouldCheckTrainerCredentialsThroughFacade() {

        TrainingType fitness =
                new TrainingType("FitnessCredentialsTrainerFacade");

        entityManager.persist(fitness);

        Trainer trainer =
                new Trainer(
                        "Credentials",
                        "Trainer",
                        fitness
                );

        facade.createTrainer(trainer);

        trainer.setPassword(passwordEncoder.encode(PASSWORD));

        entityManager.merge(trainer);

        entityManager.flush();

        assertTrue(
                facade.isTrainerCredentialsValid(
                        trainer.getUserName(),
                        PASSWORD
                )
        );

        assertFalse(
                facade.isTrainerCredentialsValid(
                        trainer.getUserName(),
                        "wrongPassword"
                )
        );
    }

    @Test
    void shouldChangeTraineePasswordThroughFacade() {

        Trainee trainee =
                new Trainee(
                        "Change",
                        "TraineePassword",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        facade.createTrainee(trainee);

        trainee.setPassword(passwordEncoder.encode(PASSWORD));

        entityManager.merge(trainee);

        entityManager.flush();

        facade.changeTraineePassword(
                trainee.getUserName(),
                PASSWORD,
                "newPassword123"
        );

        assertTrue(
                facade.isTraineeCredentialsValid(
                        trainee.getUserName(),
                        "newPassword123"
                )
        );

        assertFalse(
                facade.isTraineeCredentialsValid(
                        trainee.getUserName(),
                        "wrongPassword"
                )
        );
    }

    @Test
    void shouldChangeTrainerPasswordThroughFacade() {

        TrainingType fitness =
                new TrainingType("FitnessChangeTrainerPasswordFacade");

        entityManager.persist(fitness);

        Trainer trainer =
                new Trainer(
                        "Change",
                        "TrainerPassword",
                        fitness
                );

        facade.createTrainer(trainer);

        trainer.setPassword(passwordEncoder.encode(PASSWORD));

        entityManager.merge(trainer);

        entityManager.flush();

        facade.changeTrainerPassword(
                trainer.getUserName(),
                PASSWORD,
                "newPassword123"
        );

        assertTrue(
                facade.isTrainerCredentialsValid(
                        trainer.getUserName(),
                        "newPassword123"
                )
        );

        assertFalse(
                facade.isTrainerCredentialsValid(
                        trainer.getUserName(),
                        "wrongPassword"
                )
        );
    }

    @Test
    void shouldUseFacadeForTrainingFiltersAndTrainerListUpdate() {

        TrainingType fitness =
                new TrainingType("FitnessFacade");

        TrainingType yoga =
                new TrainingType("YogaFacade");

        TrainingType boxing =
                new TrainingType("BoxingFacade");

        entityManager.persist(fitness);
        entityManager.persist(yoga);
        entityManager.persist(boxing);

        Trainer trainer1 =
                new Trainer("Alex", "Stone", fitness);

        trainer1.setUserName("Alex.Stone");
        trainer1.setPassword(passwordEncoder.encode(PASSWORD));
        trainer1.setIsActive(true);

        entityManager.persist(trainer1);

        Trainer trainer2 =
                new Trainer("Bob", "Green", yoga);

        trainer2.setUserName("Bob.Green");
        trainer2.setPassword(passwordEncoder.encode(PASSWORD));
        trainer2.setIsActive(true);

        entityManager.persist(trainer2);

        Trainer trainer3 =
                new Trainer("Mike", "Black", boxing);

        trainer3.setUserName("Mike.Black");
        trainer3.setPassword(passwordEncoder.encode(PASSWORD));
        trainer3.setIsActive(true);

        entityManager.persist(trainer3);

        Trainee trainee =
                new Trainee(
                        "Anna",
                        "Facade",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        trainee.setUserName("Anna.Facade");
        trainee.setPassword(passwordEncoder.encode(PASSWORD));
        trainee.setIsActive(true);

        entityManager.persist(trainee);

        trainee.getTrainers().add(trainer1);
        trainer1.getTrainees().add(trainee);

        Training training1 =
                new Training(
                        "Morning Cardio",
                        fitness,
                        LocalDate.of(2026, 5, 10),
                        60,
                        trainer1,
                        trainee
                );

        Training training2 =
                new Training(
                        "Evening Yoga",
                        yoga,
                        LocalDate.of(2026, 5, 20),
                        45,
                        trainer2,
                        trainee
                );

        entityManager.persist(training1);
        entityManager.persist(training2);

        entityManager.flush();

        Collection<Training> traineeTrainings =
                facade.getTraineeTrainingsByCriteria(
                        "Anna.Facade",
                        "password123",
                        LocalDate.of(2026, 5, 1),
                        LocalDate.of(2026, 5, 15),
                        "Alex.Stone",
                        "FitnessFacade"
                );

        assertEquals(1, traineeTrainings.size());

        assertEquals(
                "Morning Cardio",
                traineeTrainings.iterator().next().getTrainingName()
        );

        Collection<Training> trainerTrainings =
                facade.getTrainerTrainingsByCriteria(
                        "Alex.Stone",
                        "password123",
                        LocalDate.of(2026, 5, 1),
                        LocalDate.of(2026, 5, 15),
                        "Anna"
                );

        assertEquals(1, trainerTrainings.size());

        assertEquals(
                "Morning Cardio",
                trainerTrainings.iterator().next().getTrainingName()
        );

        Collection<Trainer> notAssignedTrainers =
                facade.getTrainersNotAssignedToTrainee(
                        "Anna.Facade",
                        "password123"
                );

        assertFalse(notAssignedTrainers.contains(trainer1));
        assertTrue(notAssignedTrainers.contains(trainer2));
        assertTrue(notAssignedTrainers.contains(trainer3));

        facade.updateTraineeTrainersList(
                "Anna.Facade",
                "password123",
                List.of("Bob.Green", "Mike.Black")
        );

        entityManager.flush();
        entityManager.clear();

        Trainee updatedTrainee =
                entityManager.find(Trainee.class, trainee.getId());

        assertEquals(2, updatedTrainee.getTrainers().size());

        assertTrue(
                updatedTrainee.getTrainers()
                        .stream()
                        .anyMatch(trainer ->
                                trainer.getUserName().equals("Bob.Green"))
        );

        assertTrue(
                updatedTrainee.getTrainers()
                        .stream()
                        .anyMatch(trainer ->
                                trainer.getUserName().equals("Mike.Black"))
        );

        assertFalse(
                updatedTrainee.getTrainers()
                        .stream()
                        .anyMatch(trainer ->
                                trainer.getUserName().equals("Alex.Stone"))
        );
    }

    @Test
    void shouldSelectTraineeByUsernameWithCorrectPasswordThroughFacade() {

        Trainee trainee =
                new Trainee(
                        "Auth",
                        "Trainee",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        facade.createTrainee(trainee);

        trainee.setPassword(passwordEncoder.encode(PASSWORD));

        entityManager.merge(trainee);

        entityManager.flush();

        Trainee foundTrainee =
                facade.selectTraineeByUsername(
                        trainee.getUserName(),
                        PASSWORD
                );

        assertNotNull(foundTrainee);

        assertEquals(
                trainee.getUserName(),
                foundTrainee.getUserName()
        );
    }

    @Test
    void shouldThrowExceptionWhenTraineePasswordIsWrongThroughFacade() {

        Trainee trainee =
                new Trainee(
                        "Wrong",
                        "Password",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        facade.createTrainee(trainee);

        trainee.setPassword(passwordEncoder.encode(PASSWORD));

        entityManager.merge(trainee);

        entityManager.flush();

        assertThrows(
                InvalidCredentialsException.class,
                () -> facade.selectTraineeByUsername(
                        trainee.getUserName(),
                        "wrongPassword"
                )
        );
    }

    @Test
    void shouldSelectTrainerByUsernameWithCorrectPasswordThroughFacade() {

        TrainingType fitness =
                new TrainingType("FitnessAuthFacade");

        entityManager.persist(fitness);

        Trainer trainer =
                new Trainer(
                        "Auth",
                        "Trainer",
                        fitness
                );

        facade.createTrainer(trainer);

        trainer.setPassword(passwordEncoder.encode(PASSWORD));

        entityManager.merge(trainer);

        entityManager.flush();

        Trainer foundTrainer =
                facade.selectTrainerByUsername(
                        trainer.getUserName(),
                        PASSWORD
                );

        assertNotNull(foundTrainer);

        assertEquals(
                trainer.getUserName(),
                foundTrainer.getUserName()
        );
    }

    @Test
    void shouldThrowExceptionWhenTrainerPasswordIsWrongThroughFacade() {

        TrainingType fitness =
                new TrainingType("FitnessWrongPasswordFacade");

        entityManager.persist(fitness);

        Trainer trainer =
                new Trainer(
                        "Wrong",
                        "Trainer",
                        fitness
                );

        facade.createTrainer(trainer);

        trainer.setPassword(passwordEncoder.encode(PASSWORD));

        entityManager.merge(trainer);

        entityManager.flush();

        assertThrows(
                InvalidCredentialsException.class,
                () -> facade.selectTrainerByUsername(
                        trainer.getUserName(),
                        "wrongPassword"
                )
        );
    }

    @Test
    void shouldGetTrainerTrainingsByCriteriaWithAuthenticationThroughFacade() {

        TrainingType fitness =
                new TrainingType("FitnessTrainerAuthCriteria");

        entityManager.persist(fitness);

        Trainer trainer =
                new Trainer(
                        "Auth",
                        "Trainer",
                        fitness
                );

        facade.createTrainer(trainer);

        trainer.setPassword(passwordEncoder.encode(PASSWORD));

        entityManager.merge(trainer);

        entityManager.flush();

        Trainee trainee =
                new Trainee(
                        "Anna",
                        "Brown",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        facade.createTrainee(trainee);

        trainee.setPassword(passwordEncoder.encode(PASSWORD));

        entityManager.merge(trainee);

        entityManager.flush();

        Training training =
                new Training(
                        "Authenticated Cardio",
                        fitness,
                        LocalDate.of(2026, 5, 10),
                        60,
                        trainer,
                        trainee
                );

        entityManager.persist(training);
        entityManager.flush();

        Collection<Training> trainings =
                facade.getTrainerTrainingsByCriteria(
                        trainer.getUserName(),
                        PASSWORD,
                        LocalDate.of(2026, 5, 1),
                        LocalDate.of(2026, 5, 15),
                        "Anna"
                );

        assertEquals(1, trainings.size());

        assertEquals(
                "Authenticated Cardio",
                trainings.iterator().next().getTrainingName()
        );
    }

    @Test
    void shouldThrowExceptionWhenGettingTrainerTrainingsWithWrongPasswordThroughFacade() {

        TrainingType fitness =
                new TrainingType("FitnessTrainerWrongAuth");

        entityManager.persist(fitness);

        Trainer trainer =
                new Trainer(
                        "Wrong",
                        "Auth",
                        fitness
                );

        facade.createTrainer(trainer);

        trainer.setPassword(passwordEncoder.encode(PASSWORD));

        entityManager.merge(trainer);

        entityManager.flush();

        assertThrows(
                InvalidCredentialsException.class,
                () -> facade.getTrainerTrainingsByCriteria(
                        trainer.getUserName(),
                        "wrongPassword",
                        null,
                        null,
                        null
                )
        );
    }

    @Test
    void shouldCreateTrainingWithTrainerAuthenticationThroughFacade() {

        TrainingType fitness =
                new TrainingType("FitnessCreateTrainingAuth");

        entityManager.persist(fitness);

        Trainer trainer =
                new Trainer(
                        "Create",
                        "Trainer",
                        fitness
                );

        facade.createTrainer(trainer);

        trainer.setPassword(passwordEncoder.encode(PASSWORD));

        entityManager.merge(trainer);

        entityManager.flush();

        Trainee trainee =
                new Trainee(
                        "Create",
                        "Trainee",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        facade.createTrainee(trainee);

        trainee.setPassword(passwordEncoder.encode(PASSWORD));

        entityManager.merge(trainee);

        entityManager.flush();

        Training training =
                new Training(
                        "Training Created With Auth",
                        fitness,
                        LocalDate.of(2026, 6, 1),
                        50,
                        trainer,
                        trainee
                );

        facade.createTraining(
                training,
                trainer.getUserName(),
                PASSWORD
        );

        entityManager.flush();
        entityManager.clear();

        Training savedTraining =
                entityManager.find(
                        Training.class,
                        training.getTrainingId()
                );

        assertNotNull(savedTraining);

        assertEquals(
                "Training Created With Auth",
                savedTraining.getTrainingName()
        );
    }

    @Test
    void shouldThrowExceptionWhenTrainerCreatesTrainingForAnotherTrainer() {

        TrainingType fitness =
                new TrainingType("FitnessWrongTrainerAuth");

        entityManager.persist(fitness);

        Trainer trainer1 =
                new Trainer(
                        "Real",
                        "Trainer",
                        fitness
                );

        facade.createTrainer(trainer1);

        trainer1.setPassword(passwordEncoder.encode(PASSWORD));

        entityManager.merge(trainer1);

        entityManager.flush();

        Trainer trainer2 =
                new Trainer(
                        "Other",
                        "Trainer",
                        fitness
                );

        facade.createTrainer(trainer2);

        trainer2.setPassword(passwordEncoder.encode(PASSWORD));

        entityManager.merge(trainer2);

        entityManager.flush();

        Trainee trainee =
                new Trainee(
                        "Wrong",
                        "Trainee",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        facade.createTrainee(trainee);

        trainee.setPassword(passwordEncoder.encode(PASSWORD));

        entityManager.merge(trainee);

        entityManager.flush();

        Training training =
                new Training(
                        "Wrong Trainer Training",
                        fitness,
                        LocalDate.of(2026, 6, 1),
                        50,
                        trainer2,
                        trainee
                );

        assertThrows(
                InvalidCredentialsException.class,
                () -> facade.createTraining(
                        training,
                        trainer1.getUserName(),
                        PASSWORD
                )
        );
    }

    @Test
    void shouldGetTraineeTrainingsByCriteriaWithAuthenticationThroughFacade() {

        TrainingType fitness =
                new TrainingType("FitnessTraineeAuthCriteria");

        entityManager.persist(fitness);

        Trainer trainer =
                new Trainer(
                        "Trainee",
                        "AuthTrainer",
                        fitness
                );

        facade.createTrainer(trainer);

        trainer.setPassword(passwordEncoder.encode(PASSWORD));

        entityManager.merge(trainer);

        entityManager.flush();

        Trainee trainee =
                new Trainee(
                        "Trainee",
                        "Auth",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        facade.createTrainee(trainee);

        trainee.setPassword(passwordEncoder.encode(PASSWORD));

        entityManager.merge(trainee);

        entityManager.flush();

        Training training =
                new Training(
                        "Trainee Auth Cardio",
                        fitness,
                        LocalDate.of(2026, 5, 10),
                        60,
                        trainer,
                        trainee
                );

        entityManager.persist(training);
        entityManager.flush();

        Collection<Training> trainings =
                facade.getTraineeTrainingsByCriteria(
                        trainee.getUserName(),
                        PASSWORD,
                        LocalDate.of(2026, 5, 1),
                        LocalDate.of(2026, 5, 15),
                        trainer.getUserName(),
                        "FitnessTraineeAuthCriteria"
                );

        assertEquals(1, trainings.size());

        assertEquals(
                "Trainee Auth Cardio",
                trainings.iterator().next().getTrainingName()
        );
    }

    @Test
    void shouldThrowExceptionWhenGettingTraineeTrainingsWithWrongPasswordThroughFacade() {

        Trainee trainee =
                new Trainee(
                        "Wrong",
                        "TraineeAuth",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        facade.createTrainee(trainee);

        trainee.setPassword(passwordEncoder.encode(PASSWORD));

        entityManager.merge(trainee);

        entityManager.flush();

        assertThrows(
                InvalidCredentialsException.class,
                () -> facade.getTraineeTrainingsByCriteria(
                        trainee.getUserName(),
                        "wrongPassword",
                        null,
                        null,
                        null,
                        null
                )
        );
    }

    @Test
    void shouldUpdateTraineeTrainersListWithAuthenticationThroughFacade() {

        TrainingType fitness =
                new TrainingType("FitnessTraineeListAuth");

        TrainingType yoga =
                new TrainingType("YogaTraineeListAuth");

        entityManager.persist(fitness);
        entityManager.persist(yoga);

        Trainer trainer1 =
                new Trainer("List", "TrainerOne", fitness);

        facade.createTrainer(trainer1);

        trainer1.setPassword(passwordEncoder.encode(PASSWORD));

        entityManager.merge(trainer1);

        entityManager.flush();

        Trainer trainer2 =
                new Trainer("List", "TrainerTwo", yoga);

        facade.createTrainer(trainer2);

        trainer2.setPassword(passwordEncoder.encode(PASSWORD));

        entityManager.merge(trainer2);

        entityManager.flush();

        Trainee trainee =
                new Trainee(
                        "List",
                        "TraineeAuth",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        facade.createTrainee(trainee);

        trainee.setPassword(passwordEncoder.encode(PASSWORD));

        entityManager.merge(trainee);

        entityManager.flush();

        facade.updateTraineeTrainersList(
                trainee.getUserName(),
                PASSWORD,
                List.of(
                        trainer1.getUserName(),
                        trainer2.getUserName()
                )
        );

        entityManager.flush();
        entityManager.clear();

        Trainee updatedTrainee =
                entityManager.find(Trainee.class, trainee.getId());

        assertEquals(2, updatedTrainee.getTrainers().size());
    }


}
