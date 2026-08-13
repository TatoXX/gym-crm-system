package org.epam.gym_crm_system1.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.epam.gym_crm_system1.model.TrainingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class TrainingTypeRepository {

    private static final Logger logger = LoggerFactory.getLogger(TrainingTypeRepository.class);

    @PersistenceContext
    private EntityManager entityManager;

    public TrainingType findTrainingTypeById(int id) {
        logger.info("Finding training type with id {}", id);
        TrainingType trainingType = entityManager.find(TrainingType.class, id);

        if (trainingType == null) {
            logger.warn("Training type with id {} not found", id);
        }

        return trainingType;
    }

    public Collection<TrainingType> findAllTrainingTypes() {
        logger.info("Finding all training types");

        return entityManager
                .createQuery("SELECT tt FROM TrainingType tt", TrainingType.class)
                .getResultList();
    }
}