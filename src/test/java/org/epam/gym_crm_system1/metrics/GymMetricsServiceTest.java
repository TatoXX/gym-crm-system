package org.epam.gym_crm_system1.metrics;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GymMetricsServiceTest {

    private final SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
    private final GymMetricsService gymMetricsService = new GymMetricsService(meterRegistry);

    @Test
    void incrementTraineeRegistrationCount_ShouldIncreaseCounter() {
        gymMetricsService.incrementTraineeRegistrationCount();

        double count = meterRegistry
                .find("gym.trainee.registration.count")
                .counter()
                .count();

        assertEquals(1.0, count);
    }

    @Test
    void incrementTrainerRegistrationCount_ShouldIncreaseCounter() {
        gymMetricsService.incrementTrainerRegistrationCount();

        double count = meterRegistry
                .find("gym.trainer.registration.count")
                .counter()
                .count();

        assertEquals(1.0, count);
    }

    @Test
    void incrementTrainingCreationCount_ShouldIncreaseCounter() {
        gymMetricsService.incrementTrainingCreationCount();

        double count = meterRegistry
                .find("gym.training.creation.count")
                .counter()
                .count();

        assertEquals(1.0, count);
    }

    @Test
    void incrementLoginSuccessCount_ShouldIncreaseCounter() {
        gymMetricsService.incrementLoginSuccessCount();

        double count = meterRegistry
                .find("gym.login.success.count")
                .counter()
                .count();

        assertEquals(1.0, count);
    }

    @Test
    void incrementLoginFailureCount_ShouldIncreaseCounter() {
        gymMetricsService.incrementLoginFailureCount();

        double count = meterRegistry
                .find("gym.login.failure.count")
                .counter()
                .count();

        assertEquals(1.0, count);
    }
}