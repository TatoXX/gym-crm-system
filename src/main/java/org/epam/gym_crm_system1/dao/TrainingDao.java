package org.epam.gym_crm_system1.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.epam.gym_crm_system1.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class TrainingDao {

    private static final Logger logger =
            LoggerFactory.getLogger(TrainingDao.class);

    @PersistenceContext
    private EntityManager entityManager;

    public void saveTraining(Training training) {

        logger.info("Saving training with id {}",
                training.getTrainingId());

        entityManager.persist(training);

        logger.info("Training saved successfully");
    }

    public Training getTrainingById(int trainingId) {

        logger.info("Finding training with id {}",
                trainingId);

        Training training =
                entityManager.find(Training.class, trainingId);

        if (training == null) {
            logger.warn("Training with id {} not found",
                    trainingId);
        }

        return training;
    }

    public Collection<Training> getAllTrainings() {

        logger.info("Finding all trainings");

        return entityManager
                .createQuery("SELECT t FROM Training t", Training.class)
                .getResultList();
    }
}