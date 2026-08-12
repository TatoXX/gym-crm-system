package org.epam.gym_crm_system1.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {

        jwtAuthenticationFilter =
                new JwtAuthenticationFilter(
                        jwtService,
                        userDetailsService,
                        tokenBlacklistService
                );

        request =
                new MockHttpServletRequest();

        response =
                new MockHttpServletResponse();

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void requestWithoutAuthorizationHeader_ShouldContinueWithoutAuthentication()
            throws Exception {

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        verify(filterChain)
                .doFilter(
                        request,
                        response
                );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verifyNoInteractions(
                jwtService,
                userDetailsService,
                tokenBlacklistService
        );
    }

    @Test
    void requestWithNonBearerHeader_ShouldContinueWithoutAuthentication()
            throws Exception {

        request.addHeader(
                HttpHeaders.AUTHORIZATION,
                "Basic abc123"
        );

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        verify(filterChain)
                .doFilter(
                        request,
                        response
                );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verifyNoInteractions(
                jwtService,
                userDetailsService,
                tokenBlacklistService
        );
    }

    @Test
    void blacklistedToken_ShouldNotAuthenticateUser()
            throws Exception {

        request.addHeader(
                HttpHeaders.AUTHORIZATION,
                "Bearer blacklisted-token"
        );

        when(
                tokenBlacklistService
                        .isBlacklisted(
                                "blacklisted-token"
                        )
        ).thenReturn(true);

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

        verify(
                jwtService,
                never()
        ).extractUsername(any());

        verify(filterChain)
                .doFilter(
                        request,
                        response
                );
    }

    @Test
    void invalidJwt_ShouldNotAuthenticateUser()
            throws Exception {

        request.addHeader(
                HttpHeaders.AUTHORIZATION,
                "Bearer invalid-token"
        );

        when(
                tokenBlacklistService
                        .isBlacklisted(
                                "invalid-token"
                        )
        ).thenReturn(false);

        when(
                jwtService.extractUsername(
                        "invalid-token"
                )
        ).thenThrow(
                new JwtException(
                        "Invalid JWT"
                )
        );

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

        verifyNoInteractions(
                userDetailsService
        );

        verify(filterChain)
                .doFilter(
                        request,
                        response
                );
    }

    @Test
    void tokenForUnknownUser_ShouldNotAuthenticate()
            throws Exception {

        request.addHeader(
                HttpHeaders.AUTHORIZATION,
                "Bearer valid-looking-token"
        );

        when(
                tokenBlacklistService
                        .isBlacklisted(
                                "valid-looking-token"
                        )
        ).thenReturn(false);

        when(
                jwtService.extractUsername(
                        "valid-looking-token"
                )
        ).thenReturn(
                "Unknown.User"
        );

        when(
                userDetailsService
                        .loadUserByUsername(
                                "Unknown.User"
                        )
        ).thenThrow(
                new UsernameNotFoundException(
                        "User not found"
                )
        );

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

        verify(filterChain)
                .doFilter(
                        request,
                        response
                );
    }

    @Test
    void invalidTokenForExistingUser_ShouldNotAuthenticate()
            throws Exception {

        request.addHeader(
                HttpHeaders.AUTHORIZATION,
                "Bearer token"
        );

        UserDetails userDetails =
                User.withUsername(
                                "John.Smith"
                        )
                        .password(
                                "encoded"
                        )
                        .roles(
                                "TRAINEE"
                        )
                        .build();

        when(
                tokenBlacklistService
                        .isBlacklisted(
                                "token"
                        )
        ).thenReturn(false);

        when(
                jwtService.extractUsername(
                        "token"
                )
        ).thenReturn(
                "John.Smith"
        );

        when(
                userDetailsService
                        .loadUserByUsername(
                                "John.Smith"
                        )
        ).thenReturn(
                userDetails
        );

        when(
                jwtService.isTokenValid(
                        "token",
                        userDetails
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

        verify(filterChain)
                .doFilter(
                        request,
                        response
                );
    }

    @Test
    void validBearerToken_ShouldAuthenticateUser()
            throws Exception {

        request.addHeader(
                HttpHeaders.AUTHORIZATION,
                "Bearer valid-token"
        );

        UserDetails userDetails =
                User.withUsername(
                                "John.Smith"
                        )
                        .password(
                                "encoded-password"
                        )
                        .roles(
                                "TRAINEE"
                        )
                        .build();

        when(
                tokenBlacklistService
                        .isBlacklisted(
                                "valid-token"
                        )
        ).thenReturn(false);

        when(
                jwtService.extractUsername(
                        "valid-token"
                )
        ).thenReturn(
                "John.Smith"
        );

        when(
                userDetailsService
                        .loadUserByUsername(
                                "John.Smith"
                        )
        ).thenReturn(
                userDetails
        );

        when(
                jwtService.isTokenValid(
                        "valid-token",
                        userDetails
                )
        ).thenReturn(true);

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

        assertTrue(
                authentication.isAuthenticated()
        );

        assertEquals(
                "John.Smith",
                authentication.getName()
        );

        assertTrue(
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority
                                        .getAuthority()
                                        .equals(
                                                "ROLE_TRAINEE"
                                        )
                        )
        );

        verify(filterChain)
                .doFilter(
                        request,
                        response
                );
    }
}