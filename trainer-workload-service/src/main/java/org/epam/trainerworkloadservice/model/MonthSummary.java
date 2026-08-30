package org.epam.trainerworkloadservice.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "month_summaries",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"year_summary_id", "training_month"}
        )
)
public class MonthSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "training_month", nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer trainingSummaryDuration = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "year_summary_id", nullable = false)
    private YearSummary yearSummary;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getTrainingSummaryDuration() {
        return trainingSummaryDuration;
    }

    public void setTrainingSummaryDuration(Integer trainingSummaryDuration) {
        this.trainingSummaryDuration = trainingSummaryDuration;
    }

    public YearSummary getYearSummary() {
        return yearSummary;
    }

    public void setYearSummary(YearSummary yearSummary) {
        this.yearSummary = yearSummary;
    }
}