package org.epam.trainerworkloadservice.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET =
            "0123456789012345678901234567890123456789";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {

        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "secret",
                SECRET
        );
    }

    @Test
    void shouldExtractUsernameFromValidToken() {

        String token = createToken(
                "Test.Trainer",
                new Date(
                        System.currentTimeMillis()
                                + 60_000
                )
        );

        String username =
                jwtService.extractUsername(token);

        assertEquals(
                "Test.Trainer",
                username
        );
    }

    @Test
    void shouldExtractExpirationFromValidToken() {

        Date expiration =
                new Date(
                        System.currentTimeMillis()
                                + 60_000
                );

        String token = createToken(
                "Test.Trainer",
                expiration
        );

        Date extractedExpiration =
                jwtService.extractExpiration(token);

        assertEquals(
                expiration.getTime() / 1000,
                extractedExpiration.getTime() / 1000
        );
    }

    @Test
    void shouldReturnTrueForValidToken() {

        String token = createToken(
                "Test.Trainer",
                new Date(
                        System.currentTimeMillis()
                                + 60_000
                )
        );

        assertTrue(
                jwtService.isTokenValid(token)
        );
    }

    @Test
    void shouldReturnFalseWhenUsernameIsBlank() {

        String token = createToken(
                " ",
                new Date(
                        System.currentTimeMillis()
                                + 60_000
                )
        );

        assertFalse(
                jwtService.isTokenValid(token)
        );
    }

    @Test
    void shouldThrowExceptionForExpiredToken() {

        String token = createToken(
                "Test.Trainer",
                new Date(
                        System.currentTimeMillis()
                                - 60_000
                )
        );

        assertThrows(
                JwtException.class,
                () -> jwtService.isTokenValid(token)
        );
    }

    @Test
    void shouldThrowExceptionForTokenSignedWithDifferentSecret() {

        String differentSecret =
                "9999999999999999999999999999999999999999";

        SecretKey differentKey =
                Keys.hmacShaKeyFor(
                        differentSecret.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        String token =
                Jwts.builder()
                        .subject("Test.Trainer")
                        .issuedAt(new Date())
                        .expiration(
                                new Date(
                                        System.currentTimeMillis()
                                                + 60_000
                                )
                        )
                        .signWith(differentKey)
                        .compact();

        assertThrows(
                JwtException.class,
                () -> jwtService.isTokenValid(token)
        );
    }

    private String createToken(
            String username,
            Date expiration) {

        SecretKey key =
                Keys.hmacShaKeyFor(
                        SECRET.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(expiration)
                .signWith(key)
                .compact();
    }
}