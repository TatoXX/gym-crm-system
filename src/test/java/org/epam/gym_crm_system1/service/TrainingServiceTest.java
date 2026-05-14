package org.epam.gym_crm_system1.service;

import org.epam.gym_crm_system1.dao.TrainingDao;
import org.epam.gym_crm_system1.model.Specialization;
import org.epam.gym_crm_system1.model.Training;
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

        Training training =
                new Training(
                        "Morning Cardio",
                        new Specialization(1, "Fitness"),
                        LocalDate.of(2026, 5, 11),
                        60,
                        1,
                        1,
                        1
                );

        trainingService.createTraining(training);

        Training savedTraining =
                trainingDao.getTrainingById(1);

        assertNotNull(savedTraining);
        assertEquals("Morning Cardio", savedTraining.getTrainingName());
        assertEquals(60, savedTraining.getTrainingDurationMinutes());
        assertEquals(1, savedTraining.getTrainerId());
        assertEquals(1, savedTraining.getTraineeId());
    }
}