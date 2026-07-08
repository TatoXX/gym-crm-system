package org.epam.gym_crm_system1.repository;

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
class TrainerRepositoryTest {

    @Autowired
    private TrainerRepository trainerRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldSaveTrainer() {

        TrainingType trainingType =
                new TrainingType("FitnessRepositorySave");

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

        trainerRepository.saveTrainer(trainer);

        Trainer savedTrainer =
                trainerRepository.getTrainerById(trainer.getId());

        assertNotNull(savedTrainer);

        assertEquals("John",
                savedTrainer.getFirstName());

        assertEquals("FitnessRepositorySave",
                savedTrainer.getTrainingType().getName());
    }

    @Test
    void shouldUpdateTrainer() {

        TrainingType fitness =
                new TrainingType("FitnessRepositoryUpdate");

        TrainingType yoga =
                new TrainingType("YogaRepositoryUpdate");

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

        trainerRepository.saveTrainer(trainer);

        trainer.setTrainingType(yoga);

        trainerRepository.updateTrainer(trainer);

        Trainer updatedTrainer =
                trainerRepository.getTrainerById(trainer.getId());

        assertNotNull(updatedTrainer);

        assertEquals("YogaRepositoryUpdate",
                updatedTrainer.getTrainingType().getName());
    }
}