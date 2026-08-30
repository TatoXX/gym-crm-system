package org.epam.trainerworkloadservice.controller;

import org.epam.trainerworkloadservice.dto.request.TrainerWorkloadRequest;
import org.epam.trainerworkloadservice.exception.GlobalExceptionHandler;
import org.epam.trainerworkloadservice.service.TrainerWorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TrainerWorkloadService trainerWorkloadService;

    @BeforeEach
    void setUp() {

        TrainerWorkloadController controller =
                new TrainerWorkloadController(
                        trainerWorkloadService
                );

        mockMvc =
                MockMvcBuilders
                        .standaloneSetup(controller)
                        .setControllerAdvice(
                                new GlobalExceptionHandler()
                        )
                        .build();
    }

    @Test
    void updateWorkload_WhenRequestValid_ShouldReturnOk()
            throws Exception {

        String body = """
                {
                  "trainerUsername": "Test.Trainer",
                  "trainerFirstName": "Test",
                  "trainerLastName": "Trainer",
                  "isActive": true,
                  "trainingDate": "2026-08-30",
                  "trainingDuration": 40,
                  "actionType": "ADD"
                }
                """;

        mockMvc.perform(
                        post("/api/workloads")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(body)
                )
                .andExpect(status().isOk());

        ArgumentCaptor<TrainerWorkloadRequest> captor =
                ArgumentCaptor.forClass(
                        TrainerWorkloadRequest.class
                );

        verify(trainerWorkloadService)
                .updateWorkload(captor.capture());

        TrainerWorkloadRequest request =
                captor.getValue();

        assertEquals(
                "Test.Trainer",
                request.getTrainerUsername()
        );

        assertEquals(
                40,
                request.getTrainingDuration()
        );

        assertEquals(
                "ADD",
                request.getActionType().name()
        );
    }

    @Test
    void updateWorkload_WhenUsernameBlank_ShouldReturnBadRequest()
            throws Exception {

        String body = """
                {
                  "trainerUsername": "",
                  "trainerFirstName": "Test",
                  "trainerLastName": "Trainer",
                  "isActive": true,
                  "trainingDate": "2026-08-30",
                  "trainingDuration": 40,
                  "actionType": "ADD"
                }
                """;

        mockMvc.perform(
                        post("/api/workloads")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(body)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.status")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.error")
                                .value("Bad Request")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "trainerUsername: must not be blank"
                                )
                )
                .andExpect(
                        jsonPath("$.path")
                                .value("/api/workloads")
                );

        verifyNoInteractions(
                trainerWorkloadService
        );
    }

    @Test
    void updateWorkload_WhenDeleteWouldBeNegative_ShouldReturnBadRequest()
            throws Exception {

        doThrow(
                new IllegalArgumentException(
                        "Training summary duration cannot be negative"
                )
        )
                .when(trainerWorkloadService)
                .updateWorkload(any());

        String body = """
                {
                  "trainerUsername": "Test.Trainer",
                  "trainerFirstName": "Test",
                  "trainerLastName": "Trainer",
                  "isActive": true,
                  "trainingDate": "2026-08-30",
                  "trainingDuration": 40,
                  "actionType": "DELETE"
                }
                """;

        mockMvc.perform(
                        post("/api/workloads")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(body)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.status")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Training summary duration cannot be negative"
                                )
                );
    }

    @Test
    void updateWorkload_WhenUnexpectedException_ShouldReturnInternalServerError()
            throws Exception {

        doThrow(
                new RuntimeException(
                        "database problem"
                )
        )
                .when(trainerWorkloadService)
                .updateWorkload(any());

        String body = """
                {
                  "trainerUsername": "Test.Trainer",
                  "trainerFirstName": "Test",
                  "trainerLastName": "Trainer",
                  "isActive": true,
                  "trainingDate": "2026-08-30",
                  "trainingDuration": 40,
                  "actionType": "ADD"
                }
                """;

        mockMvc.perform(
                        post("/api/workloads")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(body)
                )
                .andExpect(
                        status()
                                .isInternalServerError()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(500)
                )
                .andExpect(
                        jsonPath("$.error")
                                .value(
                                        "Internal Server Error"
                                )
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Internal server error"
                                )
                );
    }

    @Test
    void getMonthlyWorkload_WhenExists_ShouldReturnDuration()
            throws Exception {

        when(
                trainerWorkloadService
                        .getMonthlyWorkload(
                                "Test.Trainer",
                                2026,
                                8
                        )
        ).thenReturn(90);

        mockMvc.perform(
                        get(
                                "/api/workloads/"
                                        + "Test.Trainer"
                                        + "/years/2026"
                                        + "/months/8"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(content().string("90"));
    }

    @Test
    void getMonthlyWorkload_WhenDoesNotExist_ShouldReturnNotFound()
            throws Exception {

        when(
                trainerWorkloadService
                        .getMonthlyWorkload(
                                "Unknown.Trainer",
                                2026,
                                8
                        )
        ).thenReturn(null);

        mockMvc.perform(
                        get(
                                "/api/workloads/"
                                        + "Unknown.Trainer"
                                        + "/years/2026"
                                        + "/months/8"
                        )
                )
                .andExpect(status().isNotFound());
    }
}