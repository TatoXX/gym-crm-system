package org.epam.gym_crm_system1.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import org.epam.gym_crm_system1.dto.request.ChangeLoginRequest;
import org.epam.gym_crm_system1.dto.request.LoginRequest;
import org.epam.gym_crm_system1.exception.InvalidCredentialsException;
import org.epam.gym_crm_system1.service.TraineeService;
import org.epam.gym_crm_system1.service.TrainerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Authentication")
@RestController
@RequestMapping("/api")
public class AuthController {

    private final TraineeService traineeService;
    private final TrainerService trainerService;

    public AuthController(TraineeService traineeService, TrainerService trainerService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
    }

    @ApiOperation(value = "Login user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Login successful"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Invalid username or password")
    })
    @GetMapping("/login")
    public ResponseEntity<Void> login(@Valid @ModelAttribute LoginRequest request) {
        boolean traineeCredentialsValid = traineeService.isTraineeCredentialsValid(
                request.getUsername(),
                request.getPassword()
        );

        boolean trainerCredentialsValid = trainerService.isTrainerCredentialsValid(
                request.getUsername(),
                request.getPassword()
        );

        if (!traineeCredentialsValid && !trainerCredentialsValid) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Change user password")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Password changed successfully"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Invalid username or password")
    })
    @PutMapping("/login")
    public ResponseEntity<Void> changeLogin(@Valid @RequestBody ChangeLoginRequest request) {
        boolean traineeCredentialsValid = traineeService.isTraineeCredentialsValid(
                request.getUsername(),
                request.getOldPassword()
        );

        if (traineeCredentialsValid) {
            traineeService.changeTraineePassword(
                    request.getUsername(),
                    request.getOldPassword(),
                    request.getNewPassword()
            );

            return ResponseEntity.ok().build();
        }

        boolean trainerCredentialsValid = trainerService.isTrainerCredentialsValid(
                request.getUsername(),
                request.getOldPassword()
        );

        if (trainerCredentialsValid) {
            trainerService.changeTrainerPassword(
                    request.getUsername(),
                    request.getOldPassword(),
                    request.getNewPassword()
            );

            return ResponseEntity.ok().build();
        }

        throw new InvalidCredentialsException("Invalid username or password");
    }
}