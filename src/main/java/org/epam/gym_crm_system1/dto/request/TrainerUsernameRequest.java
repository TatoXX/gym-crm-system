package org.epam.gym_crm_system1.dto.request;

import jakarta.validation.constraints.NotBlank;

public class TrainerUsernameRequest {

    @NotBlank(message = "Trainer username is required")
    private String trainerUsername;

    public TrainerUsernameRequest() {
    }

    public String getTrainerUsername() {
        return trainerUsername;
    }

    public void setTrainerUsername(String trainerUsername) {
        this.trainerUsername = trainerUsername;
    }
}