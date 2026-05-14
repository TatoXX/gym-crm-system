package org.epam.gym_crm_system1.service;

import org.epam.gym_crm_system1.dao.TrainingDao;
import org.epam.gym_crm_system1.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class TrainingService {

    private static final Logger logger =
            LoggerFactory.getLogger(TrainingService.class);

    private final TrainingDao trainingDao;

    public TrainingService(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    public void createTraining(Training training) {

        logger.info("Creating training with id {}",
                training.getTrainingId());

        trainingDao.saveTraining(training);

        logger.info("Training created successfully");
    }

    public Training selectTrainingById(int id) {

        logger.info("Selecting training with id {}", id);

        return trainingDao.getTrainingById(id);
    }

    public Collection<Training> selectAllTrainings() {

        logger.info("Selecting all trainings");

        return trainingDao.getAllTrainings();
    }
}