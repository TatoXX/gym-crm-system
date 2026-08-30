package org.epam.gym_crm_system1.service;

import org.epam.gym_crm_system1.exception.EntityNotFoundException;
import org.epam.gym_crm_system1.exception.InvalidCredentialsException;
import org.epam.gym_crm_system1.exception.ProfileStatusException;
import org.epam.gym_crm_system1.exception.ValidationException;
import org.epam.gym_crm_system1.helper.UserCredentialsGenerator;
import org.epam.gym_crm_system1.model.Trainee;
import org.epam.gym_crm_system1.model.Trainer;
import org.epam.gym_crm_system1.model.Training;
import org.epam.gym_crm_system1.repository.TraineeRepository;
import org.epam.gym_crm_system1.repository.TrainerRepository;
import org.epam.gym_crm_system1.repository.TrainingRepository;
import org.epam.gym_crm_system1.validator.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.epam.gym_crm_system1.client.TrainerWorkloadClient;
import org.epam.gym_crm_system1.dto.request.ActionType;
import org.epam.gym_crm_system1.dto.request.TrainerWorkloadRequest;

import java.util.List;
import java.time.LocalDate;
import java.util.Collection;

@Service
public class TraineeService {

    private static final Logger logger =
            LoggerFactory.getLogger(TraineeService.class);

    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingRepository trainingRepository;
    private final UserCredentialsGenerator userCredentialsGenerator;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;
    private final TrainerWorkloadClient trainerWorkloadClient;

    public TraineeService(TraineeRepository traineeRepository,
                          TrainingRepository trainingRepository,
                          TrainerRepository trainerRepository,
                          UserCredentialsGenerator userCredentialsGenerator,
                          UserValidator userValidator,
                          PasswordEncoder passwordEncoder,
                          TrainerWorkloadClient trainerWorkloadClient) {

        this.traineeRepository = traineeRepository;
        this.trainingRepository = trainingRepository;
        this.trainerRepository = trainerRepository;
        this.userCredentialsGenerator = userCredentialsGenerator;
        this.userValidator = userValidator;
        this.passwordEncoder = passwordEncoder;
        this.trainerWorkloadClient = trainerWorkloadClient;
    }

    @Transactional
    public String createTrainee(Trainee trainee) {

        if (trainee == null) {
            throw new ValidationException("Trainee is required");
        }

        userValidator.validateName(trainee.getFirstName(), "First name");
        userValidator.validateName(trainee.getLastName(), "Last name");

        logger.info("Creating trainee with id {}",
                trainee.getId());

        String username =
                userCredentialsGenerator.generateUsername(
                        trainee.getUser()
                );

        String password =
                userCredentialsGenerator.generatePassword();

        trainee.setUserName(username);

        // Store only the BCrypt hash in the entity/database
        trainee.setPassword(passwordEncoder.encode(password));

        trainee.setIsActive(true);

        traineeRepository.saveTrainee(trainee);

        logger.info("Trainee created successfully with username {}",
                trainee.getUserName());

        // Return the original generated password to registration controller
        return password;
    }

    @Transactional(readOnly = true)
    public Trainee selectTraineeById(int id) {

        logger.info("Selecting trainee with id {}", id);

        return traineeRepository.findTraineeById(id);
    }

    @Transactional
    public void updateTrainee(Trainee trainee) {

        if (trainee == null) {
            throw new ValidationException("Trainee is required");
        }

        userValidator.validateId(trainee.getId());
        userValidator.validateName(trainee.getFirstName(), "First name");
        userValidator.validateName(trainee.getLastName(), "Last name");
        userValidator.validateUsername(trainee.getUserName());
        userValidator.validatePassword(trainee.getPassword());

        logger.info("Updating trainee with id {}",
                trainee.getId());

        traineeRepository.updateTrainee(trainee);

        logger.info("Trainee updated successfully");
    }

    @Transactional
    public void deleteTraineeById(int id) {

        logger.info("Deleting trainee with id {}", id);

        Trainee trainee =
                traineeRepository.findTraineeById(id);

        if (trainee == null) {
            traineeRepository.deleteTraineeById(id);

            logger.info("Trainee deleted successfully");
            return;
        }

        List<Training> trainingsToDelete =
                List.copyOf(trainee.getTrainings());

        traineeRepository.deleteTraineeById(id);

        for (Training training : trainingsToDelete) {
            sendDeleteWorkload(training);
        }

        logger.info("Trainee deleted successfully");
    }

    @Transactional(readOnly = true)
    public Collection<Trainee> selectAllTrainees() {

        logger.info("Selecting all trainees");

        return traineeRepository.findAllTrainees();
    }

    @Transactional(readOnly = true)
    public boolean isTraineeCredentialsValid(String username, String password) {

        logger.info("Checking trainee credentials for username {}", username);

        Trainee trainee =
                traineeRepository.findTraineeByUsername(username);

        if (trainee == null) {
            logger.warn("Trainee with username {} not found", username);
            return false;
        }

        return passwordEncoder.matches(password, trainee.getPassword());
    }

    @Transactional
    public void changeTraineePassword(String username,
                                      String oldPassword,
                                      String newPassword) {

        logger.info("Changing trainee password for username {}", username);

        if (newPassword == null || newPassword.isBlank()) {
            throw new ValidationException("New password is required");
        }

        Trainee trainee =
                traineeRepository.findTraineeByUsername(username);

        if (trainee == null ||
                !passwordEncoder.matches(oldPassword, trainee.getPassword())) {

            throw new InvalidCredentialsException(
                    "Invalid username or password"
            );
        }

        trainee.setPassword(passwordEncoder.encode(newPassword));

        traineeRepository.updateTrainee(trainee);

        logger.info("Trainee password changed successfully");
    }

