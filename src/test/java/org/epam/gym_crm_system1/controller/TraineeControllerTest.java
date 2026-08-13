package org.epam.gym_crm_system1.controller;

import org.epam.gym_crm_system1.dto.response.TraineeProfileResponse;
import org.epam.gym_crm_system1.dto.response.TrainerSummaryResponse;
import org.epam.gym_crm_system1.dto.response.TrainingTypeResponse;
import org.epam.gym_crm_system1.dto.response.UpdateTraineeProfileResponse;
import org.epam.gym_crm_system1.mapper.ResponseMapper;
import org.epam.gym_crm_system1.model.Trainee;
import org.epam.gym_crm_system1.model.Trainer;
import org.epam.gym_crm_system1.security.JwtAuthenticationFilter;
import org.epam.gym_crm_system1.service.TraineeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TraineeController.class)
@AutoConfigureMockMvc(addFilters = false)
class TraineeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TraineeService traineeService;

    @MockitoBean
    private ResponseMapper responseMapper;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void getTraineeProfile_ShouldReturnTraineeProfile() throws Exception {

        Trainee trainee = Mockito.mock(Trainee.class);

        TraineeProfileResponse response =
                new TraineeProfileResponse(
                        "John",
                        "Smith",
                        LocalDate.of(2000, 5, 10),
                        "Tbilisi",
                        true,
                        List.of()
                );

        Mockito.when(
                traineeService.selectTraineeByUsername(
                        "John.Smith"
                )
        ).thenReturn(trainee);

        Mockito.when(
                responseMapper.toTraineeProfileResponse(
                        trainee
                )
        ).thenReturn(response);

        mockMvc.perform(
                        get("/api/trainees")
                                .param(
                                        "username",
                                        "John.Smith"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.firstName")
                                .value("John")
                )
                .andExpect(
                        jsonPath("$.lastName")
                                .value("Smith")
                )
                .andExpect(
                        jsonPath("$.dateOfBirth")
                                .value("2000-05-10")
                )
                .andExpect(
                        jsonPath("$.address")
                                .value("Tbilisi")
                )
                .andExpect(
                        jsonPath("$.isActive")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.trainers")
                                .isArray()
                );
    }

    @Test
    void updateTraineeProfile_ShouldReturnUpdatedProfile() throws Exception {

        Trainee trainee = Mockito.mock(Trainee.class);

        UpdateTraineeProfileResponse response =
                new UpdateTraineeProfileResponse(
                        "John.Smith",
                        "John",
                        "Smith",
                        LocalDate.of(2000, 5, 10),
                        "Tbilisi",
                        true,
                        List.of()
                );

        Mockito.when(
                traineeService.selectTraineeByUsername(
                        "John.Smith"
                )
        ).thenReturn(trainee);

        Mockito.when(
                responseMapper.toUpdateTraineeProfileResponse(
                        trainee
                )
        ).thenReturn(response);

        mockMvc.perform(
                        put("/api/trainees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "username": "John.Smith",
                                          "firstName": "John",
                                          "lastName": "Smith",
                                          "dateOfBirth": "2000-05-10",
                                          "address": "Tbilisi",
                                          "isActive": true
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.username")
                                .value("John.Smith")
                )
                .andExpect(
                        jsonPath("$.firstName")
                                .value("John")
                )
                .andExpect(
                        jsonPath("$.lastName")
                                .value("Smith")
                )
                .andExpect(
                        jsonPath("$.isActive")
                                .value(true)
                );

        verify(traineeService)
                .updateTrainee(trainee);
    }

    @Test
    void updateTraineeProfile_WhenUsernameMissing_ShouldReturnBadRequest()
            throws Exception {

        mockMvc.perform(
                        put("/api/trainees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "firstName": "John",
                                          "lastName": "Smith",
                                          "isActive": true
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteTraineeProfile_ShouldReturnOk() throws Exception {

        mockMvc.perform(
                        delete("/api/trainees")
                                .param(
                                        "username",
                                        "John.Smith"
                                )
                )
                .andExpect(status().isOk());

        verify(traineeService)
                .deleteTraineeByUsername(
                        "John.Smith"
                );
    }

    @Test
    void getNotAssignedActiveTrainers_ShouldReturnTrainersList()
            throws Exception {

        Trainer trainer = Mockito.mock(Trainer.class);

        List<TrainerSummaryResponse> response =
                List.of(
                        new TrainerSummaryResponse(
                                "Trainer.One",
                                "Trainer",
                                "One",
                                new TrainingTypeResponse(
                                        1,
                                        "Fitness"
                                )
                        )
                );

        Mockito.when(
                traineeService
                        .getTrainersNotAssignedToTrainee(
                                "John.Smith"
                        )
        ).thenReturn(
                List.of(trainer)
        );

        Mockito.when(
                responseMapper
                        .toTrainerSummaryResponseList(
                                any()
                        )
        ).thenReturn(response);

        mockMvc.perform(
                        get(
                                "/api/trainees/not-assigned-trainers"
                        )
                                .param(
                                        "username",
                                        "John.Smith"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].username")
                                .value("Trainer.One")
                )
                .andExpect(
                        jsonPath("$[0].firstName")
                                .value("Trainer")
                )
                .andExpect(
                        jsonPath("$[0].lastName")
                                .value("One")
                )
                .andExpect(
                        jsonPath(
                                "$[0].specialization.trainingTypeId"
                        ).value(1)
                )
                .andExpect(
                        jsonPath(
                                "$[0].specialization.trainingTypeName"
                        ).value("Fitness")
                );
    }

    @Test
    void updateTraineeTrainersList_ShouldReturnUpdatedTrainersList()
            throws Exception {

        Trainee trainee = Mockito.mock(Trainee.class);

        List<TrainerSummaryResponse> response =
                List.of(
                        new TrainerSummaryResponse(
                                "Trainer.One",
                                "Trainer",
                                "One",
                                new TrainingTypeResponse(
                                        1,
                                        "Fitness"
                                )
                        )
                );

        Mockito.when(
                traineeService.selectTraineeByUsername(
                        "John.Smith"
                )
        ).thenReturn(trainee);

        Mockito.when(
                responseMapper
                        .toTrainerSummaryResponseList(
                                any()
                        )
        ).thenReturn(response);

        mockMvc.perform(
                        put("/api/trainees/trainers")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "traineeUsername": "John.Smith",
                                          "trainers": [
                                            {
                                              "trainerUsername": "Trainer.One"
                                            }
                                          ]
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].username")
                                .value("Trainer.One")
                )
                .andExpect(
                        jsonPath(
                                "$[0].specialization.trainingTypeName"
                        ).value("Fitness")
                );

        verify(traineeService)
                .updateTraineeTrainersList(
                        eq("John.Smith"),
                        eq(
                                List.of(
                                        "Trainer.One"
                                )
                        )
                );
    }

    @Test
    void updateTraineeStatus_WhenIsActiveTrue_ShouldActivateTrainee()
            throws Exception {

        mockMvc.perform(
                        patch("/api/trainees/status")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "username": "John.Smith",
                                          "isActive": true
                                        }
                                        """)
                )
                .andExpect(status().isOk());

        verify(traineeService)
                .activateTrainee(
                        "John.Smith"
                );
    }

    @Test
    void updateTraineeStatus_WhenIsActiveFalse_ShouldDeactivateTrainee()
            throws Exception {

        mockMvc.perform(
                        patch("/api/trainees/status")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                          "username": "John.Smith",
                                          "isActive": false
                                        }
                                        """)
                )
                .andExpect(status().isOk());

        verify(traineeService)
                .deactivateTrainee(
                        "John.Smith"
                );
    }
}