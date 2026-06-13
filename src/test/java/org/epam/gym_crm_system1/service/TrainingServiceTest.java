package org.epam.gym_crm_system1.service;

import org.epam.gym_crm_system1.dao.TrainingDao;
import org.epam.gym_crm_system1.model.Trainee;
import org.epam.gym_crm_system1.model.Trainer;
import org.epam.gym_crm_system1.model.Training;
import org.epam.gym_crm_system1.model.TrainingType;
import org.epam.gym_crm_system1.storage.Storage;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TrainingServiceTest {

    @Test
    void shouldCreateTraining() {
        Storage storage = new Storage();

        TrainingDao trainingDao =
                new TrainingDao(storage);

        TrainingService trainingService =
                new TrainingService(trainingDao);

        TrainingType trainingType =
                new TrainingType(1, "Fitness");

        Trainer trainer =
                new Trainer(
                        "John",
                        "Smith",
                        trainingType
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
                        trainingType,
                        LocalDate.of(2026, 5, 11),
                        60,
                        trainer,
                        trainee
                );

        training.setTrainingId(1);

        trainingService.createTraining(training);

        Training savedTraining =
                trainingDao.getTrainingById(1);

        assertNotNull(savedTraining);
        assertEquals("Morning Cardio", savedTraining.getTrainingName());
        assertEquals(60, savedTraining.getTrainingDurationMinutes());
        assertEquals(1, savedTraining.getTrainer().getId());
        assertEquals(1, savedTraining.getTrainee().getId());
    }
}