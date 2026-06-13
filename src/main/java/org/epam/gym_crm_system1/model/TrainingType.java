package org.epam.gym_crm_system1.model;

import jakarta.persistence.*;

@Entity
@Table(name = "training_types")
public class TrainingType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "training_type_name", nullable = false, unique = true)
    private String name;

    public TrainingType() {
    }

    public TrainingType(String name) {
        this.name = name;
    }

    public TrainingType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "TrainingType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}