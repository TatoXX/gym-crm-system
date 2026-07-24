package org.epam.gym_crm_system1.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GymMetricsService {

    private static final Logger logger = LoggerFactory.getLogger(GymMetricsService.class);

    private final Counter traineeRegistrationCounter;
    private final Counter trainerRegistrationCounter;
    private final Counter trainingCreationCounter;
    private final Counter loginSuccessCounter;
    private final Counter loginFailureCounter;

    public GymMetricsService(MeterRegistry meterRegistry) {
        this.traineeRegistrationCounter = Counter.builder("gym.trainee.registration.count")
                .description("Number of successfully registered trainees")
                .register(meterRegistry);

        this.trainerRegistrationCounter = Counter.builder("gym.trainer.registration.count")
                .description("Number of successfully registered trainers")
                .register(meterRegistry);

        this.trainingCreationCounter = Counter.builder("gym.training.creation.count")
                .description("Number of successfully created trainings")
                .register(meterRegistry);

        this.loginSuccessCounter = Counter.builder("gym.login.success.count")
                .description("Number of successful login attempts")
                .register(meterRegistry);

        this.loginFailureCounter = Counter.builder("gym.login.failure.count")
                .description("Number of failed login attempts")
                .register(meterRegistry);
    }

    public void incrementTraineeRegistrationCount() {
        traineeRegistrationCounter.increment();
        logger.debug("Trainee registration metric incremented");
    }

    public void incrementTrainerRegistrationCount() {
        trainerRegistrationCounter.increment();
        logger.debug("Trainer registration metric incremented");
    }

    public void incrementTrainingCreationCount() {
        trainingCreationCounter.increment();
        logger.debug("Training creation metric incremented");
    }

    public void incrementLoginSuccessCount() {
        loginSuccessCounter.increment();
        logger.debug("Login success metric incremented");
    }

    public void incrementLoginFailureCount() {
        loginFailureCounter.increment();
        logger.debug("Login failure metric incremented");
    }
}