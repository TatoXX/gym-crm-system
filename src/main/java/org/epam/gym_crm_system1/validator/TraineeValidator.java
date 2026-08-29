package org.epam.gym_crm_system1.validator;

import org.epam.gym_crm_system1.exception.ValidationException;

import java.time.LocalDate;

public class TraineeValidator {

    public void validateAddress(String address) {
        if(address == null || address.isBlank()) {
            throw new ValidationException("Address is required");
        }
        address = address.trim();

        if(address.length() < 3 || address.length() > 55) {
            throw new ValidationException("Address length must be between 3 and 55 characters");
        }

    }

    public void validateDateOfBirth(LocalDate dateOfBirth) {
        if(dateOfBirth == null) {
            throw new ValidationException("Date of birth is required");
        }

        if(dateOfBirth.isAfter(LocalDate.now())) {
            throw new ValidationException("Date of birth is future");
        }

        LocalDate oldestAllowedDate = LocalDate.now().minusYears(120);

        if(dateOfBirth.isBefore(oldestAllowedDate)){
            throw new ValidationException("Date of birth is future");
        }


    }
}
