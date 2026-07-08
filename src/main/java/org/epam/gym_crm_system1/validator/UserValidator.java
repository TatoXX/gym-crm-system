package org.epam.gym_crm_system1.validator;

import org.epam.gym_crm_system1.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    public void validateName(String name, String fieldName) {

        if (name == null || name.isBlank()) {
            throw new ValidationException(fieldName + " is required");
        }

        name = name.trim();

        if (name.length() < 2 || name.length() > 30) {
            throw new ValidationException(fieldName + " must be between 2 and 30 characters");
        }

        if (!name.matches("^[A-Z][a-zA-Z]*(?:[ '-][a-zA-Z]+)*$")) {
            throw new ValidationException(fieldName + " has invalid format");
        }
    }

    public void validateUsername(String userName) {

        if (userName == null || userName.isBlank()) {
            throw new ValidationException("Username is required");
        }

        userName = userName.trim();

        if (userName.length() < 3 || userName.length() > 70) {
            throw new ValidationException("Username must be between 3 and 70 characters");
        }

        if (!userName.matches("^[A-Z][a-zA-Z]*(?:[ '-][a-zA-Z]+)*\\.[A-Z][a-zA-Z]*(?:[ '-][a-zA-Z]+)*\\d*$")) {
            throw new ValidationException("Username has invalid format");
        }
    }

    public void validatePassword(String password) {

        if (password == null || password.isBlank()) {
            throw new ValidationException("Password is required");
        }

        if (password.length() < 6 || password.length() > 30) {
            throw new ValidationException("Password must be between 6 and 30 characters");
        }
    }

    public void validateId(int id) {

        if (id <= 0) {
            throw new ValidationException("Id must be positive");
        }
    }

    public void validateCredentials(String username, String password) {

        validateUsername(username);
        validatePassword(password);
    }
}