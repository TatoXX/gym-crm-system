package org.epam.gym_crm_system1.service;

import org.epam.gym_crm_system1.dao.TrainingDao;
import org.epam.gym_crm_system1.exception.ValidationException;
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

        if (training == null) {
            throw new ValidationException("Training is required");
        }

        if (training.getTrainingName() == null ||
                training.getTrainingName().isBlank()) {
            throw new ValidationException("Training name is required");
        }

        if (training.getTrainingDate() == null) {
            throw new ValidationException("Training date is required");
        }

        if (training.getTrainingDurationMinutes() <= 0) {
            throw new ValidationException("Training duration must be positive");
        }

        if (training.getTrainingType() == null) {
            throw new ValidationException("Training type is required");
        }

        if (training.getTrainer() == null) {
            throw new ValidationException("Trainer is required");
        }

        if (training.getTrainee() == null) {
            throw new ValidationException("Trainee is required");
        }

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