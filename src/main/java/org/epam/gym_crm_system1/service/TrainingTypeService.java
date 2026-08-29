package org.epam.gym_crm_system1.service;

import org.epam.gym_crm_system1.exception.EntityNotFoundException;
import org.epam.gym_crm_system1.exception.ValidationException;
import org.epam.gym_crm_system1.model.TrainingType;
import org.epam.gym_crm_system1.repository.TrainingTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class TrainingTypeService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingTypeService.class);

    private final TrainingTypeRepository trainingTypeRepository;

    public TrainingTypeService(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Transactional(readOnly = true)
    public TrainingType findTrainingTypeById(Integer id) {
        if (id == null) {
            throw new ValidationException("Training type id is required");
        }

        logger.info("Selecting training type with id {}", id);

        TrainingType trainingType = trainingTypeRepository.findTrainingTypeById(id);

        if (trainingType == null) {
            throw new EntityNotFoundException("Training type not found");
        }

        return trainingType;
    }

    @Transactional(readOnly = true)
    public Collection<TrainingType> findAllTrainingTypes() {
        logger.info("Selecting all training types");
        return trainingTypeRepository.findAllTrainingTypes();
    }
}