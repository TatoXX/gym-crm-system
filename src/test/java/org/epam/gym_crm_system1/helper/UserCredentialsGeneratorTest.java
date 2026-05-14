package org.epam.gym_crm_system1.helper;

import org.epam.gym_crm_system1.dao.TraineeDao;
import org.epam.gym_crm_system1.dao.TrainerDao;
import org.epam.gym_crm_system1.model.Trainee;
import org.epam.gym_crm_system1.model.Trainer;
import org.epam.gym_crm_system1.storage.Storage;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class UserCredentialsGeneratorTest {

    @Test
    void shouldGenerateUsernameWithNumberWhenTraineeUsernameExists() {
        Storage storage = new Storage();

        TraineeDao traineeDao = new TraineeDao(storage);
        TrainerDao trainerDao = new TrainerDao(storage);

        UserCredentialsGenerator generator =
                new UserCredentialsGenerator(traineeDao, trainerDao);

        Trainee existingTrainee =
                new Trainee("John", "Smith", "Tbilisi",
                        LocalDate.of(2000, 1, 1), 1);

        existingTrainee.setUserName("John.Smith");
        traineeDao.saveTrainee(existingTrainee);

        Trainee newTrainee =
                new Trainee("John", "Smith", "Batumi",
                        LocalDate.of(2001, 2, 2), 2);

        String username = generator.generateUsername(newTrainee);

        assertEquals("John.Smith1", username);
    }
    @Test
    void shouldGenerateUsernameWhenNoDuplicateExists() {
        Storage storage = new Storage();

        TraineeDao traineeDao = new TraineeDao(storage);
        TrainerDao trainerDao = new TrainerDao(storage);

        UserCredentialsGenerator generator =
                new UserCredentialsGenerator(traineeDao, trainerDao);

        Trainee trainee =
                new Trainee("John", "Smith", "Tbilisi",
                        LocalDate.of(2000, 1, 1), 1);

        String username = generator.generateUsername(trainee);

        assertEquals("John.Smith", username);
    }

    @Test
    void shouldGenerateUsernameWithNextNumberWhenMultipleUsernamesExist() {
        Storage storage = new Storage();

        TraineeDao traineeDao = new TraineeDao(storage);
        TrainerDao trainerDao = new TrainerDao(storage);

        UserCredentialsGenerator generator =
                new UserCredentialsGenerator(traineeDao, trainerDao);

        Trainee trainee1 =
                new Trainee("John", "Smith", "Tbilisi",
                        LocalDate.of(2000, 1, 1), 1);
        trainee1.setUserName("John.Smith");
        traineeDao.saveTrainee(trainee1);

        Trainee trainee2 =
                new Trainee("John", "Smith", "Batumi",
                        LocalDate.of(2001, 2, 2), 2);
        trainee2.setUserName("John.Smith1");
        traineeDao.saveTrainee(trainee2);

        Trainee newTrainee =
                new Trainee("John", "Smith", "Kutaisi",
                        LocalDate.of(2002, 3, 3), 3);

        String username = generator.generateUsername(newTrainee);

        assertEquals("John.Smith2", username);
    }

    @Test
    void shouldCheckTrainerUsernamesToo() {
        Storage storage = new Storage();

        TraineeDao traineeDao = new TraineeDao(storage);
        TrainerDao trainerDao = new TrainerDao(storage);

        UserCredentialsGenerator generator =
                new UserCredentialsGenerator(traineeDao, trainerDao);

        Trainer existingTrainer =
                new Trainer("John", "Smith", null, 1);

        existingTrainer.setUserName("John.Smith");
        trainerDao.saveTrainer(existingTrainer);

        Trainee newTrainee =
                new Trainee("John", "Smith", "Tbilisi",
                        LocalDate.of(2000, 1, 1), 2);

        String username = generator.generateUsername(newTrainee);

        assertEquals("John.Smith1", username);
    }

    @Test
    void shouldGeneratePasswordWithLengthTen() {
        Storage storage = new Storage();

        TraineeDao traineeDao = new TraineeDao(storage);
        TrainerDao trainerDao = new TrainerDao(storage);

        UserCredentialsGenerator generator =
                new UserCredentialsGenerator(traineeDao, trainerDao);

        String password = generator.generatePassword();

        assertEquals(10, password.length());
    }
}
