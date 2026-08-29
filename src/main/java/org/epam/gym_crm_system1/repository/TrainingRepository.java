package org.epam.gym_crm_system1.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.epam.gym_crm_system1.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;

@Repository
public class TrainingRepository {

    private static final Logger logger =
            LoggerFactory.getLogger(TrainingRepository.class);

    @PersistenceContext
    private EntityManager entityManager;

    public void saveTraining(Training training) {

        logger.info("Saving training with id {}",
                training.getTrainingId());

        entityManager.persist(training);

        logger.info("Training saved successfully");
    }

    public Training getTrainingById(int trainingId) {

        logger.info("Finding training with id {}",
                trainingId);

        Training training =
                entityManager.find(Training.class, trainingId);

        if (training == null) {
            logger.warn("Training with id {} not found",
                    trainingId);
        }

        return training;
    }

    public Collection<Training> getAllTrainings() {

        logger.info("Finding all trainings");

        return entityManager
                .createQuery("SELECT t FROM Training t", Training.class)
                .getResultList();
    }

    public Collection<Training> findTrainingsByTraineeUsernameAndCriteria(
            String traineeUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerUsername,
            String trainingTypeName
    ) {

        logger.info("Finding trainings for trainee username {}", traineeUsername);

        String jpql =
                "SELECT tr FROM Training tr " +
                        "WHERE tr.trainee.user.userName = :traineeUsername ";

        if (fromDate != null) {
            jpql += "AND tr.trainingDate >= :fromDate ";
        }

        if (toDate != null) {
            jpql += "AND tr.trainingDate <= :toDate ";
        }

        if (trainerUsername != null && !trainerUsername.isBlank()) {
            jpql += "AND tr.trainer.user.userName = :trainerUsername ";
        }

        if (trainingTypeName != null && !trainingTypeName.isBlank()) {
            jpql += "AND tr.trainingType.name = :trainingTypeName ";
        }

        var query =
                entityManager.createQuery(jpql, Training.class);

        query.setParameter("traineeUsername", traineeUsername);

        if (fromDate != null) {
            query.setParameter("fromDate", fromDate);
        }

        if (toDate != null) {
            query.setParameter("toDate", toDate);
        }

        if (trainerUsername != null && !trainerUsername.isBlank()) {
            query.setParameter("trainerUsername", trainerUsername.trim());
        }

        if (trainingTypeName != null && !trainingTypeName.isBlank()) {
            query.setParameter("trainingTypeName", trainingTypeName.trim());
        }

        return query.getResultList();
    }

    public Collection<Training> findTrainingsByTrainerUsernameAndCriteria(
            String trainerUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName
    ) {

        logger.info("Finding trainings for trainer username {}", trainerUsername);

        String jpql =
                "SELECT tr FROM Training tr " +
                        "WHERE tr.trainer.user.userName = :trainerUsername ";

        if (fromDate != null) {
            jpql += "AND tr.trainingDate >= :fromDate ";
        }

        if (toDate != null) {
            jpql += "AND tr.trainingDate <= :toDate ";
        }

        if (traineeName != null && !traineeName.isBlank()) {
            jpql += "AND (tr.trainee.user.firstName = :traineeName " +
                    "OR tr.trainee.user.lastName = :traineeName) ";
        }

        var query =
                entityManager.createQuery(jpql, Training.class);

        query.setParameter("trainerUsername", trainerUsername);

        if (fromDate != null) {
            query.setParameter("fromDate", fromDate);
        }

        if (toDate != null) {
            query.setParameter("toDate", toDate);
        }

        if (traineeName != null && !traineeName.isBlank()) {
            query.setParameter("traineeName", traineeName.trim());
        }

        return query.getResultList();
    }
}