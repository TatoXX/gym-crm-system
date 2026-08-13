package org.epam.gym_crm_system1.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.epam.gym_crm_system1.dto.response.TrainingTypeResponse;
import org.epam.gym_crm_system1.model.TrainingType;
import org.epam.gym_crm_system1.service.AuthenticationService;
import org.epam.gym_crm_system1.service.TrainingTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "Training Types")
@RestController
@RequestMapping("/api/training-types")
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;
    private final AuthenticationService authenticationService;

    public TrainingTypeController(TrainingTypeService trainingTypeService,
                                  AuthenticationService authenticationService) {
        this.trainingTypeService = trainingTypeService;
        this.authenticationService = authenticationService;
    }

    @ApiOperation(value = "Get training types")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Training types returned successfully"),
            @ApiResponse(code = 401, message = "Invalid username or password")
    })
    @GetMapping
    public ResponseEntity<List<TrainingTypeResponse>> getTrainingTypes(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        authenticationService.authenticateBasic(authorizationHeader);

        Collection<TrainingType> trainingTypes = trainingTypeService.findAllTrainingTypes();

        List<TrainingTypeResponse> response = trainingTypes.stream()
                .map(trainingType -> new TrainingTypeResponse(
                        trainingType.getId(),
                        trainingType.getName()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}