    @Transactional
    public void activateTrainee(String username) {

        logger.info("Activating trainee with username {}", username);

        Trainee trainee =
                traineeRepository.findTraineeByUsername(username);

        if (trainee == null) {
            throw new EntityNotFoundException("Trainee not found");
        }

        if (trainee.getIsActive()) {
            throw new ProfileStatusException("Trainee is already active");
        }

        trainee.setIsActive(true);

        traineeRepository.updateTrainee(trainee);

        logger.info("Trainee activated successfully");
    }

    @Transactional
    public void deactivateTrainee(String username) {

        logger.info("Deactivating trainee with username {}", username);

        Trainee trainee =
                traineeRepository.findTraineeByUsername(username);

        if (trainee == null) {
            throw new EntityNotFoundException("Trainee not found");
        }

        if (!trainee.getIsActive()) {
            throw new ProfileStatusException("Trainee is already inactive");
        }

        trainee.setIsActive(false);

        traineeRepository.updateTrainee(trainee);

        logger.info("Trainee deactivated successfully");
    }

    @Transactional
    public void deleteTraineeByUsername(String username) {

        if (username == null || username.isBlank()) {
            throw new ValidationException("Username is required");
        }

        username = username.trim();

        logger.info("Deleting trainee with username {}", username);

        Trainee trainee =
                traineeRepository.findTraineeByUsername(username);

        if (trainee == null) {
            throw new EntityNotFoundException("Trainee not found");
        }

        deleteTraineeById(trainee.getId());

        logger.info("Trainee deleted by username successfully");
    }

    @Transactional(readOnly = true)
    public Trainee selectTraineeByUsername(String username) {

        if (username == null || username.isBlank()) {
            throw new ValidationException("Username is required");
        }

        username = username.trim();

        logger.info("Selecting trainee with username {}", username);

        Trainee trainee =
                traineeRepository.findTraineeByUsername(username);

        if (trainee == null) {
            throw new EntityNotFoundException("Trainee not found");
        }

        return trainee;
    }

    @Transactional(readOnly = true)
    public Collection<Training> getTraineeTrainingsByCriteria(
            String traineeUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerUsername,
            String trainingTypeName
    ) {

        if (traineeUsername == null || traineeUsername.isBlank()) {
            throw new ValidationException("Trainee username is required");
        }

        traineeUsername = traineeUsername.trim();

        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new ValidationException("From date cannot be after to date");
        }

        Trainee trainee =
                traineeRepository.findTraineeByUsername(traineeUsername);

        if (trainee == null) {
            throw new EntityNotFoundException("Trainee not found");
        }

        logger.info("Getting trainings for trainee username {}", traineeUsername);

        return trainingRepository.findTrainingsByTraineeUsernameAndCriteria(
                traineeUsername,
                fromDate,
                toDate,
                trainerUsername,
                trainingTypeName
        );
    }

    @Transactional(readOnly = true)
    public Collection<Trainer> getTrainersNotAssignedToTrainee(
            String traineeUsername
    ) {

        if (traineeUsername == null || traineeUsername.isBlank()) {
            throw new ValidationException("Trainee username is required");
        }

        traineeUsername = traineeUsername.trim();

        Trainee trainee =
                traineeRepository.findTraineeByUsername(traineeUsername);

        if (trainee == null) {
            throw new EntityNotFoundException("Trainee not found");
        }

        logger.info("Getting trainers not assigned to trainee username {}",
                traineeUsername);

        return trainerRepository.findTrainersNotAssignedToTrainee(
                traineeUsername
        );
    }

    @Transactional
    public void updateTraineeTrainersList(String traineeUsername,
                                          Collection<String> trainerUsernames) {

        if (traineeUsername == null || traineeUsername.isBlank()) {
            throw new ValidationException("Trainee username is required");
        }

        if (trainerUsernames == null) {
            throw new ValidationException("Trainer usernames are required");
        }

        traineeUsername = traineeUsername.trim();

        Trainee trainee =
                traineeRepository.findTraineeByUsername(traineeUsername);

        if (trainee == null) {
            throw new EntityNotFoundException("Trainee not found");
        }

        for (Trainer oldTrainer : trainee.getTrainers()) {
            oldTrainer.getTrainees().remove(trainee);
        }

        trainee.getTrainers().clear();

        for (String trainerUsername : trainerUsernames) {

            if (trainerUsername == null || trainerUsername.isBlank()) {
                throw new ValidationException("Trainer username is required");
            }

            Trainer trainer =
                    trainerRepository.findTrainerByUsername(
                            trainerUsername.trim()
                    );

            if (trainer == null) {
                throw new EntityNotFoundException("Trainer not found");
            }

            trainee.getTrainers().add(trainer);
            trainer.getTrainees().add(trainee);
        }

        traineeRepository.updateTrainee(trainee);

        logger.info("Trainee trainers list updated successfully");
    }

    private void sendDeleteWorkload(Training training) {

        Trainer trainer = training.getTrainer();

        TrainerWorkloadRequest workloadRequest =
                new TrainerWorkloadRequest();

        workloadRequest.setTrainerUsername(
                trainer.getUserName());

        workloadRequest.setTrainerFirstName(
                trainer.getFirstName());

        workloadRequest.setTrainerLastName(
                trainer.getLastName());

        workloadRequest.setIsActive(
                trainer.getIsActive());

        workloadRequest.setTrainingDate(
                training.getTrainingDate());

        workloadRequest.setTrainingDuration(
                training.getTrainingDurationMinutes());

        workloadRequest.setActionType(
                ActionType.DELETE);

        trainerWorkloadClient.updateWorkload(workloadRequest);
    }
}