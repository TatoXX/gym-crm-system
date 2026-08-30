package org.epam.trainerworkloadservice.security;

import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtService jwtService;
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {

        jwtService =
                mock(JwtService.class);

        jwtAuthenticationFilter =
                new JwtAuthenticationFilter(
                        jwtService
                );

        filterChain =
                mock(FilterChain.class);

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {

        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAuthenticateRequestWhenBearerTokenIsValid()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                HttpHeaders.AUTHORIZATION,
                "Bearer valid-token"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        when(
                jwtService.isTokenValid(
                        "valid-token"
                )
        ).thenReturn(true);

        when(
                jwtService.extractUsername(
                        "valid-token"
                )
        ).thenReturn("Test.Trainer");

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertNotNull(authentication);

        assertEquals(
                "Test.Trainer",
                authentication.getPrincipal()
        );

        assertTrue(
                authentication.isAuthenticated()
        );

        verify(jwtService)
                .isTokenValid(
                        "valid-token"
                );

        verify(jwtService)
                .extractUsername(
                        "valid-token"
                );

        verify(filterChain)
                .doFilter(
                        request,
                        response
                );
    }

    @Test
    void shouldRemainUnauthenticatedWhenAuthorizationHeaderIsMissing()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verifyNoInteractions(jwtService);

        verify(filterChain)
                .doFilter(
                        request,
                        response
                );
    }

    @Test
    void shouldRemainUnauthenticatedWhenAuthorizationHeaderIsNotBearer()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                HttpHeaders.AUTHORIZATION,
                "Basic some-value"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verifyNoInteractions(jwtService);

        verify(filterChain)
                .doFilter(
                        request,
                        response
                );
    }

    @Test
    void shouldRemainUnauthenticatedWhenTokenIsInvalid()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                HttpHeaders.AUTHORIZATION,
                "Bearer invalid-token"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        when(
                jwtService.isTokenValid(
                        "invalid-token"
                )
        ).thenReturn(false);

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(jwtService)
                .isTokenValid(
                        "invalid-token"
                );

        verify(
                jwtService,
                never()
        ).extractUsername(anyString());

        verify(filterChain)
                .doFilter(
                        request,
                        response
                );
    }

    @Test
    void shouldRemainUnauthenticatedWhenJwtParsingFails()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                HttpHeaders.AUTHORIZATION,
                "Bearer broken-token"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        when(
                jwtService.isTokenValid(
                        "broken-token"
                )
        ).thenThrow(
                new MalformedJwtException(
                        "Invalid JWT"
                )
        );

        assertDoesNotThrow(
                () ->
                        jwtAuthenticationFilter
                                .doFilter(
                                        request,
                                        response,
                                        filterChain
                                )
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(filterChain)
                .doFilter(
                        request,
                        response
                );
    }
}