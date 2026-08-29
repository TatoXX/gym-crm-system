package org.epam.gym_crm_system1.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "trainers")
public class Trainer {

    @Id
    private int id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @MapsId
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user = new User();

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

    public Trainer(String firstName,
                   String lastName,
                   TrainingType trainingType) {

        this.user = new User(firstName, lastName, null, null, false);
        this.trainingType = trainingType;
    }

    private User getOrCreateUser() {
        if (user == null) {
            user = new User();
        }

        return user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        getOrCreateUser().setId(id);
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public String getFirstName() {
        return user != null ? user.getFirstName() : null;
    }

    public void setFirstName(String firstName) {
        getOrCreateUser().setFirstName(firstName);
    }


    public String getLastName() {
        return user != null ? user.getLastName() : null;
    }

    public void setLastName(String lastName) {
        getOrCreateUser().setLastName(lastName);
    }


    public String getUserName() {
        return user != null ? user.getUserName() : null;
    }

    public void setUserName(String userName) {
        getOrCreateUser().setUserName(userName);
    }


    public String getPassword() {
        return user != null ? user.getPassword() : null;
    }

    public void setPassword(String password) {
        getOrCreateUser().setPassword(password);
    }


    public boolean getIsActive() {
        return user != null && user.getIsActive();
    }

    public void setIsActive(boolean active) {
        getOrCreateUser().setIsActive(active);
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
                "id=" + id +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", userName='" + getUserName() + '\'' +
                ", isActive=" + getIsActive() +
                ", trainingType=" + trainingType +
                '}';
    }
}