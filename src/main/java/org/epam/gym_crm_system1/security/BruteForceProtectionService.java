package org.epam.gym_crm_system1.security;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class BruteForceProtectionService {

    private static final int MAX_ATTEMPTS = 3;
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(5);

    private final Map<String, Integer> failedAttempts = new ConcurrentHashMap<>();

    private final Map<String, Instant> blockedUntil = new ConcurrentHashMap<>();

    public boolean isBlocked(String username) {
        Instant blockExpiration = blockedUntil.get(username);
        if (blockExpiration == null) {
            return false;
        }

        if (Instant.now().isAfter(blockExpiration)) {
            blockedUntil.remove(username);
            failedAttempts.remove(username);
            return false;
        }

        return true;          
    }

    public void loginFailed(String username) {
        int attempts = failedAttempts.merge(username, 1, Integer::sum);

        if (attempts >= MAX_ATTEMPTS) {
            blockedUntil.put(username, Instant.now().plus(BLOCK_DURATION));
        }
    }

    public void loginSucceeded(String username) {
        failedAttempts.remove(username);
        blockedUntil.remove(username);
    }
}
