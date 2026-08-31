package org.epam.trainerworkloadservice.repository;

import org.epam.trainerworkloadservice.model.TrainerWorkload;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TrainerWorkloadRepository
        extends MongoRepository<TrainerWorkload, String> {

    Optional<TrainerWorkload> findByTrainerUsername(
            String trainerUsername
    );

    List<TrainerWorkload> findByTrainerFirstNameAndTrainerLastName(
            String trainerFirstName,
            String trainerLastName
    );
}