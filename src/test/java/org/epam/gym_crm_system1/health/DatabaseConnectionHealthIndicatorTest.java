package org.epam.gym_crm_system1.health;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.springframework.boot.health.contributor.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DatabaseConnectionHealthIndicatorTest {

    @Test
    void health_WhenDatabaseReachable_ShouldReturnUp() {
        EntityManager entityManager = mock(EntityManager.class);
        Query query = mock(Query.class);

        when(entityManager.createNativeQuery("SELECT 1")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(1);

        DatabaseConnectionHealthIndicator indicator =
                new DatabaseConnectionHealthIndicator(entityManager);

        assertEquals(Status.UP, indicator.health().getStatus());
    }

    @Test
    void health_WhenDatabaseUnavailable_ShouldReturnDown() {
        EntityManager entityManager = mock(EntityManager.class);

        when(entityManager.createNativeQuery("SELECT 1"))
                .thenThrow(new RuntimeException("Database error"));

        DatabaseConnectionHealthIndicator indicator =
                new DatabaseConnectionHealthIndicator(entityManager);

        assertEquals(Status.DOWN, indicator.health().getStatus());
    }
}