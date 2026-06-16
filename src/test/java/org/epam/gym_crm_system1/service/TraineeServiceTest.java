package org.epam.gym_crm_system1.service;

import org.epam.gym_crm_system1.dao.TraineeDao;
import org.epam.gym_crm_system1.exception.EntityNotFoundException;
import org.epam.gym_crm_system1.exception.InvalidCredentialsException;
import org.epam.gym_crm_system1.exception.ProfileStatusException;
import org.epam.gym_crm_system1.exception.ValidationException;
import org.epam.gym_crm_system1.model.Trainee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TraineeServiceTest {

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private TraineeDao traineeDao;

    @Test
    void shouldCreateTrainee() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        traineeService.createTrainee(trainee);

        Trainee savedTrainee =
                traineeDao.findTraineeById(trainee.getId());

        assertNotNull(savedTrainee);
        assertEquals("John.Smith", savedTrainee.getUserName());
        assertNotNull(savedTrainee.getPassword());
        assertEquals(10, savedTrainee.getPassword().length());
        assertTrue(savedTrainee.getIsActive());
    }

    @Test
    void shouldValidateTraineeCredentials() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        traineeService.createTrainee(trainee);

        boolean result =
                traineeService.isTraineeCredentialsValid(
                        trainee.getUserName(),
                        trainee.getPassword()
                );

        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenTraineePasswordIsWrong() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        traineeService.createTrainee(trainee);

        boolean result =
                traineeService.isTraineeCredentialsValid(
                        trainee.getUserName(),
                        "wrongPassword"
                );

        assertFalse(result);
    }

    @Test
    void shouldChangeTraineePassword() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        traineeService.createTrainee(trainee);

        String oldPassword = trainee.getPassword();

        traineeService.changeTraineePassword(
                trainee.getUserName(),
                oldPassword,
                "newPassword123"
        );

        Trainee updatedTrainee =
                traineeDao.findTraineeById(trainee.getId());

        assertEquals("newPassword123",
                updatedTrainee.getPassword());
    }

    @Test
    void shouldThrowExceptionWhenOldTraineePasswordIsWrong() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        traineeService.createTrainee(trainee);

        assertThrows(
                InvalidCredentialsException.class,
                () -> traineeService.changeTraineePassword(
                        trainee.getUserName(),
                        "wrongPassword",
                        "newPassword123"
                )
        );
    }


    @Test
    void shouldDeactivateTrainee() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        traineeService.createTrainee(trainee);

        traineeService.deactivateTrainee(trainee.getUserName());

        Trainee updatedTrainee =
                traineeDao.findTraineeById(trainee.getId());

        assertFalse(updatedTrainee.getIsActive());
    }

    @Test
    void shouldActivateTrainee() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        traineeService.createTrainee(trainee);

        traineeService.deactivateTrainee(trainee.getUserName());
        traineeService.activateTrainee(trainee.getUserName());

        Trainee updatedTrainee =
                traineeDao.findTraineeById(trainee.getId());

        assertTrue(updatedTrainee.getIsActive());
    }

    @Test
    void shouldThrowExceptionWhenTraineeAlreadyActive() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        traineeService.createTrainee(trainee);

        assertThrows(
                ProfileStatusException.class,
                () -> traineeService.activateTrainee(trainee.getUserName())
        );
    }

    @Test
    void shouldThrowExceptionWhenTraineeAlreadyInactive() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        traineeService.createTrainee(trainee);

        traineeService.deactivateTrainee(trainee.getUserName());

        assertThrows(
                ProfileStatusException.class,
                () -> traineeService.deactivateTrainee(trainee.getUserName())
        );
    }

    @Test
    void shouldDeleteTraineeByUsername() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        traineeService.createTrainee(trainee);

        String username = trainee.getUserName();
        int traineeId = trainee.getId();

        traineeService.deleteTraineeByUsername(username);

        assertNull(
                traineeDao.findTraineeById(traineeId)
        );
    }

    @Test
    void shouldThrowExceptionWhenDeletingUnknownTraineeUsername() {

        assertThrows(
                EntityNotFoundException.class,
                () -> traineeService.deleteTraineeByUsername("Unknown.User")
        );
    }

    @Test
    void shouldThrowExceptionWhenDeletingTraineeUsernameIsBlank() {

        assertThrows(
                ValidationException.class,
                () -> traineeService.deleteTraineeByUsername(" ")
        );
    }

    @Test
    void shouldSelectTraineeByUsername() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        traineeService.createTrainee(trainee);

        Trainee foundTrainee =
                traineeService.selectTraineeByUsername(
                        trainee.getUserName()
                );

        assertNotNull(foundTrainee);
        assertEquals(trainee.getUserName(),
                foundTrainee.getUserName());
        assertEquals("John",
                foundTrainee.getFirstName());
    }

    @Test
    void shouldThrowExceptionWhenTraineeUsernameNotFound() {

        assertThrows(
                EntityNotFoundException.class,
                () -> traineeService.selectTraineeByUsername("Unknown.User")
        );
    }

    

}