package org.epam.gym_crm_system1.storage;


import org.epam.gym_crm_system1.model.Trainee;
import org.epam.gym_crm_system1.model.Trainer;
import org.epam.gym_crm_system1.model.Training;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Storage {

   
    private final Map<Integer,Trainee> trainees = new HashMap<>();
    private final Map<Integer, Trainer> trainers = new HashMap<>();
    private final Map<Integer, Training> trainings = new HashMap<>();

    public Map<Integer, Trainee> getTrainees() {
        return trainees;
    }

    public Map<Integer, Trainer> getTrainers() {
        return trainers;
    }

    public Map<Integer, Training> getTrainings() {
        return trainings;
    }


}
