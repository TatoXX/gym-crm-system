package org.epam.gym_crm_system1.service;

import org.epam.gym_crm_system1.model.TrainingType;
import org.epam.gym_crm_system1.repository.TrainingTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.epam.gym_crm_system1.exception.EntityNotFoundException;
import org.epam.gym_crm_system1.exception.ValidationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingTypeService trainingTypeService;

    @Test
    void findTrainingTypeById_WhenIdExists_ShouldReturnTrainingType() {
        TrainingType trainingType = new TrainingType();
        trainingType.setId(1);
        trainingType.setName("Fitness");

        when(trainingTypeRepository.findTrainingTypeById(1))
                .thenReturn(trainingType);

        TrainingType result = trainingTypeService.findTrainingTypeById(1);

        assertEquals(1, result.getId());
        assertEquals("Fitness", result.getName());
    }

    @Test
    void findTrainingTypeById_WhenIdIsNull_ShouldThrowValidationException() {
        assertThrows(
                ValidationException.class,
                () -> trainingTypeService.findTrainingTypeById(null)
        );
    }

    @Test
    void findTrainingTypeById_WhenTrainingTypeNotFound_ShouldThrowEntityNotFoundException() {
        when(trainingTypeRepository.findTrainingTypeById(99))
                .thenReturn(null);

        assertThrows(
                EntityNotFoundException.class,
                () -> trainingTypeService.findTrainingTypeById(99)
        );
    }

    @Test
    void findAllTrainingTypes_ShouldReturnAllTrainingTypes() {
        TrainingType fitness = new TrainingType();
        fitness.setId(1);
        fitness.setName("Fitness");

        TrainingType yoga = new TrainingType();
        yoga.setId(2);
        yoga.setName("Yoga");

        when(trainingTypeRepository.findAllTrainingTypes())
                .thenReturn(List.of(fitness, yoga));

        List<TrainingType> result = trainingTypeService.findAllTrainingTypes()
                .stream()
                .toList();

        assertEquals(2, result.size());
        assertEquals("Fitness", result.get(0).getName());
        assertEquals("Yoga", result.get(1).getName());
    }
}