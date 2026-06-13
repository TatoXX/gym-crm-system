package org.epam.gym_crm_system1.facade;

import org.epam.gym_crm_system1.dao.TraineeDao;
import org.epam.gym_crm_system1.dao.TrainerDao;
import org.epam.gym_crm_system1.dao.TrainingDao;
import org.epam.gym_crm_system1.helper.UserCredentialsGenerator;
import org.epam.gym_crm_system1.model.Trainee;
import org.epam.gym_crm_system1.service.TraineeService;
import org.epam.gym_crm_system1.service.TrainerService;
import org.epam.gym_crm_system1.service.TrainingService;
import org.epam.gym_crm_system1.storage.Storage;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class GymFacadeTest {

    @Test
    void shouldCreateTraineeThroughFacade() {

        Storage storage = new Storage();

        TraineeDao traineeDao =
                new TraineeDao(storage);

        TrainerDao trainerDao =
                new TrainerDao(storage);

        TrainingDao trainingDao =
                new TrainingDao(storage);

        UserCredentialsGenerator generator =
                new UserCredentialsGenerator(
                        traineeDao,
                        trainerDao
                );

        TraineeService traineeService =
                new TraineeService(
                        traineeDao,
                        generator
                );

        TrainerService trainerService =
                new TrainerService(
                        trainerDao,
                        generator
                );

        TrainingService trainingService =
                new TrainingService(trainingDao);

        GymFacade facade =
                new GymFacade(
                        traineeService,
                        trainerService,
                        trainingService
                );

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        trainee.setId(1);

        facade.createTrainee(trainee);

        assertNotNull(
                facade.selectTraineeById(1)
        );
    }
}