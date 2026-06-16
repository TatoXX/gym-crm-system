package org.epam.gym_crm_system1.service;

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
}