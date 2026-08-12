package org.epam.gym_crm_system1.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import org.epam.gym_crm_system1.dto.request.UpdateTraineeProfileRequest;
import org.epam.gym_crm_system1.dto.request.UpdateTraineeTrainersRequest;
import org.epam.gym_crm_system1.dto.response.TraineeProfileResponse;
import org.epam.gym_crm_system1.dto.response.TrainerSummaryResponse;
import org.epam.gym_crm_system1.dto.response.UpdateTraineeProfileResponse;
import org.epam.gym_crm_system1.mapper.ResponseMapper;
import org.epam.gym_crm_system1.model.Trainee;
import org.epam.gym_crm_system1.model.Trainer;
 import org.epam.gym_crm_system1.service.TraineeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.epam.gym_crm_system1.dto.request.TrainerUsernameRequest;
import org.epam.gym_crm_system1.dto.request.UpdateActiveStatusRequest;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Api(tags = "Trainees")
@RestController
@RequestMapping("/api/trainees")
public class TraineeController {

    private final TraineeService traineeService;
    private final ResponseMapper responseMapper;

    public TraineeController(TraineeService traineeService,
                             ResponseMapper responseMapper) {
        this.traineeService = traineeService;
        this.responseMapper = responseMapper;
    }

    @ApiOperation(value = "Get trainee profile")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Trainee profile returned successfully"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Invalid username or password"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    @GetMapping
    public ResponseEntity<TraineeProfileResponse> getTraineeProfile(
            @RequestParam String username) {


        Trainee trainee  = traineeService.selectTraineeByUsername(username);

        TraineeProfileResponse traineeProfileResponse = responseMapper.toTraineeProfileResponse(trainee);
        return ResponseEntity.ok(traineeProfileResponse);

    }


    @ApiOperation(value = "Update trainee profile")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Trainee profile updated successfully"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Invalid username or password"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    @PutMapping
    public ResponseEntity<UpdateTraineeProfileResponse> updateTraineeProfile(
            @Valid @RequestBody UpdateTraineeProfileRequest request
    ) {

        Trainee trainee = traineeService.selectTraineeByUsername(request.getUsername());

        trainee.setFirstName(request.getFirstName());
        trainee.setLastName(request.getLastName());
        trainee.setDateOfBirth(request.getDateOfBirth());
        trainee.setAddress(request.getAddress());
        trainee.setIsActive(request.getIsActive());

        traineeService.updateTrainee(trainee);

        UpdateTraineeProfileResponse response = responseMapper.toUpdateTraineeProfileResponse(trainee);

        return ResponseEntity.ok(response);

    }

    @ApiOperation(value = "Delete trainee profile")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Trainee profile deleted successfully"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Invalid username or password"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteTraineeProfile(
            @RequestParam String username
    ){

        traineeService.deleteTraineeByUsername(username);

        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Get active trainers not assigned to trainee")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Trainers returned successfully"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Invalid username or password"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    @GetMapping("/not-assigned-trainers")
    public ResponseEntity<List<TrainerSummaryResponse>> getNotAssignedActiveTrainers(
            @RequestParam String username
    ) {

        Collection<Trainer> trainers = traineeService.getTrainersNotAssignedToTrainee(username);

        List<TrainerSummaryResponse> response = responseMapper.toTrainerSummaryResponseList(trainers);

        return ResponseEntity.ok(response);
    }


    @ApiOperation(value = "Update trainee trainers list")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Trainee trainers list updated successfully"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Invalid username or password"),
            @ApiResponse(code = 404, message = "Trainee or trainer not found")
    })
    @PutMapping("/trainers")
    public ResponseEntity<List<TrainerSummaryResponse>> updateTraineeTrainersList(
            @Valid @RequestBody UpdateTraineeTrainersRequest request
    ) {

        List<String> trainerUsernames = request.getTrainers()
                .stream()
                .map(TrainerUsernameRequest::getTrainerUsername)
                .collect(Collectors.toList());

        traineeService.updateTraineeTrainersList(request.getTraineeUsername(), trainerUsernames);

        Trainee trainee = traineeService.selectTraineeByUsername(request.getTraineeUsername());

        List<TrainerSummaryResponse> response = responseMapper.toTrainerSummaryResponseList(
                trainee.getTrainers()
        );

        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Activate or deactivate trainee")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Trainee status changed successfully"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Invalid username or password"),
            @ApiResponse(code = 404, message = "Trainee not found")
    })
    @PatchMapping("/status")
    public ResponseEntity<Void> updateTraineeStatus(
            @Valid @RequestBody UpdateActiveStatusRequest request
    ) {

        if (request.getIsActive()) {
            traineeService.activateTrainee(request.getUsername());
        } else {
            traineeService.deactivateTrainee(request.getUsername());
        }

        return ResponseEntity.ok().build();
    }




}
