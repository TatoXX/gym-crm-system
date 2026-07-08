package org.epam.gym_crm_system1.service;

import org.epam.gym_crm_system1.exception.EntityNotFoundException;
import org.epam.gym_crm_system1.exception.InvalidCredentialsException;
import org.epam.gym_crm_system1.exception.ProfileStatusException;
import org.epam.gym_crm_system1.exception.ValidationException;
import org.epam.gym_crm_system1.helper.UserCredentialsGenerator;
import org.epam.gym_crm_system1.model.Trainer;
import org.epam.gym_crm_system1.model.Training;
import org.epam.gym_crm_system1.repository.TrainerRepository;
import org.epam.gym_crm_system1.repository.TrainingRepository;
import org.epam.gym_crm_system1.validator.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;

@Service
public class TrainerService {

    private static final Logger logger =
            LoggerFactory.getLogger(TrainerService.class);

    private final TrainerRepository trainerRepository;
    private final UserCredentialsGenerator userCredentialsGenerator;
    private final TrainingRepository trainingRepository;
    private final UserValidator userValidator;

    public TrainerService(TrainerRepository trainerRepository,
                          TrainingRepository trainingRepository,
                          UserCredentialsGenerator userCredentialsGenerator,
                          UserValidator userValidator) {

        this.userValidator = userValidator;
        this.trainingRepository = trainingRepository;
        this.trainerRepository = trainerRepository;
        this.userCredentialsGenerator = userCredentialsGenerator;
    }

    @Transactional
    public void createTrainer(Trainer trainer) {

        if (trainer == null) {
            throw new ValidationException("Trainer is required");
        }

        userValidator.validateName(trainer.getFirstName(), "First name");
        userValidator.validateName(trainer.getLastName(), "Last name");

        if (trainer.getTrainingType() == null) {
            throw new ValidationException("Training type is required");
        }

        logger.info("Creating trainer with id {}",
                trainer.getId());

        String username =
                userCredentialsGenerator.generateUsername(
                        trainer.getUser()
                );

        String password =
                userCredentialsGenerator.generatePassword();

        trainer.setUserName(username);
        trainer.setPassword(password);
        trainer.setIsActive(true);

        trainerRepository.saveTrainer(trainer);

        logger.info("Trainer created successfully with username {}",
                trainer.getUserName());
    }

    @Transactional(readOnly = true)
    public Trainer selectTrainerById(int id) {

        logger.info("Selecting trainer with id {}", id);

        return trainerRepository.getTrainerById(id);
    }

    @Transactional
    public void updateTrainer(Trainer trainer) {

        if (trainer == null) {
            throw new ValidationException("Trainer is required");
        }

        userValidator.validateId(trainer.getId());
        userValidator.validateName(trainer.getFirstName(), "First name");
        userValidator.validateName(trainer.getLastName(), "Last name");
        userValidator.validateUsername(trainer.getUserName());
        userValidator.validatePassword(trainer.getPassword());

        if (trainer.getTrainingType() == null) {
            throw new ValidationException("Training type is required");
        }

        logger.info("Updating trainer with id {}",
                trainer.getId());

        trainerRepository.updateTrainer(trainer);

        logger.info("Trainer updated successfully");
    }

    @Transactional(readOnly = true)
    public Collection<Trainer> selectAllTrainers() {

        logger.info("Selecting all trainers");

        return trainerRepository.getAllTrainers();
    }

    @Transactional(readOnly = true)
    public boolean isTrainerCredentialsValid(String username, String password) {

        logger.info("Checking trainer credentials for username {}", username);

        Trainer trainer =
                trainerRepository.findTrainerByUsername(username);

        if (trainer == null) {
            logger.warn("Trainer with username {} not found", username);
            return false;
        }

        return trainer.getPassword().equals(password);
    }

    @Transactional
    public void changeTrainerPassword(String username,
                                      String oldPassword,
                                      String newPassword) {

        logger.info("Changing trainer password for username {}", username);

        if (newPassword == null || newPassword.isBlank()) {
            throw new ValidationException("New password is required");
        }

        Trainer trainer =
                trainerRepository.findTrainerByUsername(username);

        if (trainer == null ||
                !trainer.getPassword().equals(oldPassword)) {

            throw new InvalidCredentialsException(
                    "Invalid username or password"
            );
        }

        trainer.setPassword(newPassword);

        trainerRepository.updateTrainer(trainer);

        logger.info("Trainer password changed successfully");
    }

    @Transactional
    public void activateTrainer(String username) {

        logger.info("Activating trainer with username {}", username);

        Trainer trainer =
                trainerRepository.findTrainerByUsername(username);

        if (trainer == null) {
            throw new EntityNotFoundException("Trainer not found");
        }

        if (trainer.getIsActive()) {
            throw new ProfileStatusException("Trainer is already active");
        }

        trainer.setIsActive(true);

        trainerRepository.updateTrainer(trainer);

        logger.info("Trainer activated successfully");
    }

    @Transactional
    public void deactivateTrainer(String username) {

        logger.info("Deactivating trainer with username {}", username);

        Trainer trainer =
                trainerRepository.findTrainerByUsername(username);

        if (trainer == null) {
            throw new EntityNotFoundException("Trainer not found");
        }

        if (!trainer.getIsActive()) {
            throw new ProfileStatusException("Trainer is already inactive");
        }

        trainer.setIsActive(false);

        trainerRepository.updateTrainer(trainer);

        logger.info("Trainer deactivated successfully");
    }

    @Transactional(readOnly = true)
    public Trainer selectTrainerByUsername(String username) {

        if (username == null || username.isBlank()) {
            throw new ValidationException("Username is required");
        }

        username = username.trim();

        logger.info("Selecting trainer with username {}", username);

        Trainer trainer =
                trainerRepository.findTrainerByUsername(username);

        if (trainer == null) {
            throw new EntityNotFoundException("Trainer not found");
        }

        return trainer;
    }

    @Transactional(readOnly = true)
    public Collection<Training> getTrainerTrainingsByCriteria(
            String trainerUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName
    ) {

        if (trainerUsername == null || trainerUsername.isBlank()) {
            throw new ValidationException("Trainer username is required");
        }

        trainerUsername = trainerUsername.trim();

        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new ValidationException("From date cannot be after to date");
        }

        Trainer trainer =
                trainerRepository.findTrainerByUsername(trainerUsername);

        if (trainer == null) {
            throw new EntityNotFoundException("Trainer not found");
        }

        logger.info("Getting trainings for trainer username {}", trainerUsername);

        return trainingRepository.findTrainingsByTrainerUsernameAndCriteria(
                trainerUsername,
                fromDate,
                toDate,
                traineeName
        );
    }
}