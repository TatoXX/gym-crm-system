package org.epam.gym_crm_system1.dto.response;

public class TrainingTypeResponse {

    private Integer trainingTypeId;
    private String trainingTypeName;

    public TrainingTypeResponse() {
    }

    public TrainingTypeResponse(Integer trainingTypeId, String trainingTypeName) {
        this.trainingTypeId = trainingTypeId;
        this.trainingTypeName = trainingTypeName;
    }

    public Integer getTrainingTypeId() {
        return trainingTypeId;
    }

    public void setTrainingTypeId(Integer trainingTypeId) {
        this.trainingTypeId = trainingTypeId;
    }

    public String getTrainingTypeName() {
        return trainingTypeName;
    }

    public void setTrainingTypeName(String trainingTypeName) {
        this.trainingTypeName = trainingTypeName;
    }
}