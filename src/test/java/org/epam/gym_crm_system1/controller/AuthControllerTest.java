package org.epam.gym_crm_system1.controller;

import org.epam.gym_crm_system1.metrics.GymMetricsService;
import org.epam.gym_crm_system1.security.BruteForceProtectionService;
import org.epam.gym_crm_system1.security.JwtAuthenticationFilter;
import org.epam.gym_crm_system1.security.JwtService;
import org.epam.gym_crm_system1.service.TraineeService;
import org.epam.gym_crm_system1.service.TrainerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TraineeService traineeService;

    @MockitoBean
    private TrainerService trainerService;

    @MockitoBean
    private GymMetricsService gymMetricsService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private BruteForceProtectionService bruteForceProtectionService;

    /*
     * JwtAuthenticationFilter is a Spring Filter component.
     * @WebMvcTest can discover Filter beans, so we mock it here
     * to keep this test focused only on AuthController.
     */
    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void login_WhenTraineeCredentialsValid_ShouldReturnOk() throws Exception {

        Mockito.when(
                bruteForceProtectionService.isBlocked("John.Smith")
        ).thenReturn(false);

        Mockito.when(
                authenticationManager.authenticate(any())
        ).thenReturn(
                Mockito.mock(Authentication.class)
        );

        Mockito.when(
                jwtService.generate("John.Smith")
        ).thenReturn("trainee-jwt-token");

        mockMvc.perform(
                        get("/api/login")
                                .param("username", "John.Smith")
                                .param("password", "password123")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.token")
                                .value("trainee-jwt-token")
                );

        verify(gymMetricsService)
                .incrementLoginSuccessCount();

        verify(bruteForceProtectionService)
                .loginSucceeded("John.Smith");

        verify(jwtService)
                .generate("John.Smith");
    }

    @Test
    void login_WhenTrainerCredentialsValid_ShouldReturnOk() throws Exception {

        Mockito.when(
                bruteForceProtectionService.isBlocked("Jane.Smith")
        ).thenReturn(false);

        Mockito.when(
                authenticationManager.authenticate(any())
        ).thenReturn(
                Mockito.mock(Authentication.class)
        );

        Mockito.when(
                jwtService.generate("Jane.Smith")
        ).thenReturn("trainer-jwt-token");

        mockMvc.perform(
                        get("/api/login")
                                .param("username", "Jane.Smith")
                                .param("password", "password456")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.token")
                                .value("trainer-jwt-token")
                );

        verify(gymMetricsService)
                .incrementLoginSuccessCount();

        verify(bruteForceProtectionService)
                .loginSucceeded("Jane.Smith");

        verify(jwtService)
                .generate("Jane.Smith");
    }

    @Test
    void login_WhenCredentialsInvalid_ShouldReturnUnauthorized() throws Exception {

        Mockito.when(
                bruteForceProtectionService.isBlocked("Wrong.User")
        ).thenReturn(false);

        Mockito.when(
                authenticationManager.authenticate(any())
        ).thenThrow(
                new BadCredentialsException("Bad credentials")
        );

        mockMvc.perform(
                        get("/api/login")
                                .param("username", "Wrong.User")
                                .param("password", "wrong")
                )
                .andExpect(status().isUnauthorized());

        verify(gymMetricsService)
                .incrementLoginFailureCount();

        verify(bruteForceProtectionService)
                .loginFailed("Wrong.User");

        verify(jwtService, never())
                .generate(any());
    }

    @Test
    void login_WhenUserBlocked_ShouldReturnUnauthorized() throws Exception {

        Mockito.when(
                bruteForceProtectionService.isBlocked("Blocked.User")
        ).thenReturn(true);

        mockMvc.perform(
                        get("/api/login")
                                .param("username", "Blocked.User")
                                .param("password", "password123")
                )
                .andExpect(status().isUnauthorized());

        verify(authenticationManager, never())
                .authenticate(any());

        verify(jwtService, never())
                .generate(any());

        verify(gymMetricsService, never())
                .incrementLoginSuccessCount();
    }

    @Test
    void changeLogin_WhenTraineeCredentialsValid_ShouldReturnOk() throws Exception {

        Mockito.when(
                traineeService.isTraineeCredentialsValid(
                        "John.Smith",
                        "oldPass"
                )
        ).thenReturn(true);

        mockMvc.perform(
                        put("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "username": "John.Smith",
                                          "oldPassword": "oldPass",
                                          "newPassword": "newPass"
                                        }
                                        """)
                )
                .andExpect(status().isOk());

        verify(traineeService)
                .changeTraineePassword(
                        "John.Smith",
                        "oldPass",
                        "newPass"
                );
    }

    @Test
    void changeLogin_WhenTrainerCredentialsValid_ShouldReturnOk() throws Exception {

        Mockito.when(
                traineeService.isTraineeCredentialsValid(
                        "Jane.Smith",
                        "oldPass"
                )
        ).thenReturn(false);

        Mockito.when(
                trainerService.isTrainerCredentialsValid(
                        "Jane.Smith",
                        "oldPass"
                )
        ).thenReturn(true);

        mockMvc.perform(
                        put("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "username": "Jane.Smith",
                                          "oldPassword": "oldPass",
                                          "newPassword": "newPass"
                                        }
                                        """)
                )
                .andExpect(status().isOk());

        verify(trainerService)
                .changeTrainerPassword(
                        "Jane.Smith",
                        "oldPass",
                        "newPass"
                );
    }

    @Test
    void changeLogin_WhenOldPasswordMissing_ShouldReturnBadRequest() throws Exception {

        mockMvc.perform(
                        put("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "username": "John.Smith",
                                          "newPassword": "newPass"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }
}