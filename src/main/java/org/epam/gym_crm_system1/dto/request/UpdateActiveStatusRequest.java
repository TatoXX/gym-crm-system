package org.epam.gym_crm_system1.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateActiveStatusRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotNull(message = "Active status is required")
    private Boolean isActive;

    public UpdateActiveStatusRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}