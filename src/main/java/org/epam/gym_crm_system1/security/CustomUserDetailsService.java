package org.epam.gym_crm_system1.security;

import org.epam.gym_crm_system1.model.Trainee;
import org.epam.gym_crm_system1.model.Trainer;
import org.epam.gym_crm_system1.repository.TraineeRepository;
import org.epam.gym_crm_system1.repository.TrainerRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService{

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    public CustomUserDetailsService(TraineeRepository traineeRepository,
                                    TrainerRepository trainerRepository) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Trainee trainee =
                traineeRepository.findTraineeByUsername(username);

        if (trainee != null) {
            return User.withUsername(trainee.getUserName())
                    .password(trainee.getPassword())
                    .roles("TRAINEE")
                    .disabled(!trainee.getIsActive())
                    .build();
        }

        Trainer trainer =
                trainerRepository.findTrainerByUsername(username);

        if (trainer != null) {
            return User.withUsername(trainer.getUserName())
                    .password(trainer.getPassword())
                    .roles("TRAINER")
                    .disabled(!trainer.getIsActive())
                    .build();
        }

        throw new UsernameNotFoundException(
                "User not found: " + username
        );
    }
}
