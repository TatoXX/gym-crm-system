package org.epam.gym_crm_system1.controller;

import org.epam.gym_crm_system1.model.Trainee;
import org.epam.gym_crm_system1.model.Trainer;
import org.epam.gym_crm_system1.model.Training;
import org.epam.gym_crm_system1.model.TrainingType;
import org.epam.gym_crm_system1.service.AuthenticationService;
import org.epam.gym_crm_system1.service.TraineeService;
import org.epam.gym_crm_system1.service.TrainerService;
import org.epam.gym_crm_system1.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TrainingController.class)
class TrainingControllerTest {

    private static final String AUTH_HEADER = "Basic test-token";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrainingService trainingService;

    @MockitoBean
    private TraineeService traineeService;

    @MockitoBean
    private TrainerService trainerService;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Test
    void getTraineeTrainings_ShouldReturnTrainingsList() throws Exception {
        Training training = Mockito.mock(Training.class);
        Trainer trainer = Mockito.mock(Trainer.class);
        TrainingType trainingType = Mockito.mock(TrainingType.class);

        Mockito.when(training.getTrainingName()).thenReturn("Morning Training");
        Mockito.when(training.getTrainingDate()).thenReturn(LocalDate.of(2026, 7, 16));
        Mockito.when(training.getTrainingDurationMinutes()).thenReturn(60);
        Mockito.when(training.getTrainingType()).thenReturn(trainingType);
        Mockito.when(trainingType.getName()).thenReturn("Fitness");
        Mockito.when(training.getTrainer()).thenReturn(trainer);
        Mockito.when(trainer.getUserName()).thenReturn("Trainer.One");

        Mockito.when(traineeService.getTraineeTrainingsByCriteria(
                        "John.Smith",
                        LocalDate.of(2026, 7, 1),
                        LocalDate.of(2026, 7, 31),
                        "Trainer",
                        "Fitness"
                ))
                .thenReturn(List.of(training));

        mockMvc.perform(get("/api/trainings/trainee")
                        .header("Authorization", AUTH_HEADER)
                        .param("username", "John.Smith")
                        .param("periodFrom", "2026-07-01")
                        .param("periodTo", "2026-07-31")
                        .param("trainerName", "Trainer")
                        .param("trainingType", "Fitness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Morning Training"))
                .andExpect(jsonPath("$[0].trainingDate").value("2026-07-16"))
                .andExpect(jsonPath("$[0].trainingType").value("Fitness"))
                .andExpect(jsonPath("$[0].trainingDuration").value(60))
                .andExpect(jsonPath("$[0].trainerName").value("Trainer.One"));
    }

    @Test
    void getTrainerTrainings_ShouldReturnTrainingsList() throws Exception {
        Training training = Mockito.mock(Training.class);
        Trainee trainee = Mockito.mock(Trainee.class);
        TrainingType trainingType = Mockito.mock(TrainingType.class);

        Mockito.when(training.getTrainingName()).thenReturn("Evening Training");
        Mockito.when(training.getTrainingDate()).thenReturn(LocalDate.of(2026, 7, 17));
        Mockito.when(training.getTrainingDurationMinutes()).thenReturn(45);
        Mockito.when(training.getTrainingType()).thenReturn(trainingType);
        Mockito.when(trainingType.getName()).thenReturn("Yoga");
        Mockito.when(training.getTrainee()).thenReturn(trainee);
        Mockito.when(trainee.getUserName()).thenReturn("Trainee.One");

        Mockito.when(trainerService.getTrainerTrainingsByCriteria(
                        "Trainer.One",
                        LocalDate.of(2026, 7, 1),
                        LocalDate.of(2026, 7, 31),
                        "Trainee"
                ))
                .thenReturn(List.of(training));

        mockMvc.perform(get("/api/trainings/trainer")
                        .header("Authorization", AUTH_HEADER)
                        .param("username", "Trainer.One")
                        .param("periodFrom", "2026-07-01")
                        .param("periodTo", "2026-07-31")
                        .param("traineeName", "Trainee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Evening Training"))
                .andExpect(jsonPath("$[0].trainingDate").value("2026-07-17"))
                .andExpect(jsonPath("$[0].trainingType").value("Yoga"))
                .andExpect(jsonPath("$[0].trainingDuration").value(45))
                .andExpect(jsonPath("$[0].traineeName").value("Trainee.One"));
    }

    @Test
    void addTraining_ShouldReturnOk() throws Exception {
        Trainee trainee = Mockito.mock(Trainee.class);
        Trainer trainer = Mockito.mock(Trainer.class);
        TrainingType trainingType = Mockito.mock(TrainingType.class);

        Mockito.when(traineeService.selectTraineeByUsername("John.Smith"))
                .thenReturn(trainee);
        Mockito.when(trainerService.selectTrainerByUsername("Trainer.One"))
                .thenReturn(trainer);
        Mockito.when(trainer.getTrainingType()).thenReturn(trainingType);

        mockMvc.perform(post("/api/trainings")
                        .header("Authorization", AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "traineeUsername": "John.Smith",
                                  "trainerUsername": "Trainer.One",
                                  "trainingName": "Morning Training",
                                  "trainingDate": "2026-07-16",
                                  "trainingDuration": 60
                                }
                                """))
                .andExpect(status().isOk());

        verify(trainingService).createTraining(any(Training.class));
    }

    @Test
    void addTraining_WhenTrainingNameMissing_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/trainings")
                        .header("Authorization", AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "traineeUsername": "John.Smith",
                                  "trainerUsername": "Trainer.One",
                                  "trainingDate": "2026-07-16",
                                  "trainingDuration": 60
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}