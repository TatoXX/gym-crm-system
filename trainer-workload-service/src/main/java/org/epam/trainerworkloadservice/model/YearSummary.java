package org.epam.trainerworkloadservice.model;

import java.util.ArrayList;
import java.util.List;

public class YearSummary {

    private Integer year;

    private List<MonthSummary> months = new ArrayList<>();

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public List<MonthSummary> getMonths() {
        return months;
    }

    public void setMonths(List<MonthSummary> months) {
        this.months = months;
    }
}