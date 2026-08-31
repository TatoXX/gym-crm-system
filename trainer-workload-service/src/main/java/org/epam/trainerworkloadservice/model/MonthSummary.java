package org.epam.trainerworkloadservice.model;

public class MonthSummary {

    private Integer month;

    private Integer trainingSummaryDuration = 0;

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getTrainingSummaryDuration() {
        return trainingSummaryDuration;
    }

    public void setTrainingSummaryDuration(
            Integer trainingSummaryDuration) {

        this.trainingSummaryDuration =
                trainingSummaryDuration;
    }
}