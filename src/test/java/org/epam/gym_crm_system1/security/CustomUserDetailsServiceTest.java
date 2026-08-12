package org.epam.gym_crm_system1.security;

import org.epam.gym_crm_system1.model.Trainee;
import org.epam.gym_crm_system1.model.Trainer;
import org.epam.gym_crm_system1.repository.TraineeRepository;
import org.epam.gym_crm_system1.repository.TrainerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_WhenTraineeExists_ShouldReturnTraineeUserDetails() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        trainee.setUserName(
                "John.Smith"
        );

        trainee.setPassword(
                "$2a$10$encodedPassword"
        );

        trainee.setIsActive(true);

        when(
                traineeRepository
                        .findTraineeByUsername(
                                "John.Smith"
                        )
        ).thenReturn(trainee);

        UserDetails result =
                customUserDetailsService
                        .loadUserByUsername(
                                "John.Smith"
                        );

        assertEquals(
                "John.Smith",
                result.getUsername()
        );

        assertEquals(
                "$2a$10$encodedPassword",
                result.getPassword()
        );

        assertTrue(result.isEnabled());

        assertTrue(
                result.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority
                                        .getAuthority()
                                        .equals(
                                                "ROLE_TRAINEE"
                                        )
                        )
        );

        verify(
                trainerRepository,
                never()
        ).findTrainerByUsername(any());
    }

    @Test
    void loadUserByUsername_WhenTraineeInactive_ShouldReturnDisabledUser() {

        Trainee trainee =
                new Trainee(
                        "John",
                        "Smith",
                        "Tbilisi",
                        LocalDate.of(2000, 1, 1)
                );

        trainee.setUserName(
                "John.Smith"
        );

        trainee.setPassword(
                "encoded"
        );

        trainee.setIsActive(false);

        when(
                traineeRepository
                        .findTraineeByUsername(
                                "John.Smith"
                        )
        ).thenReturn(trainee);

        UserDetails result =
                customUserDetailsService
                        .loadUserByUsername(
                                "John.Smith"
                        );

        assertFalse(
                result.isEnabled()
        );
    }

    @Test
    void loadUserByUsername_WhenTrainerExists_ShouldReturnTrainerUserDetails() {

        Trainer trainer =
                new Trainer(
                        "Jane",
                        "Smith",
                        null
                );

        trainer.setUserName(
                "Jane.Smith"
        );

        trainer.setPassword(
                "$2a$10$encodedTrainerPassword"
        );

        trainer.setIsActive(true);

        when(
                traineeRepository
                        .findTraineeByUsername(
                                "Jane.Smith"
                        )
        ).thenReturn(null);

        when(
                trainerRepository
                        .findTrainerByUsername(
                                "Jane.Smith"
                        )
        ).thenReturn(trainer);

        UserDetails result =
                customUserDetailsService
                        .loadUserByUsername(
                                "Jane.Smith"
                        );

        assertEquals(
                "Jane.Smith",
                result.getUsername()
        );

        assertEquals(
                "$2a$10$encodedTrainerPassword",
                result.getPassword()
        );

        assertTrue(result.isEnabled());

        assertTrue(
                result.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority
                                        .getAuthority()
                                        .equals(
                                                "ROLE_TRAINER"
                                        )
                        )
        );
    }

    @Test
    void loadUserByUsername_WhenTrainerInactive_ShouldReturnDisabledUser() {

        Trainer trainer =
                new Trainer(
                        "Jane",
                        "Smith",
                        null
                );

        trainer.setUserName(
                "Jane.Smith"
        );

        trainer.setPassword(
                "encoded"
        );

        trainer.setIsActive(false);

        when(
                traineeRepository
                        .findTraineeByUsername(
                                "Jane.Smith"
                        )
        ).thenReturn(null);

        when(
                trainerRepository
                        .findTrainerByUsername(
                                "Jane.Smith"
                        )
        ).thenReturn(trainer);

        UserDetails result =
                customUserDetailsService
                        .loadUserByUsername(
                                "Jane.Smith"
                        );

        assertFalse(
                result.isEnabled()
        );
    }

    @Test
    void loadUserByUsername_WhenUserDoesNotExist_ShouldThrowException() {

        when(
                traineeRepository
                        .findTraineeByUsername(
                                "Unknown.User"
                        )
        ).thenReturn(null);

        when(
                trainerRepository
                        .findTrainerByUsername(
                                "Unknown.User"
                        )
        ).thenReturn(null);

        assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService
                        .loadUserByUsername(
                                "Unknown.User"
                        )
        );
    }
}