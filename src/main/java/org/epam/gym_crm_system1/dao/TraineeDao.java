package org.epam.gym_crm_system1.dao;

import org.epam.gym_crm_system1.model.Trainee;
import org.epam.gym_crm_system1.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class TraineeDao {

    private static final Logger logger =
            LoggerFactory.getLogger(TraineeDao.class);

    private final Storage storage;

    public TraineeDao(Storage storage) {
        this.storage = storage;
    }

    public void saveTrainee(Trainee trainee) {

        logger.info("Saving trainee with id {}",
                trainee.getUserId());

        storage.getTrainees()
                .put(trainee.getUserId(), trainee);

        logger.info("Trainee saved successfully");
    }

    public Trainee findTraineeById(int userId) {

        logger.info("Finding trainee with id {}", userId);

        Trainee trainee =
                storage.getTrainees().get(userId);

        if (trainee == null) {

            logger.warn("Trainee with id {} not found",
                    userId);
        }

        return trainee;
    }

    public Collection<Trainee> findAllTrainees() {

        logger.info("Finding all trainees");

        return storage.getTrainees().values();
    }

    public void updateTrainee(Trainee trainee) {

        logger.info("Updating trainee with id {}",
                trainee.getUserId());

        storage.getTrainees()
                .put(trainee.getUserId(), trainee);

        logger.info("Trainee updated successfully");
    }

    public void deleteTraineeById(int id) {

        logger.info("Deleting trainee with id {}", id);

        storage.getTrainees().remove(id);

        logger.info("Trainee deleted successfully");
    }
}