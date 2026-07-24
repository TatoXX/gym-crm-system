package org.epam.gym_crm_system1.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import org.epam.gym_crm_system1.dto.request.AddTrainingRequest;
import org.epam.gym_crm_system1.dto.response.TraineeTrainingResponse;
import org.epam.gym_crm_system1.dto.response.TrainerTrainingResponse;
import org.epam.gym_crm_system1.metrics.GymMetricsService;
import org.epam.gym_crm_system1.model.Trainee;
import org.epam.gym_crm_system1.model.Trainer;
import org.epam.gym_crm_system1.model.Training;
import org.epam.gym_crm_system1.service.AuthenticationService;
import org.epam.gym_crm_system1.service.TraineeService;
import org.epam.gym_crm_system1.service.TrainerService;
import org.epam.gym_crm_system1.service.TrainingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "Trainings")
@RestController
@RequestMapping("/api/trainings")
public class TrainingController {

    private final TrainingService trainingService;
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final AuthenticationService authenticationService;
    private final GymMetricsService gymMetricsService;

    public TrainingController(TrainingService trainingService,
                              TraineeService traineeService,
                              TrainerService trainerService,
                              AuthenticationService authenticationService,
                              GymMetricsService gymMetricsService) {
        this.trainingService = trainingService;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.authenticationService = authenticationService;
        this.gymMetricsService = gymMetricsService;
    }

    @ApiOperation(value = "Get trainee trainings list")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Trainee trainings returned successfully"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Invalid username or password"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    @GetMapping("/trainee")
    public ResponseEntity<List<TraineeTrainingResponse>> getTraineeTrainings(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestParam String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @RequestParam(required = false) String trainerName,
            @RequestParam(required = false) String trainingType
    ) {
        authenticationService.authenticateBasic(authorizationHeader);

        Collection<Training> trainings = traineeService.getTraineeTrainingsByCriteria(
                username,
                periodFrom,
                periodTo,
                trainerName,
                trainingType
        );

        List<TraineeTrainingResponse> response = trainings.stream()
                .map(training -> new TraineeTrainingResponse(
                        training.getTrainingName(),
                        training.getTrainingDate(),
                        training.getTrainingType().getName(),
                        training.getTrainingDurationMinutes(),
                        training.getTrainer().getUserName()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Get trainer trainings list")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Trainer trainings returned successfully"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Invalid username or password"),
            @ApiResponse(code = 404, message = "Trainer not found")
    })
    @GetMapping("/trainer")
    public ResponseEntity<List<TrainerTrainingResponse>> getTrainerTrainings(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestParam String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @RequestParam(required = false) String traineeName
    ) {
        authenticationService.authenticateBasic(authorizationHeader);

        Collection<Training> trainings = trainerService.getTrainerTrainingsByCriteria(
                username,
                periodFrom,
                periodTo,
                traineeName
        );

        List<TrainerTrainingResponse> response = trainings.stream()
                .map(training -> new TrainerTrainingResponse(
                        training.getTrainingName(),
                        training.getTrainingDate(),
                        training.getTrainingType().getName(),
                        training.getTrainingDurationMinutes(),
                        training.getTrainee().getUserName()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Add training")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Training added successfully"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Invalid username or password"),
            @ApiResponse(code = 404, message = "Trainee or trainer not found")
    })
    @PostMapping
    public ResponseEntity<Void> addTraining(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @Valid @RequestBody AddTrainingRequest request
    ) {
        authenticationService.authenticateBasic(authorizationHeader);

        Trainee trainee = traineeService.selectTraineeByUsername(request.getTraineeUsername());
        Trainer trainer = trainerService.selectTrainerByUsername(request.getTrainerUsername());

        Training training = new Training(
                request.getTrainingName(),
                trainer.getTrainingType(),
                request.getTrainingDate(),
                request.getTrainingDuration(),
                trainer,
                trainee
        );

        trainingService.createTraining(training);
        gymMetricsService.incrementTrainingCreationCount();

        return ResponseEntity.ok().build();
    }
}