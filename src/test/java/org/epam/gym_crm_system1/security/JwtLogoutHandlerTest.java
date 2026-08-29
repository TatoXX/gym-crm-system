package org.epam.gym_crm_system1.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtLogoutHandlerTest {

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    private JwtLogoutHandler jwtLogoutHandler;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {

        jwtLogoutHandler =
                new JwtLogoutHandler(
                        tokenBlacklistService
                );

        request =
                new MockHttpServletRequest();

        response =
                new MockHttpServletResponse();
    }

    @Test
    void logoutWithoutAuthorizationHeader_ShouldDoNothing() {

        jwtLogoutHandler.logout(
                request,
                response,
                null
        );

        verifyNoInteractions(
                tokenBlacklistService
        );
    }

    @Test
    void logoutWithNonBearerHeader_ShouldDoNothing() {

        request.addHeader(
                HttpHeaders.AUTHORIZATION,
                "Basic abc123"
        );

        jwtLogoutHandler.logout(
                request,
                response,
                null
        );

        verifyNoInteractions(
                tokenBlacklistService
        );
    }

    @Test
    void logoutWithBearerToken_ShouldBlacklistToken() {

        request.addHeader(
                HttpHeaders.AUTHORIZATION,
                "Bearer valid-token"
        );

        jwtLogoutHandler.logout(
                request,
                response,
                null
        );

        verify(
                tokenBlacklistService
        ).blacklist(
                "valid-token"
        );
    }

    @Test
    void logoutWithInvalidToken_ShouldNotThrowException() {

        request.addHeader(
                HttpHeaders.AUTHORIZATION,
                "Bearer invalid-token"
        );

        doThrow(
                new JwtException(
                        "Invalid token"
                )
        ).when(
                tokenBlacklistService
        ).blacklist(
                "invalid-token"
        );

        assertDoesNotThrow(
                () -> jwtLogoutHandler.logout(
                        request,
                        response,
                        null
                )
        );
    }
}