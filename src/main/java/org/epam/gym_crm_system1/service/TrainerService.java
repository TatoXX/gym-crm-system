package org.epam.gym_crm_system1.service;

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
}