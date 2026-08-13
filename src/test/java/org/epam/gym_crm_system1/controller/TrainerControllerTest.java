package org.epam.gym_crm_system1.controller;

import org.epam.gym_crm_system1.dto.response.TrainerProfileResponse;
import org.epam.gym_crm_system1.dto.response.TrainingTypeResponse;
import org.epam.gym_crm_system1.dto.response.UpdateTrainerProfileResponse;
import org.epam.gym_crm_system1.mapper.ResponseMapper;
import org.epam.gym_crm_system1.model.Trainer;
import org.epam.gym_crm_system1.service.AuthenticationService;
import org.epam.gym_crm_system1.service.TrainerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TrainerController.class)
class TrainerControllerTest {

    private static final String AUTH_HEADER = "Basic test-token";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrainerService trainerService;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private ResponseMapper responseMapper;

    @Test
    void getTrainerProfile_ShouldReturnTrainerProfile() throws Exception {
        Trainer trainer = Mockito.mock(Trainer.class);

        TrainerProfileResponse response = new TrainerProfileResponse(
                "Jane",
                "Smith",
                new TrainingTypeResponse(1, "Fitness"),
                true,
                List.of()
        );

        Mockito.when(trainerService.selectTrainerByUsername("Jane.Smith"))
                .thenReturn(trainer);
        Mockito.when(responseMapper.toTrainerProfileResponse(trainer))
                .thenReturn(response);

        mockMvc.perform(get("/api/trainers")
                        .header("Authorization", AUTH_HEADER)
                        .param("username", "Jane.Smith"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.specialization.trainingTypeId").value(1))
                .andExpect(jsonPath("$.specialization.trainingTypeName").value("Fitness"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.trainees").isArray());
    }

    @Test
    void updateTrainerProfile_ShouldReturnUpdatedTrainerProfile() throws Exception {
        Trainer trainer = Mockito.mock(Trainer.class);

        UpdateTrainerProfileResponse response = new UpdateTrainerProfileResponse(
                "Jane.Smith",
                "Jane",
                "Smith",
                new TrainingTypeResponse(1, "Fitness"),
                true,
                List.of()
        );

        Mockito.when(trainerService.selectTrainerByUsername("Jane.Smith"))
                .thenReturn(trainer);
        Mockito.when(responseMapper.toUpdateTrainerProfileResponse(trainer))
                .thenReturn(response);

        mockMvc.perform(put("/api/trainers")
                        .header("Authorization", AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "Jane.Smith",
                                  "firstName": "Jane",
                                  "lastName": "Smith",
                                  "isActive": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Jane.Smith"))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.specialization.trainingTypeName").value("Fitness"))
                .andExpect(jsonPath("$.isActive").value(true));

        verify(trainerService).updateTrainer(trainer);
    }

    @Test
    void updateTrainerProfile_WhenFirstNameMissing_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(put("/api/trainers")
                        .header("Authorization", AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "Jane.Smith",
                                  "lastName": "Smith",
                                  "isActive": true
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTrainerStatus_WhenIsActiveTrue_ShouldActivateTrainer() throws Exception {
        mockMvc.perform(patch("/api/trainers/status")
                        .header("Authorization", AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "Jane.Smith",
                                  "isActive": true
                                }
                                """))
                .andExpect(status().isOk());

        verify(trainerService).activateTrainer("Jane.Smith");
    }

    @Test
    void updateTrainerStatus_WhenIsActiveFalse_ShouldDeactivateTrainer() throws Exception {
        mockMvc.perform(patch("/api/trainers/status")
                        .header("Authorization", AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "Jane.Smith",
                                  "isActive": false
                                }
                                """))
                .andExpect(status().isOk());

        verify(trainerService).deactivateTrainer("Jane.Smith");
    }
}