package org.epam.gym_crm_system1.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "trainers")
public class Trainer extends User {

    @ManyToOne
    @JoinColumn(name = "training_type_id", nullable = false)
    private TrainingType trainingType;

    @ManyToMany
    @JoinTable(
            name = "trainer_trainee",
            joinColumns = @JoinColumn(name = "trainer_id"),
            inverseJoinColumns = @JoinColumn(name = "trainee_id")
    )
    private Set<Trainee> trainees = new HashSet<>();

    public Trainer() {
    }

    public Trainer(String firstName, String lastName, TrainingType trainingType) {
        super(firstName, lastName, null, null, false);
        this.trainingType = trainingType;
    }

    public TrainingType getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(TrainingType trainingType) {
        this.trainingType = trainingType;
    }


    public Set<Trainee> getTrainees() {
        return trainees;
    }

    public void setTrainees(Set<Trainee> trainees) {
        this.trainees = trainees;
    }

    @Override
    public String toString() {
        return "Trainer{" +
                "id=" + getId() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", userName='" + getUserName() + '\'' +
                ", isActive=" + getIsActive() +
                ", trainingType=" + trainingType +
                '}';
    }
}