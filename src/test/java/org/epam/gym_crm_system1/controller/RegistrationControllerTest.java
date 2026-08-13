package org.epam.gym_crm_system1.controller;

import org.epam.gym_crm_system1.metrics.GymMetricsService;
import org.epam.gym_crm_system1.model.Trainee;
import org.epam.gym_crm_system1.model.Trainer;
import org.epam.gym_crm_system1.model.TrainingType;
import org.epam.gym_crm_system1.service.TraineeService;
import org.epam.gym_crm_system1.service.TrainerService;
import org.epam.gym_crm_system1.service.TrainingTypeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RegistrationController.class)
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TraineeService traineeService;

    @MockitoBean
    private TrainerService trainerService;

    @MockitoBean
    private TrainingTypeService trainingTypeService;

    @MockitoBean
    private GymMetricsService gymMetricsService;

    @Test
    void registerTrainee_ShouldReturnUsernameAndPassword() throws Exception {
        Mockito.doAnswer(invocation -> {
            Trainee trainee = invocation.getArgument(0);
            trainee.setUserName("John.Smith");
            trainee.setPassword("password123");
            return null;
        }).when(traineeService).createTrainee(any(Trainee.class));

        mockMvc.perform(post("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "John",
                                  "lastName": "Smith",
                                  "dateOfBirth": "2000-05-10",
                                  "address": "Tbilisi"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("John.Smith"))
                .andExpect(jsonPath("$.password").value("password123"));

        verify(gymMetricsService).incrementTraineeRegistrationCount();
    }

    @Test
    void registerTrainer_ShouldReturnUsernameAndPassword() throws Exception {
        TrainingType trainingType = new TrainingType();
        trainingType.setId(1);
        trainingType.setName("Fitness");

        Mockito.when(trainingTypeService.findTrainingTypeById(eq(1)))
                .thenReturn(trainingType);

        Mockito.doAnswer(invocation -> {
            Trainer trainer = invocation.getArgument(0);
            trainer.setUserName("Jane.Smith");
            trainer.setPassword("password456");
            return null;
        }).when(trainerService).createTrainer(any(Trainer.class));

        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Jane",
                                  "lastName": "Smith",
                                  "specializationId": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Jane.Smith"))
                .andExpect(jsonPath("$.password").value("password456"));

        verify(gymMetricsService).incrementTrainerRegistrationCount();
    }

    @Test
    void registerTrainee_WhenFirstNameMissing_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "lastName": "Smith",
                                  "dateOfBirth": "2000-05-10",
                                  "address": "Tbilisi"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}