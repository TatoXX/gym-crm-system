package org.epam.gym_crm_system1.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class JwtLogoutHandler implements LogoutHandler {

    private final TokenBlacklistService tokenBlacklistService;

    public JwtLogoutHandler(
            TokenBlacklistService tokenBlacklistService) {

        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {

        String authorizationHeader =
                request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {
            return;
        }

        String token = authorizationHeader.substring(7);

        try {
            tokenBlacklistService.blacklist(token);
        } catch (JwtException | IllegalArgumentException exception) {
            // Invalid token - nothing to blacklist
        }
    }
}