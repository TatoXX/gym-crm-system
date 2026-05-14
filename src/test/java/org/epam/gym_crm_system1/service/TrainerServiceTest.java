package org.epam.gym_crm_system1.service;

import org.epam.gym_crm_system1.dao.TraineeDao;
import org.epam.gym_crm_system1.dao.TrainerDao;
import org.epam.gym_crm_system1.helper.UserCredentialsGenerator;
import org.epam.gym_crm_system1.model.Specialization;
import org.epam.gym_crm_system1.model.Trainer;
import org.epam.gym_crm_system1.storage.Storage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrainerServiceTest {

    @Test
    void shouldCreateTrainer() {
        Storage storage = new Storage();

        TraineeDao traineeDao = new TraineeDao(storage);
        TrainerDao trainerDao = new TrainerDao(storage);

        UserCredentialsGenerator generator =
                new UserCredentialsGenerator(traineeDao, trainerDao);

        TrainerService trainerService =
                new TrainerService(trainerDao, generator);

        Trainer trainer =
                new Trainer(
                        "John",
                        "Smith",
                        new Specialization(1, "Fitness"),
                        1
                );

        trainerService.createTrainer(trainer);

        Trainer savedTrainer =
                trainerDao.getTrainerById(1);

        assertNotNull(savedTrainer);
        assertEquals("John.Smith", savedTrainer.getUserName());
        assertNotNull(savedTrainer.getPassword());
        assertEquals(10, savedTrainer.getPassword().length());
        assertTrue(savedTrainer.getIsActive());
    }
}