package org.epam.gym_crm_system1.facade;

import org.epam.gym_crm_system1.exception.InvalidCredentialsException;
import org.epam.gym_crm_system1.model.Trainee;
import org.epam.gym_crm_system1.model.Trainer;
import org.epam.gym_crm_system1.model.Training;
import org.epam.gym_crm_system1.service.TraineeService;
import org.epam.gym_crm_system1.service.TrainerService;
import org.epam.gym_crm_system1.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collection;

@Component
public class GymFacade {

    private static final Logger logger =
            LoggerFactory.getLogger(GymFacade.class);

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public GymFacade(TraineeService traineeService,
                     TrainerService trainerService,
                     TrainingService trainingService) {

        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    // =========================================================
    // Create profile methods do NOT require authentication
    // =========================================================

    public void createTrainee(Trainee trainee) {

        logger.info("Facade request: create trainee");

        traineeService.createTrainee(trainee);
    }

    public void createTrainer(Trainer trainer) {

        logger.info("Facade request: create trainer");

        trainerService.createTrainer(trainer);
    }

    // =========================================================
    // Credential matching methods
    // =========================================================

    public boolean isTraineeCredentialsValid(String username,
                                             String password) {

        logger.info("Facade request: check trainee credentials");

        return traineeService.isTraineeCredentialsValid(
                username,
                password
        );
    }

    public boolean isTrainerCredentialsValid(String username,
                                             String password) {

        logger.info("Facade request: check trainer credentials");

        return trainerService.isTrainerCredentialsValid(
                username,
                password
        );
    }

    // =========================================================
    // Password change methods
    // These already authenticate by checking old password
    // =========================================================

    public void changeTraineePassword(String username,
                                      String oldPassword,
                                      String newPassword) {

        logger.info("Facade request: change trainee password");

        traineeService.changeTraineePassword(
                username,
                oldPassword,
                newPassword
        );
    }

    public void changeTrainerPassword(String username,
                                      String oldPassword,
                                      String newPassword) {

        logger.info("Facade request: change trainer password");

        trainerService.changeTrainerPassword(
                username,
                oldPassword,
                newPassword
        );
    }

    // =========================================================
    // Trainee authenticated methods
    // =========================================================

    public Trainee selectTraineeByUsername(String username,
                                           String password) {

        logger.info("Facade request: select trainee by username with authentication");

        authenticateTrainee(username, password);

        return traineeService.selectTraineeByUsername(username);
    }

    public void updateTrainee(Trainee trainee,
                              String username,
                              String password) {

        logger.info("Facade request: update trainee with authentication");

        authenticateTrainee(username, password);

        if (trainee == null ||
                trainee.getUserName() == null ||
                !trainee.getUserName().equals(username)) {

            throw new InvalidCredentialsException(
                    "You can update only your own trainee profile"
            );
        }

        traineeService.updateTrainee(trainee);
    }

    public void deleteTraineeByUsername(String username,
                                        String password) {

        logger.info("Facade request: delete trainee by username with authentication");

        authenticateTrainee(username, password);

        traineeService.deleteTraineeByUsername(username);
    }

    public void activateTrainee(String username,
                                String password) {

        logger.info("Facade request: activate trainee with authentication");

        authenticateTrainee(username, password);

        traineeService.activateTrainee(username);
    }

    public void deactivateTrainee(String username,
                                  String password) {

        logger.info("Facade request: deactivate trainee with authentication");

        authenticateTrainee(username, password);

        traineeService.deactivateTrainee(username);
    }

    public Collection<Training> getTraineeTrainingsByCriteria(
            String traineeUsername,
            String password,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerUsername,
            String trainingTypeName
    ) {

        logger.info("Facade request: get trainee trainings by criteria with authentication");

        authenticateTrainee(traineeUsername, password);

        return traineeService.getTraineeTrainingsByCriteria(
                traineeUsername,
                fromDate,
                toDate,
                trainerUsername,
                trainingTypeName
        );
    }

    public Collection<Trainer> getTrainersNotAssignedToTrainee(
            String traineeUsername,
            String password
    ) {

        logger.info("Facade request: get trainers not assigned to trainee with authentication");

        authenticateTrainee(traineeUsername, password);

        return traineeService.getTrainersNotAssignedToTrainee(
                traineeUsername
        );
    }

    public void updateTraineeTrainersList(
            String traineeUsername,
            String password,
            Collection<String> trainerUsernames
    ) {

        logger.info("Facade request: update trainee trainers list with authentication");

        authenticateTrainee(traineeUsername, password);

        traineeService.updateTraineeTrainersList(
                traineeUsername,
                trainerUsernames
        );
    }

    // =========================================================
    // Trainer authenticated methods
    // =========================================================

    public Trainer selectTrainerByUsername(String username,
                                           String password) {

        logger.info("Facade request: select trainer by username with authentication");

        authenticateTrainer(username, password);

        return trainerService.selectTrainerByUsername(username);
    }

    public void updateTrainer(Trainer trainer,
                              String username,
                              String password) {

        logger.info("Facade request: update trainer with authentication");

        authenticateTrainer(username, password);

        if (trainer == null ||
                trainer.getUserName() == null ||
                !trainer.getUserName().equals(username)) {

            throw new InvalidCredentialsException(
                    "You can update only your own trainer profile"
            );
        }

        trainerService.updateTrainer(trainer);
    }

    public void activateTrainer(String username,
                                String password) {

        logger.info("Facade request: activate trainer with authentication");

        authenticateTrainer(username, password);

        trainerService.activateTrainer(username);
    }

    public void deactivateTrainer(String username,
                                  String password) {

        logger.info("Facade request: deactivate trainer with authentication");

        authenticateTrainer(username, password);

        trainerService.deactivateTrainer(username);
    }

    public Collection<Training> getTrainerTrainingsByCriteria(
            String trainerUsername,
            String password,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName
    ) {

        logger.info("Facade request: get trainer trainings by criteria with authentication");

        authenticateTrainer(trainerUsername, password);

        return trainerService.getTrainerTrainingsByCriteria(
                trainerUsername,
                fromDate,
                toDate,
                traineeName
        );
    }

    public void createTraining(Training training,
                               String trainerUsername,
                               String password) {

        logger.info("Facade request: create training with trainer authentication");

        authenticateTrainer(trainerUsername, password);

        if (training == null ||
                training.getTrainer() == null ||
                training.getTrainer().getUserName() == null ||
                !training.getTrainer().getUserName().equals(trainerUsername)) {

            throw new InvalidCredentialsException(
                    "Trainer can create training only for their own profile"
            );
        }

        trainingService.createTraining(training);
    }

    // =========================================================
    // Private authentication helpers
    // =========================================================

    private void authenticateTrainee(String username,
                                     String password) {

        if (!traineeService.isTraineeCredentialsValid(username, password)) {
            throw new InvalidCredentialsException(
                    "Invalid trainee username or password"
            );
        }
    }

    private void authenticateTrainer(String username,
                                     String password) {

        if (!trainerService.isTrainerCredentialsValid(username, password)) {
            throw new InvalidCredentialsException(
                    "Invalid trainer username or password"
            );
        }
    }
}