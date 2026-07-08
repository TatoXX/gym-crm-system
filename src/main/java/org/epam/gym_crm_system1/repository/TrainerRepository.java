package org.epam.gym_crm_system1.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.epam.gym_crm_system1.model.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class TrainerRepository {

    private static final Logger logger =
            LoggerFactory.getLogger(TrainerRepository.class);

    @PersistenceContext
    private EntityManager entityManager;

    public void saveTrainer(Trainer trainer) {

        logger.info("Saving trainer with id {}",
                trainer.getId());

        entityManager.persist(trainer);

        logger.info("Trainer saved successfully");
    }

    public void updateTrainer(Trainer trainer) {

        logger.info("Updating trainer with id {}",
                trainer.getId());

        entityManager.merge(trainer);

        logger.info("Trainer updated successfully");
    }

    public Trainer getTrainerById(int trainerId) {

        logger.info("Finding trainer with id {}", trainerId);

        Trainer trainer =
                entityManager.find(Trainer.class, trainerId);

        if (trainer == null) {
            logger.warn("Trainer with id {} not found",
                    trainerId);
        }

        return trainer;
    }

    public Trainer findTrainerByUsername(String username) {

        logger.info("Finding trainer with username {}", username);

        try {
            return entityManager
                    .createQuery(
                            "SELECT t FROM Trainer t WHERE t.user.userName = :username",
                            Trainer.class
                    )
                    .setParameter("username", username)
                    .getSingleResult();

        } catch (NoResultException e) {
            logger.warn("Trainer with username {} not found", username);
            return null;
        }
    }

    public Collection<Trainer> getAllTrainers() {

        logger.info("Finding all trainers");

        return entityManager
                .createQuery("SELECT t FROM Trainer t", Trainer.class)
                .getResultList();
    }

    public Collection<Trainer> findTrainersNotAssignedToTrainee(
            String traineeUsername
    ) {

        logger.info("Finding trainers not assigned to trainee username {}",
                traineeUsername);

        return entityManager
                .createQuery(
                        "SELECT trainer FROM Trainer trainer " +
                                "WHERE trainer NOT IN (" +
                                "SELECT assignedTrainer FROM Trainee trainee " +
                                "JOIN trainee.trainers assignedTrainer " +
                                "WHERE trainee.user.userName = :traineeUsername" +
                                ")",
                        Trainer.class
                )
                .setParameter("traineeUsername", traineeUsername)
                .getResultList();
    }
}