package org.epam.gym_crm_system1.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTest {

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig =
                new SecurityConfig();
    }

    @Test
    void passwordEncoder_ShouldUseBCryptWithSalt() {

        PasswordEncoder passwordEncoder =
                securityConfig.passwordEncoder();

        assertInstanceOf(
                BCryptPasswordEncoder.class,
                passwordEncoder
        );

        String password =
                "password123";

        String firstHash =
                passwordEncoder.encode(password);

        String secondHash =
                passwordEncoder.encode(password);

        assertNotEquals(
                password,
                firstHash
        );

        assertNotEquals(
                password,
                secondHash
        );

        /*
         * Same password must produce different hashes
         * because BCrypt generates a random salt.
         */
        assertNotEquals(
                firstHash,
                secondHash
        );

        assertTrue(
                passwordEncoder.matches(
                        password,
                        firstHash
                )
        );

        assertTrue(
                passwordEncoder.matches(
                        password,
                        secondHash
                )
        );
    }

    @Test
    void corsConfiguration_ShouldContainRequiredConfiguration() {

        UrlBasedCorsConfigurationSource source =
                securityConfig
                        .corsConfigurationSource();

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setRequestURI(
                "/api/trainees"
        );

        request.setServletPath(
                "/api/trainees"
        );

        CorsConfiguration configuration =
                source.getCorsConfiguration(
                        request
                );

        assertNotNull(configuration);

        assertNotNull(
                configuration
                        .getAllowedOriginPatterns()
        );

        assertTrue(
                configuration
                        .getAllowedOriginPatterns()
                        .contains("*")
        );

        assertNotNull(
                configuration
                        .getAllowedMethods()
        );

        assertTrue(
                configuration
                        .getAllowedMethods()
                        .containsAll(
                                List.of(
                                        "GET",
                                        "POST",
                                        "PUT",
                                        "DELETE",
                                        "OPTIONS"
                                )
                        )
        );

        assertNotNull(
                configuration
                        .getAllowedHeaders()
        );

        assertTrue(
                configuration
                        .getAllowedHeaders()
                        .contains(
                                "Authorization"
                        )
        );

        assertTrue(
                configuration
                        .getAllowedHeaders()
                        .contains(
                                "Content-Type"
                        )
        );
    }
}