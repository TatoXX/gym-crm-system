package org.epam.gym_crm_system1.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import org.epam.gym_crm_system1.dto.request.ChangeLoginRequest;
import org.epam.gym_crm_system1.dto.request.LoginRequest;
import org.epam.gym_crm_system1.exception.InvalidCredentialsException;
import org.epam.gym_crm_system1.metrics.GymMetricsService;
import org.epam.gym_crm_system1.service.TraineeService;
import org.epam.gym_crm_system1.service.TrainerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.epam.gym_crm_system1.dto.response.JwtResponse;
import org.epam.gym_crm_system1.security.JwtService;
import org.epam.gym_crm_system1.security.BruteForceProtectionService;


@Api(tags = "Authentication")
@RestController
@RequestMapping("/api")
public class AuthController {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final GymMetricsService gymMetricsService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final BruteForceProtectionService bruteForceProtectionService;

    public AuthController(
            TraineeService traineeService,
            TrainerService trainerService,
            GymMetricsService gymMetricsService,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            BruteForceProtectionService bruteForceProtectionService
            ) {

        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.gymMetricsService = gymMetricsService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.bruteForceProtectionService = bruteForceProtectionService;
    }

    @ApiOperation(value = "Login user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Login successful"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Invalid username or password")
    })
    @GetMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @ModelAttribute LoginRequest request) {

        if (bruteForceProtectionService.isBlocked(request.getUsername())) {
            throw new InvalidCredentialsException(
                    "User is temporarily blocked. Try again later."
            );
        }

        try {
            authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            gymMetricsService.incrementLoginSuccessCount();

            String token =
                    jwtService.generate(request.getUsername());

            bruteForceProtectionService.loginSucceeded(request.getUsername());

            return ResponseEntity.ok(
                    new JwtResponse(token)
            );

        } catch (AuthenticationException exception) {

            gymMetricsService.incrementLoginFailureCount();

            bruteForceProtectionService.loginFailed(request.getUsername());


            throw new InvalidCredentialsException(
                    "Invalid username or password"
            );
        }
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