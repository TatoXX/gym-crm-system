package org.epam.gym_crm_system1.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.epam.gym_crm_system1.model.Trainee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class TraineeRepository {

    private static final Logger logger =
            LoggerFactory.getLogger(TraineeRepository.class);

    @PersistenceContext
    private EntityManager entityManager;

    public void saveTrainee(Trainee trainee) {

        logger.info("Saving trainee with id {}",
                trainee.getId());

        entityManager.persist(trainee);

        logger.info("Trainee saved successfully");
    }

    public Trainee findTraineeById(int id) {

        logger.info("Finding trainee with id {}", id);

        Trainee trainee =
                entityManager.find(Trainee.class, id);

        if (trainee == null) {
            logger.warn("Trainee with id {} not found", id);
        }

        return trainee;
    }

    public Trainee findTraineeByUsername(String username) {

        logger.info("Finding trainee with username {}", username);

        try {
            return entityManager
                    .createQuery(
                            "SELECT t FROM Trainee t WHERE t.user.userName = :username",
                            Trainee.class
                    )
                    .setParameter("username", username)
                    .getSingleResult();

        } catch (NoResultException e) {
            logger.warn("Trainee with username {} not found", username);
            return null;
        }
    }

    public Collection<Trainee> findAllTrainees() {

        logger.info("Finding all trainees");

        return entityManager
                .createQuery("SELECT t FROM Trainee t", Trainee.class)
                .getResultList();
    }

    public Trainee updateTrainee(Trainee trainee) {

        logger.info("Updating trainee with id {}",
                trainee.getId());

        Trainee updatedTrainee =
                entityManager.merge(trainee);

        logger.info("Trainee updated successfully");

        return updatedTrainee;
    }

    public void deleteTraineeById(int id) {

        logger.info("Deleting trainee with id {}", id);

        Trainee trainee =
                entityManager.find(Trainee.class, id);

        if (trainee == null) {
            logger.warn("Trainee with id {} not found, delete skipped", id);
            return;
        }

        entityManager.remove(trainee);

        logger.info("Trainee deleted successfully");
    }
}