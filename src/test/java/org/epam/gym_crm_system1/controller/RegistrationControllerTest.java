package org.epam.gym_crm_system1.controller;

import org.epam.gym_crm_system1.metrics.GymMetricsService;
import org.epam.gym_crm_system1.model.Trainee;
import org.epam.gym_crm_system1.model.Trainer;
import org.epam.gym_crm_system1.model.TrainingType;
import org.epam.gym_crm_system1.security.JwtAuthenticationFilter;
import org.epam.gym_crm_system1.service.TraineeService;
import org.epam.gym_crm_system1.service.TrainerService;
import org.epam.gym_crm_system1.service.TrainingTypeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RegistrationController.class)
@AutoConfigureMockMvc(addFilters = false)
class RegistrationControllerTest {

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

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void registerTrainee_ShouldReturnUsernameAndPassword() throws Exception {

        Mockito.when(
                traineeService.createTrainee(
                        any(Trainee.class)
                )
        ).thenAnswer(invocation -> {

            Trainee trainee = invocation.getArgument(0);

            trainee.setUserName("John.Smith");

            return "password123";
        });

        mockMvc.perform(
                        post("/api/trainees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "firstName": "John",
                                          "lastName": "Smith",
                                          "dateOfBirth": "2000-05-10",
                                          "address": "Tbilisi"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.username")
                                .value("John.Smith")
                )
                .andExpect(
                        jsonPath("$.password")
                                .value("password123")
                );

        verify(gymMetricsService)
                .incrementTraineeRegistrationCount();
    }

    @Test
    void registerTrainer_ShouldReturnUsernameAndPassword() throws Exception {

        TrainingType trainingType = new TrainingType();

        trainingType.setId(1);
        trainingType.setName("Fitness");

        Mockito.when(
                trainingTypeService.findTrainingTypeById(
                        eq(1)
                )
        ).thenReturn(trainingType);

        Mockito.when(
                trainerService.createTrainer(
                        any(Trainer.class)
                )
        ).thenAnswer(invocation -> {

            Trainer trainer = invocation.getArgument(0);

            trainer.setUserName("Jane.Smith");

            return "password456";
        });

        mockMvc.perform(
                        post("/api/trainers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "firstName": "Jane",
                                          "lastName": "Smith",
                                          "specializationId": 1
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.username")
                                .value("Jane.Smith")
                )
                .andExpect(
                        jsonPath("$.password")
                                .value("password456")
                );

        verify(gymMetricsService)
                .incrementTrainerRegistrationCount();
    }

    @Test
    void registerTrainee_WhenFirstNameMissing_ShouldReturnBadRequest() throws Exception {

        mockMvc.perform(
                        post("/api/trainees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "lastName": "Smith",
                                          "dateOfBirth": "2000-05-10",
                                          "address": "Tbilisi"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }
}