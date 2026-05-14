package org.epam.gym_crm_system1.service;

import org.epam.gym_crm_system1.helper.UserCredentialsGenerator;
import org.epam.gym_crm_system1.dao.TrainerDao;
import org.epam.gym_crm_system1.model.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

    public void createTrainer(Trainer trainer) {

        logger.info("Creating trainer with id {}",
                trainer.getUserId());

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

    public Trainer selectTrainerById(int id) {

        logger.info("Selecting trainer with id {}", id);

        return trainerDao.getTrainerById(id);
    }

    public void updateTrainer(Trainer trainer) {

        logger.info("Updating trainer with id {}",
                trainer.getUserId());

        trainerDao.updateTrainer(trainer);

        logger.info("Trainer updated successfully");
    }

    public Collection<Trainer> selectAllTrainers() {

        logger.info("Selecting all trainers");

        return trainerDao.getAllTrainers();
    }
}