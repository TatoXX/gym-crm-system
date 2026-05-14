package org.epam.gym_crm_system1.model;

public class Trainer extends User{
    private Specialization specialization;
    private int userId;

    public Trainer(){

    }
    public Trainer(String firstName, String lastName, Specialization specialization, int userId) {
        setFirstName(firstName);
        setLastName(lastName);

        this.specialization = specialization;
        this.userId = userId;
    }


    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Trainer{" +
                "specialization=" + specialization +
                ", userId=" + userId +
                '}';
    }
}
