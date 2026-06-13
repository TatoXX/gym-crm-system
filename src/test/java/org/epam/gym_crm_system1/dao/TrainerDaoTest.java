package org.epam.gym_crm_system1.dao;

import org.epam.gym_crm_system1.model.TrainingType;
import org.epam.gym_crm_system1.model.Trainer;
import org.epam.gym_crm_system1.storage.Storage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrainerDaoTest {

    @Test
    void shouldSaveTrainer() {

        Storage storage = new Storage();

        TrainerDao trainerDao =
                new TrainerDao(storage);

        Trainer trainer =
                new Trainer(
                        "John",
                        "Smith",
                        new TrainingType(1, "Fitness")
                );

        trainer.setId(1);

        trainerDao.saveTrainer(trainer);

        assertEquals(trainer,
                trainerDao.getTrainerById(1));
    }

    @Test
    void shouldUpdateTrainer() {

        Storage storage = new Storage();

        TrainerDao trainerDao =
                new TrainerDao(storage);

        Trainer trainer =
                new Trainer(
                        "John",
                        "Smith",
                        new TrainingType(1, "Fitness")
                );

        trainer.setId(1);

        trainerDao.saveTrainer(trainer);

        trainer.setTrainingType(
                new TrainingType(2, "Yoga")
        );

        trainerDao.updateTrainer(trainer);

        assertEquals("Yoga",
                trainerDao.getTrainerById(1)
                        .getTrainingType()
                        .getName());
    }
}