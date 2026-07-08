package org.epam.gym_crm_system1.helper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.epam.gym_crm_system1.model.Trainee;
import org.epam.gym_crm_system1.model.Trainer;
import org.epam.gym_crm_system1.model.TrainingType;
import org.epam.gym_crm_system1.repository.TraineeRepository;
import org.epam.gym_crm_system1.repository.TrainerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class UserCredentialsGeneratorTest {

    @Autowired
    private UserCredentialsGenerator generator;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldGenerateUsernameWithNumberWhenTraineeUsernameExists() {

        Trainee existingTrainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        existingTrainee.setUserName("John.Smith");
        existingTrainee.setPassword("password123");
        existingTrainee.setIsActive(true);

        traineeRepository.saveTrainee(existingTrainee);

        Trainee newTrainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Batumi",
                        LocalDate.of(2001, 2, 2)
                );

        String username =
                generator.generateUsername(newTrainee.getUser());

        assertEquals("John.Smith1", username);
    }

    @Test
    void shouldGenerateUsernameWhenNoDuplicateExists() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        String username =
                generator.generateUsername(trainee.getUser());

        assertEquals("John.Smith", username);
    }

    @Test
    void shouldGenerateUsernameWithNextNumberWhenMultipleUsernamesExist() {

        Trainee trainee1 =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        trainee1.setUserName("John.Smith");
        trainee1.setPassword("password123");
        trainee1.setIsActive(true);

        traineeRepository.saveTrainee(trainee1);

        Trainee trainee2 =
                new Trainee(
                        "John",
                        "Smith",
                        "Batumi",
                        LocalDate.of(2001, 2, 2)
                );

        trainee2.setUserName("John.Smith1");
        trainee2.setPassword("password123");
        trainee2.setIsActive(true);

        traineeRepository.saveTrainee(trainee2);

        Trainee newTrainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Kutaisi",
                        LocalDate.of(2002, 3, 3)
                );

        String username =
                generator.generateUsername(newTrainee.getUser());

        assertEquals("John.Smith2", username);
    }

    @Test
    void shouldCheckTrainerUsernamesToo() {

        TrainingType trainingType =
                new TrainingType("FitnessGeneratorTest");

        entityManager.persist(trainingType);

        Trainer existingTrainer =
                new Trainer(
                        "John",
                        "Smith",
                        trainingType
                );

        existingTrainer.setUserName("John.Smith");
        existingTrainer.setPassword("password123");
        existingTrainer.setIsActive(true);

        trainerRepository.saveTrainer(existingTrainer);

        Trainee newTrainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        String username =
                generator.generateUsername(newTrainee.getUser());

        assertEquals("John.Smith1", username);
    }

    @Test
    void shouldGeneratePasswordWithLengthTen() {

        String password =
                generator.generatePassword();

        assertEquals(10, password.length());
    }
}