package org.epam.gym_crm_system1.dao;

import org.epam.gym_crm_system1.model.Specialization;
import org.epam.gym_crm_system1.model.Training;
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

        Training training =
                new Training(
                        "Morning Cardio",
                        new Specialization(1, "Fitness"),
                        LocalDate.of(2026,5,11),
                        60,
                        1,
                        1,
                        1
                );

        trainingDao.saveTraining(training);

        assertEquals(training,
                trainingDao.getTrainingById(1));
    }
}