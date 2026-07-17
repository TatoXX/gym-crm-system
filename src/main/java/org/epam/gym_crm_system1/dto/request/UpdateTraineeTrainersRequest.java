package org.epam.gym_crm_system1.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class UpdateTraineeTrainersRequest {

    @NotBlank(message = "Trainee username is required")
    private String traineeUsername;

    @NotEmpty(message = "Trainers list is required")
    @Valid
    private List<TrainerUsernameRequest> trainers;

    public UpdateTraineeTrainersRequest() {
    }

    public String getTraineeUsername() {
        return traineeUsername;
    }

    public void setTraineeUsername(String traineeUsername) {
        this.traineeUsername = traineeUsername;
    }

    public List<TrainerUsernameRequest> getTrainers() {
        return trainers;
    }

    public void setTrainers(List<TrainerUsernameRequest> trainers) {
        this.trainers = trainers;
    }
}