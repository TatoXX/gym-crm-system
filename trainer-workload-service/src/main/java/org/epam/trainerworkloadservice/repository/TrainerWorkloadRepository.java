package org.epam.trainerworkloadservice.repository;

import org.epam.trainerworkloadservice.model.TrainerWorkload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainerWorkloadRepository
        extends JpaRepository<TrainerWorkload, Long> {

    Optional<TrainerWorkload> findByTrainerUsername(String trainerUsername);
}