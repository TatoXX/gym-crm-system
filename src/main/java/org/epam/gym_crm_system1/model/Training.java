package org.epam.gym_crm_system1.model;

import java.time.LocalDate;

public class Training {
    private String trainingName;
    private TrainingType trainingType;
    private LocalDate trainingDate;
    private int trainingDurationMinutes;
    private int trainerId;
    private int traineeId;
    private int trainingId;
    public Training() {

    }

    public Training(String trainingName, TrainingType trainingType, LocalDate trainingDate, int trainingDurationMinutes, int trainerId, int traineeId, int trainingId) {
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainingDate = trainingDate;
        this.trainingDurationMinutes = trainingDurationMinutes;
        this.trainerId = trainerId;
        this.traineeId = traineeId;
        this.trainingId = trainingId;
    }

    public int getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(int trainingId) {
        this.trainingId = trainingId;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public TrainingType getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(TrainingType trainingType) {
        this.trainingType = trainingType;
    }

    public LocalDate getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(LocalDate trainingDate) {
        this.trainingDate = trainingDate;
    }

    public int getTrainingDurationMinutes() {
        return trainingDurationMinutes;
    }

    public void setTrainingDurationMinutes(int trainingDurationMinutes) {
        this.trainingDurationMinutes = trainingDurationMinutes;
    }

    public int getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
    }

    public int getTraineeId() {
        return traineeId;
    }

    public void setTraineeId(int traineeId) {
        this.traineeId = traineeId;
    }

    @Override
    public String toString() {
        return "Training{" +
                "trainingName='" + trainingName + '\'' +
                ", trainingType=" + trainingType +
                ", trainingDate=" + trainingDate +
                ", trainingDurationMinutes=" + trainingDurationMinutes +
                ", trainerId=" + trainerId +
                ", traineeId=" + traineeId +
                ", trainingId=" + trainingId +
                '}';
    }

}
