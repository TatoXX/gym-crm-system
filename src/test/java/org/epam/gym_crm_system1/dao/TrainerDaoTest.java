package org.epam.gym_crm_system1.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.epam.gym_crm_system1.model.TrainingType;
import org.epam.gym_crm_system1.model.Trainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TrainerDaoTest {

    @Autowired
    private TrainerDao trainerDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldSaveTrainer() {

        TrainingType trainingType =
                new TrainingType("FitnessDaoSave");

        entityManager.persist(trainingType);

        Trainer trainer =
                new Trainer(
                        "John",
                        "Smith",
                        trainingType
                );

        trainer.setUserName("John.Smith");
        trainer.setPassword("password123");
        trainer.setIsActive(true);

        trainerDao.saveTrainer(trainer);

        Trainer savedTrainer =
                trainerDao.getTrainerById(trainer.getId());

        assertNotNull(savedTrainer);
        assertEquals("John", savedTrainer.getFirstName());
        assertEquals("FitnessDaoSave",
                savedTrainer.getTrainingType().getName());
    }

    @Test
    void shouldUpdateTrainer() {

        TrainingType fitness =
                new TrainingType("FitnessDaoUpdate");

        TrainingType yoga =
                new TrainingType("YogaDaoUpdate");

        entityManager.persist(fitness);
        entityManager.persist(yoga);

        Trainer trainer =
                new Trainer(
                        "John",
                        "Smith",
                        fitness
                );

        trainer.setUserName("John.Smith");
        trainer.setPassword("password123");
        trainer.setIsActive(true);

        trainerDao.saveTrainer(trainer);

        trainer.setTrainingType(yoga);

        trainerDao.updateTrainer(trainer);

        Trainer updatedTrainer =
                trainerDao.getTrainerById(trainer.getId());

        assertNotNull(updatedTrainer);
        assertEquals("YogaDaoUpdate",
                updatedTrainer.getTrainingType().getName());
    }
}