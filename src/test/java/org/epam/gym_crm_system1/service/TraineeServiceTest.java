package org.epam.gym_crm_system1.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.epam.gym_crm_system1.repository.TraineeRepository;
import org.epam.gym_crm_system1.exception.EntityNotFoundException;
import org.epam.gym_crm_system1.exception.InvalidCredentialsException;
import org.epam.gym_crm_system1.exception.ProfileStatusException;
import org.epam.gym_crm_system1.exception.ValidationException;
import org.epam.gym_crm_system1.model.Trainee;
import org.epam.gym_crm_system1.model.Trainer;
import org.epam.gym_crm_system1.model.Training;
import org.epam.gym_crm_system1.model.TrainingType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TraineeServiceTest {

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private TraineeRepository traineeRepository;

    @PersistenceContext
    private EntityManager entityManager;

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
                traineeRepository.findTraineeById(trainee.getId());

        assertNotNull(savedTrainee);
        assertEquals("John.Smith", savedTrainee.getUserName());
        assertNotNull(savedTrainee.getPassword());
        assertEquals(10, savedTrainee.getPassword().length());
        assertTrue(savedTrainee.getIsActive());
    }

    @Test
    void shouldValidateTraineeCredentials() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        traineeService.createTrainee(trainee);

        boolean result =
                traineeService.isTraineeCredentialsValid(
                        trainee.getUserName(),
                        trainee.getPassword()
                );

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenTraineePasswordIsWrong() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        traineeService.createTrainee(trainee);

        boolean result =
                traineeService.isTraineeCredentialsValid(
                        trainee.getUserName(),
                        "wrongPassword"
                );

        assertFalse(result);
    }

    @Test
    void shouldChangeTraineePassword() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        traineeService.createTrainee(trainee);

        String oldPassword = trainee.getPassword();

        traineeService.changeTraineePassword(
                trainee.getUserName(),
                oldPassword,
                "newPassword123"
        );

        Trainee updatedTrainee =
                traineeRepository.findTraineeById(trainee.getId());

        assertEquals("newPassword123",
                updatedTrainee.getPassword());
    }

    @Test
    void shouldThrowExceptionWhenOldTraineePasswordIsWrong() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        traineeService.createTrainee(trainee);

        assertThrows(
                InvalidCredentialsException.class,
                () -> traineeService.changeTraineePassword(
                        trainee.getUserName(),
                        "wrongPassword",
                        "newPassword123"
                )
        );
    }


    @Test
    void shouldDeactivateTrainee() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        traineeService.createTrainee(trainee);

        traineeService.deactivateTrainee(trainee.getUserName());

        Trainee updatedTrainee =
                traineeRepository.findTraineeById(trainee.getId());

        assertFalse(updatedTrainee.getIsActive());
    }

    @Test
    void shouldActivateTrainee() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        traineeService.createTrainee(trainee);

        traineeService.deactivateTrainee(trainee.getUserName());
        traineeService.activateTrainee(trainee.getUserName());

        Trainee updatedTrainee =
                traineeRepository.findTraineeById(trainee.getId());

        assertTrue(updatedTrainee.getIsActive());
    }

    @Test
    void shouldThrowExceptionWhenTraineeAlreadyActive() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        traineeService.createTrainee(trainee);

        assertThrows(
                ProfileStatusException.class,
                () -> traineeService.activateTrainee(trainee.getUserName())
        );
    }

    @Test
    void shouldThrowExceptionWhenTraineeAlreadyInactive() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        traineeService.createTrainee(trainee);

        traineeService.deactivateTrainee(trainee.getUserName());

        assertThrows(
                ProfileStatusException.class,
                () -> traineeService.deactivateTrainee(trainee.getUserName())
        );
    }

    @Test
    void shouldDeleteTraineeByUsername() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        traineeService.createTrainee(trainee);

        String username = trainee.getUserName();
        int traineeId = trainee.getId();

        traineeService.deleteTraineeByUsername(username);

        assertNull(
                traineeRepository.findTraineeById(traineeId)
        );
    }

    @Test
    void shouldThrowExceptionWhenDeletingUnknownTraineeUsername() {

        assertThrows(
                EntityNotFoundException.class,
                () -> traineeService.deleteTraineeByUsername("Unknown.User")
        );
    }

    @Test
    void shouldThrowExceptionWhenDeletingTraineeUsernameIsBlank() {

        assertThrows(
                ValidationException.class,
                () -> traineeService.deleteTraineeByUsername(" ")
        );
    }

    @Test
    void shouldSelectTraineeByUsername() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        traineeService.createTrainee(trainee);

        Trainee foundTrainee =
                traineeService.selectTraineeByUsername(
                        trainee.getUserName()
                );

        assertNotNull(foundTrainee);
        assertEquals(trainee.getUserName(),
                foundTrainee.getUserName());
        assertEquals("John",
                foundTrainee.getFirstName());
    }

    @Test
    void shouldThrowExceptionWhenTraineeUsernameNotFound() {

        assertThrows(
                EntityNotFoundException.class,
                () -> traineeService.selectTraineeByUsername("Unknown.User")
        );
    }

    @Test
    void shouldGetTraineeTrainingsByCriteria() {

        TrainingType fitness =
                new TrainingType("FitnessTraineeCriteria");

        TrainingType yoga =
                new TrainingType("YogaTraineeCriteria");

        entityManager.persist(fitness);
        entityManager.persist(yoga);

        Trainer trainer1 =
                new Trainer(
                        "Alex",
                        "Stone",
                        fitness
                );

        trainer1.setUserName("Alex.Stone");
        trainer1.setPassword("password123");
        trainer1.setIsActive(true);

        entityManager.persist(trainer1);

        Trainer trainer2 =
                new Trainer(
                        "Bob",
                        "Green",
                        yoga
                );

        trainer2.setUserName("Bob.Green");
        trainer2.setPassword("password123");
        trainer2.setIsActive(true);

        entityManager.persist(trainer2);

        Trainee trainee =
                new Trainee(
                        "Anna",
                        "Brown",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        traineeService.createTrainee(trainee);

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

        Collection<Training> trainings =
                traineeService.getTraineeTrainingsByCriteria(
                        trainee.getUserName(),
                        LocalDate.of(2026, 5, 1),
                        LocalDate.of(2026, 5, 15),
                        "Alex.Stone",
                        "FitnessTraineeCriteria"
                );

        assertEquals(1, trainings.size());

        Training foundTraining =
                trainings.iterator().next();

        assertEquals("Morning Cardio",
                foundTraining.getTrainingName());
    }

    @Test
    void shouldThrowExceptionWhenTraineeTrainingFromDateIsAfterToDate() {

        assertThrows(
                ValidationException.class,
                () -> traineeService.getTraineeTrainingsByCriteria(
                        "Anna.Brown",
                        LocalDate.of(2026, 6, 1),
                        LocalDate.of(2026, 5, 1),
                        null,
                        null
                )
        );
    }


    @Test
    void shouldGetTrainersNotAssignedToTrainee() {

        TrainingType fitness =
                new TrainingType("FitnessNotAssigned");

        TrainingType yoga =
                new TrainingType("YogaNotAssigned");

        entityManager.persist(fitness);
        entityManager.persist(yoga);

        Trainer assignedTrainer =
                new Trainer(
                        "Alex",
                        "Stone",
                        fitness
                );

        assignedTrainer.setUserName("Alex.Stone");
        assignedTrainer.setPassword("password123");
        assignedTrainer.setIsActive(true);

        entityManager.persist(assignedTrainer);

        Trainer notAssignedTrainer =
                new Trainer(
                        "Bob",
                        "Green",
                        yoga
                );

        notAssignedTrainer.setUserName("Bob.Green");
        notAssignedTrainer.setPassword("password123");
        notAssignedTrainer.setIsActive(true);

        entityManager.persist(notAssignedTrainer);

        Trainee trainee =
                new Trainee(
                        "Anna",
                        "Brown",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        traineeService.createTrainee(trainee);

        trainee.getTrainers().add(assignedTrainer);
        assignedTrainer.getTrainees().add(trainee);

        entityManager.merge(trainee);
        entityManager.merge(assignedTrainer);

        Collection<Trainer> trainers =
                traineeService.getTrainersNotAssignedToTrainee(
                        trainee.getUserName()
                );

        assertFalse(trainers.contains(assignedTrainer));
        assertTrue(trainers.contains(notAssignedTrainer));
    }


    @Test
    void shouldThrowExceptionWhenGettingNotAssignedTrainersForUnknownTrainee() {

        assertThrows(
                EntityNotFoundException.class,
                () -> traineeService.getTrainersNotAssignedToTrainee("Unknown.User")
        );
    }

    @Test
    void shouldUpdateTraineeTrainersList() {

        TrainingType fitness =
                new TrainingType("FitnessUpdateList");

        TrainingType yoga =
                new TrainingType("YogaUpdateList");

        TrainingType boxing =
                new TrainingType("BoxingUpdateList");

        entityManager.persist(fitness);
        entityManager.persist(yoga);
        entityManager.persist(boxing);

        Trainer oldTrainer =
                new Trainer("Alex", "Stone", fitness);

        oldTrainer.setUserName("Alex.Stone");
        oldTrainer.setPassword("password123");
        oldTrainer.setIsActive(true);

        entityManager.persist(oldTrainer);

        Trainer newTrainer1 =
                new Trainer("Bob", "Green", yoga);

        newTrainer1.setUserName("Bob.Green");
        newTrainer1.setPassword("password123");
        newTrainer1.setIsActive(true);

        entityManager.persist(newTrainer1);

        Trainer newTrainer2 =
                new Trainer("Mike", "Black", boxing);

        newTrainer2.setUserName("Mike.Black");
        newTrainer2.setPassword("password123");
        newTrainer2.setIsActive(true);

        entityManager.persist(newTrainer2);

        Trainee trainee =
                new Trainee(
                        "Anna",
                        "Brown",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        traineeService.createTrainee(trainee);

        trainee.getTrainers().add(oldTrainer);
        oldTrainer.getTrainees().add(trainee);

        entityManager.merge(oldTrainer);
        entityManager.flush();

        traineeService.updateTraineeTrainersList(
                trainee.getUserName(),
                List.of("Bob.Green", "Mike.Black")
        );

        entityManager.flush();
        entityManager.clear();

        Trainee updatedTrainee =
                traineeService.selectTraineeByUsername(trainee.getUserName());

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
    void shouldThrowExceptionWhenUpdatingTraineeTrainersListWithUnknownTrainer() {

        Trainee trainee =
                new Trainee(
                        "Anna",
                        "Brown",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        traineeService.createTrainee(trainee);

        assertThrows(
                EntityNotFoundException.class,
                () -> traineeService.updateTraineeTrainersList(
                        trainee.getUserName(),
                        List.of("Unknown.Trainer")
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenCreatingTraineeWithInvalidFirstName() {

        Trainee trainee =
                new Trainee(
                        "A",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        assertThrows(
                ValidationException.class,
                () -> traineeService.createTrainee(trainee)
        );
    }

    @Test
    void shouldThrowExceptionWhenUpdatingTraineeWithInvalidPassword() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        traineeService.createTrainee(trainee);

        trainee.setPassword(" ");

        assertThrows(
                ValidationException.class,
                () -> traineeService.updateTrainee(trainee)
        );
    }

    
    

}