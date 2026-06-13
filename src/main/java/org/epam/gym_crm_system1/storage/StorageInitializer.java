package org.epam.gym_crm_system1.storage;

import jakarta.annotation.PostConstruct;
import org.epam.gym_crm_system1.model.TrainingType;
import org.epam.gym_crm_system1.model.Trainee;
import org.epam.gym_crm_system1.model.Trainer;
import org.epam.gym_crm_system1.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;

@Component
public class StorageInitializer {

    private static final Logger logger =
            LoggerFactory.getLogger(StorageInitializer.class);

    private final Storage storage;

    @Value("${trainees.file.path}")
    private String traineesFilePath;

    @Value("${trainers.file.path}")
    private String trainersFilePath;

    @Value("${trainings.file.path}")
    private String trainingsFilePath;

    public StorageInitializer(Storage storage) {
        this.storage = storage;
    }

    @PostConstruct
    public void initializeStorage() {

        logger.info("Initializing storage");

        loadTrainees();
        loadTrainers();
        loadTrainings();

        logger.info("Trainees loaded: {}",
                storage.getTrainees().size());

        logger.info("Trainers loaded: {}",
                storage.getTrainers().size());

        logger.info("Trainings loaded: {}",
                storage.getTrainings().size());

        logger.info("Storage initialized successfully");
    }

    private void loadTrainees() {

        logger.info("Loading trainees from file");

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(traineesFilePath))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");

                int id = Integer.parseInt(data[0]);

                String firstName = data[1];
                String lastName = data[2];
                String userName = data[3];
                String password = data[4];

                boolean isActive =
                        Boolean.parseBoolean(data[5]);

                String address = data[6];

                LocalDate dateOfBirth =
                        LocalDate.parse(data[7]);

                Trainee trainee =
                        new Trainee(firstName,
                                lastName,
                                address,
                                dateOfBirth);

                trainee.setId(id);
                trainee.setUserName(userName);
                trainee.setPassword(password);
                trainee.setIsActive(isActive);

                storage.getTrainees().put(id, trainee);
            }

            logger.info("Trainees loaded successfully");

        } catch (Exception e) {

            logger.error("Failed to load trainees: {}",
                    e.getMessage());
        }
    }

    private void loadTrainers() {

        logger.info("Loading trainers from file");

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(trainersFilePath))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");

                int id = Integer.parseInt(data[0]);

                String firstName = data[1];
                String lastName = data[2];
                String userName = data[3];
                String password = data[4];

                boolean isActive =
                        Boolean.parseBoolean(data[5]);

                String specializationName = data[6];

                Trainer trainer =
                        new Trainer(firstName,
                                lastName,
                                new TrainingType(0, specializationName));

                trainer.setId(id);
                trainer.setUserName(userName);
                trainer.setPassword(password);
                trainer.setIsActive(isActive);

                storage.getTrainers().put(id, trainer);
            }

            logger.info("Trainers loaded successfully");

        } catch (Exception e) {

            logger.error("Failed to load trainers: {}",
                    e.getMessage());
        }
    }

    private void loadTrainings() {

        logger.info("Loading trainings from file");

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(trainingsFilePath))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");

                int trainingId =
                        Integer.parseInt(data[0]);

                String trainingName = data[1];

                String specializationName = data[2];

                LocalDate trainingDate =
                        LocalDate.parse(data[3]);

                int duration =
                        Integer.parseInt(data[4]);

                int trainerId =
                        Integer.parseInt(data[5]);

                int traineeId =
                        Integer.parseInt(data[6]);

                Trainer trainer =
                        storage.getTrainers().get(trainerId);

                Trainee trainee =
                        storage.getTrainees().get(traineeId);

                if (trainer == null || trainee == null) {
                    logger.warn(
                            "Training with id {} was skipped because trainer or trainee was not found",
                            trainingId
                    );
                    continue;
                }

                Training training =
                        new Training(
                                trainingName,
                                new TrainingType(0, specializationName),
                                trainingDate,
                                duration,
                                trainer,
                                trainee
                        );

                training.setTrainingId(trainingId);

                storage.getTrainings()
                        .put(trainingId, training);
            }

            logger.info("Trainings loaded successfully");

        } catch (Exception e) {

            logger.error("Failed to load trainings: {}",
                    e.getMessage());
        }
    }
}