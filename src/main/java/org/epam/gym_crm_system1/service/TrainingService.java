package org.epam.gym_crm_system1.service;

import org.epam.gym_crm_system1.exception.ValidationException;
import org.epam.gym_crm_system1.model.Training;
import org.epam.gym_crm_system1.repository.TrainingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.epam.gym_crm_system1.client.TrainerWorkloadClient;
import org.epam.gym_crm_system1.dto.request.ActionType;
import org.epam.gym_crm_system1.dto.request.TrainerWorkloadRequest;
import org.epam.gym_crm_system1.model.Trainer;
import java.util.Collection;

@Service
public class TrainingService {

    private static final Logger logger =
            LoggerFactory.getLogger(TrainingService.class);

    private final TrainingRepository trainingRepository;
    private final TrainerWorkloadClient trainerWorkloadClient;

    public TrainingService(
            TrainingRepository trainingRepository,
            TrainerWorkloadClient trainerWorkloadClient) {

        this.trainingRepository = trainingRepository;
        this.trainerWorkloadClient = trainerWorkloadClient;
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

        trainingRepository.saveTraining(training);

        Trainer trainer = training.getTrainer();

        TrainerWorkloadRequest workloadRequest =
                new TrainerWorkloadRequest();

        workloadRequest.setTrainerUsername(trainer.getUserName());
        workloadRequest.setTrainerFirstName(trainer.getFirstName());
        workloadRequest.setTrainerLastName(trainer.getLastName());
        workloadRequest.setIsActive(trainer.getIsActive());
        workloadRequest.setTrainingDate(training.getTrainingDate());
        workloadRequest.setTrainingDuration(
                training.getTrainingDurationMinutes()
        );
        workloadRequest.setActionType(ActionType.ADD);

        trainerWorkloadClient.updateWorkload(workloadRequest);

        logger.info("Training created successfully");
    }

    @Transactional(readOnly = true)
    public Training selectTrainingById(int id) {

        logger.info("Selecting training with id {}", id);

        return trainingRepository.getTrainingById(id);
    }

    @Transactional(readOnly = true)
    public Collection<Training> selectAllTrainings() {

        logger.info("Selecting all trainings");

        return trainingRepository.getAllTrainings();
    }
}