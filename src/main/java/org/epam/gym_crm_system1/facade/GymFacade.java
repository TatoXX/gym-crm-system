package org.epam.gym_crm_system1.facade;

import org.epam.gym_crm_system1.model.Trainee;
import org.epam.gym_crm_system1.model.Trainer;
import org.epam.gym_crm_system1.model.Training;
import org.epam.gym_crm_system1.service.TraineeService;
import org.epam.gym_crm_system1.service.TrainerService;
import org.epam.gym_crm_system1.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class GymFacade {

    private static final Logger logger =
            LoggerFactory.getLogger(GymFacade.class);

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public GymFacade(
            TraineeService traineeService,
            TrainerService trainerService,
            TrainingService trainingService
    ) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    public void createTrainee(Trainee trainee) {

        logger.info("Facade request: create trainee");

        traineeService.createTrainee(trainee);
    }

    public Trainee selectTraineeById(int id) {

        logger.info("Facade request: select trainee with id {}", id);

        return traineeService.selectTraineeById(id);
    }

    public void updateTrainee(Trainee trainee) {

        logger.info("Facade request: update trainee with id {}",
                trainee.getId());

        traineeService.updateTrainee(trainee);
    }

    public void deleteTraineeById(int id) {

        logger.info("Facade request: delete trainee with id {}", id);

        traineeService.deleteTraineeById(id);
    }

    public Collection<Trainee> selectAllTrainees() {

        logger.info("Facade request: select all trainees");

        return traineeService.selectAllTrainees();
    }

    public void createTrainer(Trainer trainer) {

        logger.info("Facade request: create trainer");

        trainerService.createTrainer(trainer);
    }

    public Trainer selectTrainerById(int id) {

        logger.info("Facade request: select trainer with id {}", id);

        return trainerService.selectTrainerById(id);
    }
    public void updateTrainer(Trainer trainer) {
        logger.info("Facade request: update trainer with id {}",
                trainer.getId());

        trainerService.updateTrainer(trainer);
    }

    public Collection<Trainer> selectAllTrainers() {

        logger.info("Facade request: select all trainers");

        return trainerService.selectAllTrainers();
    }

    public void createTraining(Training training) {

        logger.info("Facade request: create training");

        trainingService.createTraining(training);
    }

    public Training selectTrainingById(int id) {

        logger.info("Facade request: select training with id {}", id);

        return trainingService.selectTrainingById(id);
    }

    public Collection<Training> selectAllTrainings() {

        logger.info("Facade request: select all trainings");

        return trainingService.selectAllTrainings();
    }
}