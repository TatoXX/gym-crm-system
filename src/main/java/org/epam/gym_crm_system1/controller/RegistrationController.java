package org.epam.gym_crm_system1.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import org.epam.gym_crm_system1.dto.request.TraineeRegistrationRequest;
import org.epam.gym_crm_system1.dto.request.TrainerRegistrationRequest;
import org.epam.gym_crm_system1.dto.response.CredentialsResponse;
import org.epam.gym_crm_system1.metrics.GymMetricsService;
import org.epam.gym_crm_system1.model.Trainee;
import org.epam.gym_crm_system1.model.Trainer;
import org.epam.gym_crm_system1.model.TrainingType;
import org.epam.gym_crm_system1.service.TraineeService;
import org.epam.gym_crm_system1.service.TrainerService;
import org.epam.gym_crm_system1.service.TrainingTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Registration")
@RestController
@RequestMapping("/api")
public class RegistrationController {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingTypeService trainingTypeService;
    private final GymMetricsService gymMetricsService;

    public RegistrationController(TraineeService traineeService,
                                  TrainerService trainerService,
                                  TrainingTypeService trainingTypeService,
                                  GymMetricsService gymMetricsService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingTypeService = trainingTypeService;
        this.gymMetricsService = gymMetricsService;
    }

    @ApiOperation(value = "Register trainee")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Trainee registered successfully"),
            @ApiResponse(code = 400, message = "Validation error")
    })
    @PostMapping("/trainees")
    public ResponseEntity<CredentialsResponse> registerTrainee(
            @Valid @RequestBody TraineeRegistrationRequest request
    ) {
        Trainee trainee = new Trainee(
                request.getFirstName(),
                request.getLastName(),
                request.getAddress(),
                request.getDateOfBirth()
        );

        String password = traineeService.createTrainee(trainee);

        gymMetricsService.incrementTraineeRegistrationCount();

        CredentialsResponse response = new CredentialsResponse(
                trainee.getUserName(),
                password
        );

        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Register trainer")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Trainer registered successfully"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 404, message = "Training type not found")
    })
    @PostMapping("/trainers")
    public ResponseEntity<CredentialsResponse> registerTrainer(
            @Valid @RequestBody TrainerRegistrationRequest request
    ) {
        TrainingType specialization = trainingTypeService.findTrainingTypeById(
                request.getSpecializationId()
        );

        Trainer trainer = new Trainer(
                request.getFirstName(),
                request.getLastName(),
                specialization
        );

        String password = trainerService.createTrainer(trainer);
        gymMetricsService.incrementTrainerRegistrationCount();

        CredentialsResponse response = new CredentialsResponse(
                trainer.getUserName(),
                password
        );

        return ResponseEntity.ok(response);
    }
}