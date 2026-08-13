package org.epam.gym_crm_system1.health;

import org.epam.gym_crm_system1.model.TrainingType;
import org.epam.gym_crm_system1.service.TrainingTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.health.contributor.Status;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TrainingTypeHealthIndicatorTest {

    @Test
    void health_WhenTrainingTypesExist_ShouldReturnUp() {
        TrainingTypeService trainingTypeService = mock(TrainingTypeService.class);

        TrainingType trainingType = new TrainingType();
        trainingType.setId(1);
        trainingType.setName("Fitness");

        when(trainingTypeService.findAllTrainingTypes())
                .thenReturn(List.of(trainingType));

        TrainingTypeHealthIndicator indicator =
                new TrainingTypeHealthIndicator(trainingTypeService);

        assertEquals(Status.UP, indicator.health().getStatus());
    }

    @Test
    void health_WhenTrainingTypesMissing_ShouldReturnDown() {
        TrainingTypeService trainingTypeService = mock(TrainingTypeService.class);

        when(trainingTypeService.findAllTrainingTypes())
                .thenReturn(List.of());

        TrainingTypeHealthIndicator indicator =
                new TrainingTypeHealthIndicator(trainingTypeService);

        assertEquals(Status.DOWN, indicator.health().getStatus());
    }

    @Test
    void health_WhenServiceFails_ShouldReturnDown() {
        TrainingTypeService trainingTypeService = mock(TrainingTypeService.class);

        when(trainingTypeService.findAllTrainingTypes())
                .thenThrow(new RuntimeException("Service error"));

        TrainingTypeHealthIndicator indicator =
                new TrainingTypeHealthIndicator(trainingTypeService);

        assertEquals(Status.DOWN, indicator.health().getStatus());
    }
}