package org.epam.trainerworkloadservice.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "year_summaries",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"trainer_workload_id", "training_year"}
        )
)
public class YearSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "training_year", nullable = false)
    private Integer year;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_workload_id", nullable = false)
    private TrainerWorkload trainerWorkload;

    @OneToMany(
            mappedBy = "yearSummary",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<MonthSummary> months = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public TrainerWorkload getTrainerWorkload() {
        return trainerWorkload;
    }

    public void setTrainerWorkload(TrainerWorkload trainerWorkload) {
        this.trainerWorkload = trainerWorkload;
    }

    public List<MonthSummary> getMonths() {
        return months;
    }

    public void setMonths(List<MonthSummary> months) {
        this.months = months;
    }
}