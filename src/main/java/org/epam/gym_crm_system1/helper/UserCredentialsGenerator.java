package org.epam.gym_crm_system1.helper;

import org.epam.gym_crm_system1.dao.TraineeDao;
import org.epam.gym_crm_system1.dao.TrainerDao;
import org.epam.gym_crm_system1.model.Trainee;
import org.epam.gym_crm_system1.model.Trainer;
import org.epam.gym_crm_system1.model.User;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserCredentialsGenerator {

    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;

    public UserCredentialsGenerator(TraineeDao traineeDao,
                                    TrainerDao trainerDao) {

        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
    }

    public String generateUsername(User user) {

        String baseUserName =
                user.getFirstName() + "." + user.getLastName();

        String candidate = baseUserName;

        int digit = 1;

        while (usernameExists(candidate)) {

            candidate = baseUserName + digit;

            digit++;
        }

        return candidate;
    }

    private boolean usernameExists(String username) {

        for (Trainee trainee : traineeDao.findAllTrainees()) {

            if (trainee.getUserName() != null
                    && trainee.getUserName().equals(username)) {

                return true;
            }                                               
        }

        for (Trainer trainer : trainerDao.getAllTrainers()) {

            if (trainer.getUserName() != null
                    && trainer.getUserName().equals(username)) {

                return true;
            }
        }

        return false;
    }


    public String generatePassword() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 10);
    }
}