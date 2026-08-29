package org.epam.gym_crm_system1.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "trainees")
public class Trainee {

    @Id
    private int id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @MapsId
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user = new User();

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

    public Trainee(String firstName,
                   String lastName,
                   String address,
                   LocalDate dateOfBirth) {

        this.user = new User(firstName, lastName, null, null, false);
        this.address = address;
        this.dateOfBirth = dateOfBirth;
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
                "id=" + id +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", userName='" + getUserName() + '\'' +
                ", isActive=" + getIsActive() +
                ", dateOfBirth=" + dateOfBirth +
                ", address='" + address + '\'' +
                '}';
    }
}