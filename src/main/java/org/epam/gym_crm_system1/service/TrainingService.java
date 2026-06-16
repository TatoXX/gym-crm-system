package org.epam.gym_crm_system1.service;

import org.epam.gym_crm_system1.dao.TrainingDao;
import org.epam.gym_crm_system1.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class TrainingService {

    private static final Logger logger =
            LoggerFactory.getLogger(TrainingService.class);

    private final TrainingDao trainingDao;

    public TrainingService(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Transactional
    public void createTraining(Training training) {

        logger.info("Creating training with id {}",
                training.getTrainingId());

        trainingDao.saveTraining(training);

        logger.info("Training created successfully");
    }

    @Transactional(readOnly = true)
    public Training selectTrainingById(int id) {

        logger.info("Selecting training with id {}", id);

        return trainingDao.getTrainingById(id);
    }

    @Transactional(readOnly = true)
    public Collection<Training> selectAllTrainings() {

        logger.info("Selecting all trainings");

        return trainingDao.getAllTrainings();
    }
}