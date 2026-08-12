package org.epam.gym_crm_system1.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BruteForceProtectionServiceTest {

    private BruteForceProtectionService bruteForceProtectionService;

    @BeforeEach
    void setUp() {
        bruteForceProtectionService =
                new BruteForceProtectionService();
    }

    @Test
    void userShouldNotBeBlockedInitially() {

        assertFalse(
                bruteForceProtectionService.isBlocked(
                        "John.Smith"
                )
        );
    }

    @Test
    void userShouldNotBeBlockedAfterOneFailedLogin() {

        bruteForceProtectionService.loginFailed(
                "John.Smith"
        );

        assertFalse(
                bruteForceProtectionService.isBlocked(
                        "John.Smith"
                )
        );
    }

    @Test
    void userShouldNotBeBlockedAfterTwoFailedLogins() {

        bruteForceProtectionService.loginFailed(
                "John.Smith"
        );

        bruteForceProtectionService.loginFailed(
                "John.Smith"
        );

        assertFalse(
                bruteForceProtectionService.isBlocked(
                        "John.Smith"
                )
        );
    }

    @Test
    void userShouldBeBlockedAfterThreeFailedLogins() {

        bruteForceProtectionService.loginFailed(
                "John.Smith"
        );

        bruteForceProtectionService.loginFailed(
                "John.Smith"
        );

        bruteForceProtectionService.loginFailed(
                "John.Smith"
        );

        assertTrue(
                bruteForceProtectionService.isBlocked(
                        "John.Smith"
                )
        );
    }

    @Test
    void successfulLoginShouldClearBlockedState() {

        bruteForceProtectionService.loginFailed(
                "John.Smith"
        );

        bruteForceProtectionService.loginFailed(
                "John.Smith"
        );

        bruteForceProtectionService.loginFailed(
                "John.Smith"
        );

        assertTrue(
                bruteForceProtectionService.isBlocked(
                        "John.Smith"
                )
        );

        bruteForceProtectionService.loginSucceeded(
                "John.Smith"
        );

        assertFalse(
                bruteForceProtectionService.isBlocked(
                        "John.Smith"
                )
        );
    }

    @Test
    void successfulLoginShouldResetFailedAttemptCounter() {

        bruteForceProtectionService.loginFailed(
                "John.Smith"
        );

        bruteForceProtectionService.loginFailed(
                "John.Smith"
        );

        bruteForceProtectionService.loginSucceeded(
                "John.Smith"
        );

        bruteForceProtectionService.loginFailed(
                "John.Smith"
        );

        bruteForceProtectionService.loginFailed(
                "John.Smith"
        );

        assertFalse(
                bruteForceProtectionService.isBlocked(
                        "John.Smith"
                )
        );
    }

    @Test
    void failedAttemptsShouldBeTrackedSeparatelyForDifferentUsers() {

        bruteForceProtectionService.loginFailed(
                "John.Smith"
        );

        bruteForceProtectionService.loginFailed(
                "John.Smith"
        );

        bruteForceProtectionService.loginFailed(
                "John.Smith"
        );

        bruteForceProtectionService.loginFailed(
                "Jane.Smith"
        );

        assertTrue(
                bruteForceProtectionService.isBlocked(
                        "John.Smith"
                )
        );

        assertFalse(
                bruteForceProtectionService.isBlocked(
                        "Jane.Smith"
                )
        );
    }

    @Test
    void blockShouldLastApproximatelyFiveMinutes() {

        Instant beforeBlock = Instant.now();

        bruteForceProtectionService.loginFailed(
                "John.Smith"
        );

        bruteForceProtectionService.loginFailed(
                "John.Smith"
        );

        bruteForceProtectionService.loginFailed(
                "John.Smith"
        );

        Map<String, Instant> blockedUntil =
                getBlockedUntilMap();

        Instant expiration =
                blockedUntil.get("John.Smith");

        assertNotNull(expiration);

        Duration duration =
                Duration.between(
                        beforeBlock,
                        expiration
                );

        assertTrue(
                duration.compareTo(
                        Duration.ofMinutes(4)
                                .plusSeconds(50)
                ) >= 0
        );

        assertTrue(
                duration.compareTo(
                        Duration.ofMinutes(5)
                                .plusSeconds(10)
                ) <= 0
        );
    }

    @Test
    void expiredBlockShouldResetAttemptsAndAllowFreshThreeAttempts() {

        bruteForceProtectionService.loginFailed(
                "John.Smith"
        );

        bruteForceProtectionService.loginFailed(
                "John.Smith"
        );

        bruteForceProtectionService.loginFailed(
                "John.Smith"
        );

        assertTrue(
                bruteForceProtectionService.isBlocked(
                        "John.Smith"
                )
        );

        Map<String, Instant> blockedUntil =
                getBlockedUntilMap();

        blockedUntil.put(
                "John.Smith",
                Instant.now().minusSeconds(1)
        );

        assertFalse(
                bruteForceProtectionService.isBlocked(
                        "John.Smith"
                )
        );

        // After the old block expires, this must be
        // treated as the FIRST new failed attempt.
        bruteForceProtectionService.loginFailed(
                "John.Smith"
        );

        assertFalse(
                bruteForceProtectionService.isBlocked(
                        "John.Smith"
                )
        );

        bruteForceProtectionService.loginFailed(
                "John.Smith"
        );

        assertFalse(
                bruteForceProtectionService.isBlocked(
                        "John.Smith"
                )
        );

        bruteForceProtectionService.loginFailed(
                "John.Smith"
        );

        assertTrue(
                bruteForceProtectionService.isBlocked(
                        "John.Smith"
                )
        );
    }

    @SuppressWarnings("unchecked")
    private Map<String, Instant> getBlockedUntilMap() {

        return (Map<String, Instant>)
                ReflectionTestUtils.getField(
                        bruteForceProtectionService,
                        "blockedUntil"
                );
    }
}