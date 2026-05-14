package org.epam.gym_crm_system1.dao;

import org.epam.gym_crm_system1.model.Training;
import org.epam.gym_crm_system1.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class TrainingDao {

    private static final Logger logger =
            LoggerFactory.getLogger(TrainingDao.class);

    private final Storage storage;

    public TrainingDao(Storage storage) {
        this.storage = storage;
    }

    public void saveTraining(Training training) {

        logger.info("Saving training with id {}",
                training.getTrainingId());

        storage.getTrainings()
                .put(training.getTrainingId(), training);

        logger.info("Training saved successfully");
    }

    public Training getTrainingById(int trainingId) {

        logger.info("Finding training with id {}",
                trainingId);

        Training training =
                storage.getTrainings().get(trainingId);

        if (training == null) {

            logger.warn("Training with id {} not found",
                    trainingId);
        }

        return training;
    }

    public Collection<Training> getAllTrainings() {

        logger.info("Finding all trainings");

        return storage.getTrainings().values();
    }
}