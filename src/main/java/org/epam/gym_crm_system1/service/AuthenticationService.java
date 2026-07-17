package org.epam.gym_crm_system1.service;

import org.epam.gym_crm_system1.exception.InvalidCredentialsException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class AuthenticationService {

    private static final String BASIC_PREFIX = "Basic ";

    private final TraineeService traineeService;
    private final TrainerService trainerService;

    public AuthenticationService(TraineeService traineeService, TrainerService trainerService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
    }

    public void authenticate(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new InvalidCredentialsException("Username and password are required");
        }

        boolean traineeCredentialsValid = traineeService.isTraineeCredentialsValid(username, password);
        boolean trainerCredentialsValid = trainerService.isTrainerCredentialsValid(username, password);

        if (!traineeCredentialsValid && !trainerCredentialsValid) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }

    public String authenticateBasic(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BASIC_PREFIX)) {
            throw new InvalidCredentialsException("Authorization header is required");
        }

        String encodedCredentials = authorizationHeader.substring(BASIC_PREFIX.length());

        String decodedCredentials;
        try {
            decodedCredentials = new String(
                    Base64.getDecoder().decode(encodedCredentials),
                    StandardCharsets.UTF_8
            );
        } catch (IllegalArgumentException exception) {
            throw new InvalidCredentialsException("Invalid authorization header");
        }

        int separatorIndex = decodedCredentials.indexOf(":");

        if (separatorIndex == -1) {
            throw new InvalidCredentialsException("Invalid authorization header");
        }

        String username = decodedCredentials.substring(0, separatorIndex);
        String password = decodedCredentials.substring(separatorIndex + 1);

        authenticate(username, password);

        return username;
    }
}