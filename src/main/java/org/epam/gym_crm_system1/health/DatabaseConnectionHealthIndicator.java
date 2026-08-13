package org.epam.gym_crm_system1.health;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("gymDatabase")
public class DatabaseConnectionHealthIndicator implements HealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionHealthIndicator.class);

    private final EntityManager entityManager;

    public DatabaseConnectionHealthIndicator(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Health health() {
        try {
            Object result = entityManager.createNativeQuery("SELECT 1").getSingleResult();

            logger.debug("Database health check passed");

            return Health.up()
                    .withDetail("database", "reachable")
                    .withDetail("validationQuery", "SELECT 1")
                    .withDetail("result", result)
                    .build();

        } catch (Exception exception) {
            logger.error("Database health check failed", exception);

            return Health.down()
                    .withDetail("database", "unreachable")
                    .withDetail("error", exception.getMessage())
                    .build();
        }
    }
}