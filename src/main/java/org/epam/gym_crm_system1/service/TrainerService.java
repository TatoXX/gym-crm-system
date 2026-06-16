package org.epam.gym_crm_system1.service;

import org.epam.gym_crm_system1.exception.EntityNotFoundException;
import org.epam.gym_crm_system1.exception.InvalidCredentialsException;
import org.epam.gym_crm_system1.exception.ProfileStatusException;
import org.epam.gym_crm_system1.exception.ValidationException;
import org.epam.gym_crm_system1.helper.UserCredentialsGenerator;
import org.epam.gym_crm_system1.dao.TrainerDao;
import org.epam.gym_crm_system1.model.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class TrainerService {

    private static final Logger logger =
            LoggerFactory.getLogger(TrainerService.class);

    private final TrainerDao trainerDao;
    private final UserCredentialsGenerator userCredentialsGenerator;

    public TrainerService(TrainerDao trainerDao,
                          UserCredentialsGenerator userCredentialsGenerator) {

        this.trainerDao = trainerDao;
        this.userCredentialsGenerator = userCredentialsGenerator;
    }

   @Transactional
    public void createTrainer(Trainer trainer) {

        logger.info("Creating trainer with id {}",
                trainer.getId());

        String username =
                userCredentialsGenerator.generateUsername(trainer);

        String password =
                userCredentialsGenerator.generatePassword();

        trainer.setUserName(username);
        trainer.setPassword(password);
        trainer.setIsActive(true);

        trainerDao.saveTrainer(trainer);

        logger.info("Trainer created successfully with username {}",
                trainer.getUserName());
    }

    @Transactional(readOnly = true)
    public Trainer selectTrainerById(int id) {

        logger.info("Selecting trainer with id {}", id);

        return trainerDao.getTrainerById(id);
    }

    @Transactional
    public void updateTrainer(Trainer trainer) {

        logger.info("Updating trainer with id {}",
                trainer.getId());

        trainerDao.updateTrainer(trainer);

        logger.info("Trainer updated successfully");
    }
    @Transactional(readOnly = true)
    public Collection<Trainer> selectAllTrainers() {

        logger.info("Selecting all trainers");

        return trainerDao.getAllTrainers();
    }

    @Transactional(readOnly = true)
    public boolean isTrainerCredentialsValid(String username, String password) {

        logger.info("Checking trainer credentials for username {}", username);

        Trainer trainer =
                trainerDao.findTrainerByUsername(username);

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
                trainerDao.findTrainerByUsername(username);

        if (trainer == null ||
                !trainer.getPassword().equals(oldPassword)) {

            throw new InvalidCredentialsException(
                    "Invalid username or password"
            );
        }

        trainer.setPassword(newPassword);

        trainerDao.updateTrainer(trainer);

        logger.info("Trainer password changed successfully");
    }


    @Transactional
    public void activateTrainer(String username) {

        logger.info("Activating trainer with username {}", username);

        Trainer trainer =
                trainerDao.findTrainerByUsername(username);

        if (trainer == null) {
            throw new EntityNotFoundException("Trainer not found");
        }

        if (trainer.getIsActive()) {
            throw new ProfileStatusException("Trainer is already active");
        }

        trainer.setIsActive(true);

        trainerDao.updateTrainer(trainer);

        logger.info("Trainer activated successfully");
    }

    @Transactional
    public void deactivateTrainer(String username) {

        logger.info("Deactivating trainer with username {}", username);

        Trainer trainer =
                trainerDao.findTrainerByUsername(username);

        if (trainer == null) {
            throw new EntityNotFoundException("Trainer not found");
        }

        if (!trainer.getIsActive()) {
            throw new ProfileStatusException("Trainer is already inactive");
        }

        trainer.setIsActive(false);

        trainerDao.updateTrainer(trainer);

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
                trainerDao.findTrainerByUsername(username);

        if (trainer == null) {
            throw new EntityNotFoundException("Trainer not found");
        }

        return trainer;
    }
}