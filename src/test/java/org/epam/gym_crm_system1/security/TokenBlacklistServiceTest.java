package org.epam.gym_crm_system1.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private TokenBlacklistService tokenBlacklistService;

    @Test
    void tokenShouldNotBeBlacklistedInitially() {

        assertFalse(
                tokenBlacklistService.isBlacklisted(
                        "token"
                )
        );
    }

    @Test
    void blacklist_ShouldBlacklistTokenUntilExpiration() {

        String token = "valid-token";

        Date futureExpiration =
                new Date(
                        System.currentTimeMillis()
                                + 60_000
                );

        when(
                jwtService.extractExpiration(token)
        ).thenReturn(futureExpiration);

        tokenBlacklistService.blacklist(token);

        assertTrue(
                tokenBlacklistService
                        .isBlacklisted(token)
        );

        verify(jwtService)
                .extractExpiration(token);
    }

    @Test
    void expiredBlacklistedToken_ShouldNoLongerBeBlacklisted() {

        String token = "expired-token";

        Date pastExpiration =
                new Date(
                        System.currentTimeMillis()
                                - 60_000
                );

        when(
                jwtService.extractExpiration(token)
        ).thenReturn(pastExpiration);

        tokenBlacklistService.blacklist(token);

        assertFalse(
                tokenBlacklistService
                        .isBlacklisted(token)
        );

        assertFalse(
                tokenBlacklistService
                        .isBlacklisted(token)
        );
    }

    @Test
    void blacklist_ShouldUseJwtExpiration() {

        String token = "another-token";

        Date expiration =
                new Date(
                        System.currentTimeMillis()
                                + 300_000
                );

        when(
                jwtService.extractExpiration(token)
        ).thenReturn(expiration);

        tokenBlacklistService.blacklist(token);

        verify(
                jwtService,
                times(1)
        ).extractExpiration(token);

        assertTrue(
                tokenBlacklistService
                        .isBlacklisted(token)
        );
    }
}