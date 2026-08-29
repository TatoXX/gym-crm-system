package org.epam.gym_crm_system1.health;

import org.epam.gym_crm_system1.model.TrainingType;
import org.epam.gym_crm_system1.service.TrainingTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component("trainingTypes")
public class TrainingTypeHealthIndicator implements HealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(TrainingTypeHealthIndicator.class);

    private final TrainingTypeService trainingTypeService;

    public TrainingTypeHealthIndicator(TrainingTypeService trainingTypeService) {
        this.trainingTypeService = trainingTypeService;
    }

    @Override
    public Health health() {
        try {
            Collection<TrainingType> trainingTypes = trainingTypeService.findAllTrainingTypes();
            int count = trainingTypes == null ? 0 : trainingTypes.size();

            if (count == 0) {
                logger.warn("Training type health check found no training types");

                return Health.down()
                        .withDetail("trainingTypes", "missing")
                        .withDetail("count", 0)
                        .withDetail("reason", "Trainer registration requires at least one training type")
                        .build();
            }

            logger.debug("Training type health check passed. count={}", count);

            return Health.up()
                    .withDetail("trainingTypes", "available")
                    .withDetail("count", count)
                    .build();

        } catch (Exception exception) {
            logger.error("Training type health check failed", exception);

            return Health.down()
                    .withDetail("trainingTypes", "unavailable")
                    .withDetail("error", exception.getMessage())
                    .build();
        }
    }
}