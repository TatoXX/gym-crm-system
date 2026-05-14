package org.epam.gym_crm_system1.model;

public class Trainer extends User{
    private TrainingType trainingType;
    private int userId;

    public Trainer(){

    }
    public Trainer(String firstName, String lastName, TrainingType trainingType, int userId) {
        super(firstName, lastName, null, null, false);

        this.trainingType = trainingType;
        this.userId = userId;
    }


    public TrainingType getSpecialization() {
        return trainingType;
    }

    public void setSpecialization(TrainingType trainingType) {
        this.trainingType = trainingType;
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
                "specialization=" + trainingType +
                ", userId=" + userId +
                '}';
    }
}
