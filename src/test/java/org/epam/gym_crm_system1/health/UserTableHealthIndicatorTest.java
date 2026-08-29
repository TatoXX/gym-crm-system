package org.epam.gym_crm_system1.health;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.springframework.boot.health.contributor.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserTableHealthIndicatorTest {

    @Test
    void health_WhenUsersTableExists_ShouldReturnUp() {
        EntityManager entityManager = mock(EntityManager.class);
        Query query = mock(Query.class);

        when(entityManager.createNativeQuery("SELECT COUNT(*) FROM users")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(3L);

        UserTableHealthIndicator indicator =
                new UserTableHealthIndicator(entityManager);

        assertEquals(Status.UP, indicator.health().getStatus());
    }

    @Test
    void health_WhenUsersTableUnavailable_ShouldReturnDown() {
        EntityManager entityManager = mock(EntityManager.class);

        when(entityManager.createNativeQuery("SELECT COUNT(*) FROM users"))
                .thenThrow(new RuntimeException("Table not found"));

        UserTableHealthIndicator indicator =
                new UserTableHealthIndicator(entityManager);

        assertEquals(Status.DOWN, indicator.health().getStatus());
    }
}