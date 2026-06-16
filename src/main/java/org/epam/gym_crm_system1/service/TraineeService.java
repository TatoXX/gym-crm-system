package org.epam.gym_crm_system1.service;

import org.epam.gym_crm_system1.exception.EntityNotFoundException;
import org.epam.gym_crm_system1.exception.InvalidCredentialsException;
import org.epam.gym_crm_system1.exception.ProfileStatusException;
import org.epam.gym_crm_system1.exception.ValidationException;
import org.springframework.transaction.annotation.Transactional;
import org.epam.gym_crm_system1.helper.UserCredentialsGenerator;
import org.epam.gym_crm_system1.dao.TraineeDao;
import org.epam.gym_crm_system1.model.Trainee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class TraineeService {

    private static final Logger logger =
            LoggerFactory.getLogger(TraineeService.class);

    private final TraineeDao traineeDao;
    private final UserCredentialsGenerator userCredentialsGenerator;

    public TraineeService(TraineeDao traineeDao,
                          UserCredentialsGenerator userCredentialsGenerator) {

        this.traineeDao = traineeDao;
        this.userCredentialsGenerator = userCredentialsGenerator;
    }

    @Transactional
    public void createTrainee(Trainee trainee) {

        logger.info("Creating trainee with id {}",
                trainee.getId());

        String username =
                userCredentialsGenerator.generateUsername(trainee);

        String password =
                userCredentialsGenerator.generatePassword();

        trainee.setUserName(username);
        trainee.setPassword(password);
        trainee.setIsActive(true);

        traineeDao.saveTrainee(trainee);

        logger.info("Trainee created successfully with username {}",
                trainee.getUserName());
    }

    @Transactional(readOnly = true)
    public Trainee selectTraineeById(int id) {

        logger.info("Selecting trainee with id {}", id);

        return traineeDao.findTraineeById(id);
    }

    @Transactional
    public void updateTrainee(Trainee trainee) {

        logger.info("Updating trainee with id {}",
                trainee.getId());

        traineeDao.updateTrainee(trainee);

        logger.info("Trainee updated successfully");
    }

    @Transactional
    public void deleteTraineeById(int id) {

        logger.info("Deleting trainee with id {}", id);

        traineeDao.deleteTraineeById(id);

        logger.info("Trainee deleted successfully");
    }


    @Transactional(readOnly = true)
    public Collection<Trainee> selectAllTrainees() {

        logger.info("Selecting all trainees");

        return traineeDao.findAllTrainees();
    }

    @Transactional(readOnly = true)
    public boolean isTraineeCredentialsValid(String username, String password) {

        logger.info("Checking trainee credentials for username {}", username);

        Trainee trainee =
                traineeDao.findTraineeByUsername(username);

        if (trainee == null) {
            logger.warn("Trainee with username {} not found", username);
            return false;
        }

        return trainee.getPassword().equals(password);
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
                traineeDao.findTraineeByUsername(username);

        if (trainee == null ||
                !trainee.getPassword().equals(oldPassword)) {

            throw new InvalidCredentialsException(
                    "Invalid username or password"
            );
        }

        trainee.setPassword(newPassword);

        traineeDao.updateTrainee(trainee);

        logger.info("Trainee password changed successfully");
    }


    @Transactional
    public void activateTrainee(String username) {

        logger.info("Activating trainee with username {}", username);

        Trainee trainee =
                traineeDao.findTraineeByUsername(username);

        if (trainee == null) {
            throw new EntityNotFoundException("Trainee not found");
        }

        if (trainee.getIsActive()) {
            throw new ProfileStatusException("Trainee is already active");
        }

        trainee.setIsActive(true);

        traineeDao.updateTrainee(trainee);

        logger.info("Trainee activated successfully");
    }

    @Transactional
    public void deactivateTrainee(String username) {

        logger.info("Deactivating trainee with username {}", username);

        Trainee trainee =
                traineeDao.findTraineeByUsername(username);

        if (trainee == null) {
            throw new EntityNotFoundException("Trainee not found");
        }

        if (!trainee.getIsActive()) {
            throw new ProfileStatusException("Trainee is already inactive");
        }

        trainee.setIsActive(false);

        traineeDao.updateTrainee(trainee);

        logger.info("Trainee deactivated successfully");
    }

    @Transactional
    public void deleteTraineeByUsername(String username) {
        if(username == null || username.isBlank()) {
            throw new ValidationException("Username is required");
        }

        username = username.trim();

        logger.info("Deleting trainee with username {}", username);

        Trainee trainee = traineeDao.findTraineeByUsername(username);

        if (trainee == null) {
            throw new EntityNotFoundException("Trainee not found");
        }

        traineeDao.deleteTraineeById(trainee.getId());

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
                traineeDao.findTraineeByUsername(username);

        if (trainee == null) {
            throw new EntityNotFoundException("Trainee not found");
        }

        return trainee;
    }
    
}