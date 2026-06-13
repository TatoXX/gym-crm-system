package org.epam.gym_crm_system1.dao;

import org.epam.gym_crm_system1.model.Trainee;
import org.epam.gym_crm_system1.model.Trainer;
import org.epam.gym_crm_system1.model.Training;
import org.epam.gym_crm_system1.model.TrainingType;
import org.epam.gym_crm_system1.storage.Storage;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TrainingDaoTest {

    @Test
    void shouldSaveTraining() {

        Storage storage = new Storage();

        TrainingDao trainingDao =
                new TrainingDao(storage);

        Trainer trainer =
                new Trainer(
                        "John",
                        "Smith",
                        new TrainingType(1, "Fitness")
                );

        trainer.setId(1);

        Trainee trainee =
                new Trainee(
                        "Anna",
                        "Brown",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        trainee.setId(1);

        Training training =
                new Training(
                        "Morning Cardio",
                        new TrainingType(1, "Fitness"),
                        LocalDate.of(2026, 5, 11),
                        60,
                        trainer,
                        trainee
                );

        training.setTrainingId(1);

        trainingDao.saveTraining(training);

        assertEquals(training,
                trainingDao.getTrainingById(1));
    }
}