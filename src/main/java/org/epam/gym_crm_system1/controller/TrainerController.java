package org.epam.gym_crm_system1.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import org.epam.gym_crm_system1.dto.request.UpdateActiveStatusRequest;
import org.epam.gym_crm_system1.dto.request.UpdateTrainerProfileRequest;
import org.epam.gym_crm_system1.dto.response.TrainerProfileResponse;
import org.epam.gym_crm_system1.dto.response.UpdateTrainerProfileResponse;
import org.epam.gym_crm_system1.mapper.ResponseMapper;
import org.epam.gym_crm_system1.model.Trainer;
import org.epam.gym_crm_system1.service.TrainerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Trainers")
@RestController
@RequestMapping("/api/trainers")
public class TrainerController {

    private final TrainerService trainerService;
    private final ResponseMapper responseMapper;

    public TrainerController(TrainerService trainerService,
                             ResponseMapper responseMapper) {
        this.trainerService = trainerService;
        this.responseMapper = responseMapper;
    }

    @ApiOperation("Get trainer profile")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Trainer profile returned successfully"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Invalid username or password"),
            @ApiResponse(code = 404, message = "Trainer not found")
    })
    @GetMapping
    public ResponseEntity<TrainerProfileResponse> getTrainerProfile(
            @RequestParam String username
    ){

        Trainer trainer = trainerService.selectTrainerByUsername(username);

        TrainerProfileResponse response = responseMapper.toTrainerProfileResponse(trainer);

        return ResponseEntity.ok(response);
    }


    @ApiOperation(value = "Update trainer profile")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Trainer profile updated successfully"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Invalid username or password"),
            @ApiResponse(code = 404, message = "Trainer not found")
    })
    @PutMapping
    public ResponseEntity<UpdateTrainerProfileResponse> updateTrainerProfile(
            @Valid @RequestBody UpdateTrainerProfileRequest request
    ){


        Trainer trainer = trainerService.selectTrainerByUsername(request.getUsername());

        trainer.setFirstName(request.getFirstName());
        trainer.setLastName(request.getLastName());
        trainer.setIsActive(request.getIsActive());

        trainerService.updateTrainer(trainer);

        UpdateTrainerProfileResponse response = responseMapper.toUpdateTrainerProfileResponse(trainer);

        return ResponseEntity.ok(response);

    }

    @ApiOperation(value = "Activate or deactivate trainer")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Trainer status changed successfully"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Invalid username or password"),
            @ApiResponse(code = 404, message = "Trainer not found")
    })
    @PatchMapping("/status")
    public ResponseEntity<Void> updateTrainerStatus(
            @Valid @RequestBody UpdateActiveStatusRequest request


    ){



       if (request.getIsActive()) {
           trainerService.activateTrainer(request.getUsername());
       } else {
           trainerService.deactivateTrainer(request.getUsername());
       }

       return ResponseEntity.ok().build();
    }



}
