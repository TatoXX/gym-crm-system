package org.epam.gym_crm_system1.health;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("userTable")
public class UserTableHealthIndicator implements HealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(UserTableHealthIndicator.class);

    private final EntityManager entityManager;

    public UserTableHealthIndicator(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Health health() {
        try {
            Number userCount = (Number) entityManager
                    .createNativeQuery("SELECT COUNT(*) FROM users")
                    .getSingleResult();

            logger.debug("User table health check passed. userCount={}", userCount.longValue());

            return Health.up()
                    .withDetail("table", "users")
                    .withDetail("status", "available")
                    .withDetail("userCount", userCount.longValue())
                    .build();

        } catch (Exception exception) {
            logger.error("User table health check failed", exception);

            return Health.down()
                    .withDetail("table", "users")
                    .withDetail("status", "unavailable")
                    .withDetail("error", exception.getMessage())
                    .build();
        }
    }
}