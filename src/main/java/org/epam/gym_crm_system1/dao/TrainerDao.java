package org.epam.gym_crm_system1.dao;

import org.epam.gym_crm_system1.model.Trainer;
import org.epam.gym_crm_system1.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class TrainerDao {

    private static final Logger logger =
            LoggerFactory.getLogger(TrainerDao.class);

    private final Storage storage;

    public TrainerDao(Storage storage) {
        this.storage = storage;
    }

    public void saveTrainer(Trainer trainer) {

        logger.info("Saving trainer with id {}",
                trainer.getUserId());

        storage.getTrainers()
                .put(trainer.getUserId(), trainer);

        logger.info("Trainer saved successfully");
    }

    public void updateTrainer(Trainer trainer) {

        logger.info("Updating trainer with id {}",
                trainer.getUserId());

        storage.getTrainers()
                .put(trainer.getUserId(), trainer);

        logger.info("Trainer updated successfully");
    }

    public Trainer getTrainerById(int trainerId) {

        logger.info("Finding trainer with id {}", trainerId);

        return storage.getTrainers().get(trainerId);
    }

    public Collection<Trainer> getAllTrainers() {

        logger.info("Finding all trainers");

        return storage.getTrainers().values();
    }
}