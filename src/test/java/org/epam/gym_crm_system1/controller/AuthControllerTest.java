package org.epam.gym_crm_system1.controller;

import org.epam.gym_crm_system1.metrics.GymMetricsService;
import org.epam.gym_crm_system1.service.TraineeService;
import org.epam.gym_crm_system1.service.TrainerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TraineeService traineeService;

    @MockitoBean
    private TrainerService trainerService;

    @MockitoBean
    private GymMetricsService gymMetricsService;

    @Test
    void login_WhenTraineeCredentialsValid_ShouldReturnOk() throws Exception {
        Mockito.when(traineeService.isTraineeCredentialsValid("John.Smith", "password123"))
                .thenReturn(true);

        mockMvc.perform(get("/api/login")
                        .param("username", "John.Smith")
                        .param("password", "password123"))
                .andExpect(status().isOk());

        verify(gymMetricsService).incrementLoginSuccessCount();
    }

    @Test
    void login_WhenTrainerCredentialsValid_ShouldReturnOk() throws Exception {
        Mockito.when(traineeService.isTraineeCredentialsValid("Jane.Smith", "password456"))
                .thenReturn(false);
        Mockito.when(trainerService.isTrainerCredentialsValid("Jane.Smith", "password456"))
                .thenReturn(true);

        mockMvc.perform(get("/api/login")
                        .param("username", "Jane.Smith")
                        .param("password", "password456"))
                .andExpect(status().isOk());

        verify(gymMetricsService).incrementLoginSuccessCount();
    }

    @Test
    void login_WhenCredentialsInvalid_ShouldReturnUnauthorized() throws Exception {
        Mockito.when(traineeService.isTraineeCredentialsValid("Wrong.User", "wrong"))
                .thenReturn(false);
        Mockito.when(trainerService.isTrainerCredentialsValid("Wrong.User", "wrong"))
                .thenReturn(false);

        mockMvc.perform(get("/api/login")
                        .param("username", "Wrong.User")
                        .param("password", "wrong"))
                .andExpect(status().isUnauthorized());

        verify(gymMetricsService).incrementLoginFailureCount();
    }

    @Test
    void changeLogin_WhenTraineeCredentialsValid_ShouldReturnOk() throws Exception {
        Mockito.when(traineeService.isTraineeCredentialsValid("John.Smith", "oldPass"))
                .thenReturn(true);

        mockMvc.perform(put("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "John.Smith",
                                  "oldPassword": "oldPass",
                                  "newPassword": "newPass"
                                }
                                """))
                .andExpect(status().isOk());

        verify(traineeService).changeTraineePassword("John.Smith", "oldPass", "newPass");
    }

    @Test
    void changeLogin_WhenTrainerCredentialsValid_ShouldReturnOk() throws Exception {
        Mockito.when(traineeService.isTraineeCredentialsValid("Jane.Smith", "oldPass"))
                .thenReturn(false);
        Mockito.when(trainerService.isTrainerCredentialsValid("Jane.Smith", "oldPass"))
                .thenReturn(true);

        mockMvc.perform(put("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "Jane.Smith",
                                  "oldPassword": "oldPass",
                                  "newPassword": "newPass"
                                }
                                """))
                .andExpect(status().isOk());

        verify(trainerService).changeTrainerPassword("Jane.Smith", "oldPass", "newPass");
    }

    @Test
    void changeLogin_WhenOldPasswordMissing_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(put("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "John.Smith",
                                  "newPassword": "newPass"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}