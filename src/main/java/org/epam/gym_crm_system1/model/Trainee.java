package org.epam.gym_crm_system1.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "trainees")
public class Trainee extends User {

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "address")
    private String address;

    @ManyToMany(mappedBy = "trainees")
    private Set<Trainer> trainers = new HashSet<>();

    @OneToMany(mappedBy = "trainee", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Training> trainings = new ArrayList<>();

    public Trainee() {
    }

    public Trainee(String firstName, String lastName, String address, LocalDate dateOfBirth) {
        super(firstName, lastName, null, null, false);
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public Set<Trainer> getTrainers() {
        return trainers;
    }

    public void setTrainers(Set<Trainer> trainers) {
        this.trainers = trainers;
    }


    public List<Training> getTrainings() {
        return trainings;
    }

    public void setTrainings(List<Training> trainings) {
        this.trainings = trainings;
    }

    @Override
    public String toString() {
        return "Trainee{" +
                "id=" + getId() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", userName='" + getUserName() + '\'' +
                ", isActive=" + getIsActive() +
                ", dateOfBirth=" + dateOfBirth +
                ", address='" + address + '\'' +
                '}';
    }
}