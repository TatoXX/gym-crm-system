package org.epam.gym_crm_system1.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@";

    private static final String OTHER_SECRET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz#$";

    private static final long EXPIRATION_MS =
            3_600_000L;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {

        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "secret",
                SECRET
        );

        ReflectionTestUtils.setField(
                jwtService,
                "expirationMs",
                EXPIRATION_MS
        );
    }

    @Test
    void generate_ShouldCreateTokenContainingUsername() {

        String token =
                jwtService.generate(
                        "John.Smith"
                );

        assertNotNull(token);
        assertFalse(token.isBlank());

        assertEquals(
                "John.Smith",
                jwtService.extractUsername(token)
        );
    }

    @Test
    void isTokenValid_ShouldReturnTrueForMatchingUser() {

        String token =
                jwtService.generate(
                        "John.Smith"
                );

        UserDetails userDetails =
                User.withUsername("John.Smith")
                        .password("encoded-password")
                        .roles("TRAINEE")
                        .build();

        assertTrue(
                jwtService.isTokenValid(
                        token,
                        userDetails
                )
        );
    }

    @Test
    void isTokenValid_ShouldReturnFalseForDifferentUsername() {

        String token =
                jwtService.generate(
                        "John.Smith"
                );

        UserDetails anotherUser =
                User.withUsername("Jane.Smith")
                        .password("encoded-password")
                        .roles("TRAINER")
                        .build();

        assertFalse(
                jwtService.isTokenValid(
                        token,
                        anotherUser
                )
        );
    }

    @Test
    void tokenSignedWithDifferentSecret_ShouldBeRejected() {

        JwtService anotherJwtService =
                new JwtService();

        ReflectionTestUtils.setField(
                anotherJwtService,
                "secret",
                OTHER_SECRET
        );

        ReflectionTestUtils.setField(
                anotherJwtService,
                "expirationMs",
                EXPIRATION_MS
        );

        String token =
                anotherJwtService.generate(
                        "John.Smith"
                );

        assertThrows(
                JwtException.class,
                () -> jwtService.extractUsername(token)
        );
    }

    @Test
    void extractExpiration_ShouldUseConfiguredExpirationTime() {

        long beforeGeneration =
                System.currentTimeMillis();

        String token =
                jwtService.generate(
                        "John.Smith"
                );

        Date expiration =
                jwtService.extractExpiration(token);

        long afterGeneration =
                System.currentTimeMillis();

        long minimumExpected =
                beforeGeneration + EXPIRATION_MS - 2_000;

        long maximumExpected =
                afterGeneration + EXPIRATION_MS + 2_000;

        assertTrue(
                expiration.getTime()
                        >= minimumExpected
        );

        assertTrue(
                expiration.getTime()
                        <= maximumExpected
        );

        assertTrue(
                Duration.between(
                                new Date().toInstant(),
                                expiration.toInstant()
                        )
                        .toMinutes() >= 59
        );
    }
}