package org.epam.gym_crm_system1.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.epam.gym_crm_system1.exception.InvalidCredentialsException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void authenticate_WhenTraineeCredentialsValid_ShouldNotThrowException() {
        when(traineeService.isTraineeCredentialsValid("John.Smith", "password123"))
                .thenReturn(true);

        assertDoesNotThrow(() ->
                authenticationService.authenticate("John.Smith", "password123")
        );
    }

    @Test
    void authenticate_WhenTrainerCredentialsValid_ShouldNotThrowException() {
        when(traineeService.isTraineeCredentialsValid("Jane.Smith", "password456"))
                .thenReturn(false);
        when(trainerService.isTrainerCredentialsValid("Jane.Smith", "password456"))
                .thenReturn(true);

        assertDoesNotThrow(() ->
                authenticationService.authenticate("Jane.Smith", "password456")
        );
    }

    @Test
    void authenticate_WhenCredentialsInvalid_ShouldThrowInvalidCredentialsException() {
        when(traineeService.isTraineeCredentialsValid("Wrong.User", "wrong"))
                .thenReturn(false);
        when(trainerService.isTrainerCredentialsValid("Wrong.User", "wrong"))
                .thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> authenticationService.authenticate("Wrong.User", "wrong")
        );
    }

    @Test
    void authenticate_WhenUsernameIsBlank_ShouldThrowInvalidCredentialsException() {
        assertThrows(
                InvalidCredentialsException.class,
                () -> authenticationService.authenticate("", "password123")
        );
    }

    @Test
    void authenticate_WhenPasswordIsBlank_ShouldThrowInvalidCredentialsException() {
        assertThrows(
                InvalidCredentialsException.class,
                () -> authenticationService.authenticate("John.Smith", "")
        );
    }

    @Test
    void authenticateBasic_WhenValidTraineeHeader_ShouldReturnUsername() {
        String header = createBasicHeader("John.Smith", "password123");

        when(traineeService.isTraineeCredentialsValid("John.Smith", "password123"))
                .thenReturn(true);

        String username = authenticationService.authenticateBasic(header);

        assertEquals("John.Smith", username);
    }

    @Test
    void authenticateBasic_WhenValidTrainerHeader_ShouldReturnUsername() {
        String header = createBasicHeader("Jane.Smith", "password456");

        when(traineeService.isTraineeCredentialsValid("Jane.Smith", "password456"))
                .thenReturn(false);
        when(trainerService.isTrainerCredentialsValid("Jane.Smith", "password456"))
                .thenReturn(true);

        String username = authenticationService.authenticateBasic(header);

        assertEquals("Jane.Smith", username);
    }

    @Test
    void authenticateBasic_WhenHeaderIsNull_ShouldThrowInvalidCredentialsException() {
        assertThrows(
                InvalidCredentialsException.class,
                () -> authenticationService.authenticateBasic(null)
        );
    }

    @Test
    void authenticateBasic_WhenHeaderDoesNotStartWithBasic_ShouldThrowInvalidCredentialsException() {
        assertThrows(
                InvalidCredentialsException.class,
                () -> authenticationService.authenticateBasic("Bearer token")
        );
    }

    @Test
    void authenticateBasic_WhenHeaderIsNotBase64_ShouldThrowInvalidCredentialsException() {
        assertThrows(
                InvalidCredentialsException.class,
                () -> authenticationService.authenticateBasic("Basic not-base64")
        );
    }

    @Test
    void authenticateBasic_WhenDecodedHeaderDoesNotContainColon_ShouldThrowInvalidCredentialsException() {
        String encoded = Base64.getEncoder()
                .encodeToString("John.Smith-password123".getBytes(StandardCharsets.UTF_8));

        assertThrows(
                InvalidCredentialsException.class,
                () -> authenticationService.authenticateBasic("Basic " + encoded)
        );
    }

    private String createBasicHeader(String username, String password) {
        String credentials = username + ":" + password;
        String encodedCredentials = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        return "Basic " + encodedCredentials;
    }
}