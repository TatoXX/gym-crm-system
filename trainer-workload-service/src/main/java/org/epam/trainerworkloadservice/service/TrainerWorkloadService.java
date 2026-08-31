package org.epam.trainerworkloadservice.service;

import org.epam.trainerworkloadservice.dto.ActionType;
import org.epam.trainerworkloadservice.dto.request.TrainerWorkloadRequest;
import org.epam.trainerworkloadservice.model.MonthSummary;
import org.epam.trainerworkloadservice.model.TrainerWorkload;
import org.epam.trainerworkloadservice.model.YearSummary;
import org.epam.trainerworkloadservice.repository.TrainerWorkloadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TrainerWorkloadService {

    private static final Logger logger =
            LoggerFactory.getLogger(TrainerWorkloadService.class);

    private final TrainerWorkloadRepository trainerWorkloadRepository;

    public TrainerWorkloadService(
            TrainerWorkloadRepository trainerWorkloadRepository) {

        this.trainerWorkloadRepository = trainerWorkloadRepository;
    }

    public void updateWorkload(TrainerWorkloadRequest request) {

        logger.info(
                "Updating trainer workload. trainerUsername={}, actionType={}, trainingDate={}, trainingDuration={}",
                request.getTrainerUsername(),
                request.getActionType(),
                request.getTrainingDate(),
                request.getTrainingDuration()
        );

        TrainerWorkload trainerWorkload =
                trainerWorkloadRepository
                        .findByTrainerUsername(
                                request.getTrainerUsername()
                        )
                        .orElseGet(() ->
                                createTrainerWorkload(request)
                        );

        updateTrainerProfile(
                trainerWorkload,
                request
        );

        int yearValue =
                request.getTrainingDate().getYear();

        int monthValue =
                request.getTrainingDate().getMonthValue();

        YearSummary yearSummary =
                findOrCreateYearSummary(
                        trainerWorkload,
                        yearValue
                );

        MonthSummary monthSummary =
                findOrCreateMonthSummary(
                        yearSummary,
                        monthValue
                );

        updateTrainingDuration(
                monthSummary,
                request
        );

        trainerWorkloadRepository.save(
                trainerWorkload
        );

        logger.info(
                "Trainer workload updated successfully. trainerUsername={}, year={}, month={}, totalDuration={}",
                request.getTrainerUsername(),
                yearValue,
                monthValue,
                monthSummary.getTrainingSummaryDuration()
        );
    }

    private TrainerWorkload createTrainerWorkload(
            TrainerWorkloadRequest request) {

        logger.info(
                "Creating new trainer workload document. trainerUsername={}",
                request.getTrainerUsername()
        );

        TrainerWorkload trainerWorkload =
                new TrainerWorkload();

        trainerWorkload.setTrainerUsername(
                request.getTrainerUsername()
        );

        trainerWorkload.setTrainerFirstName(
                request.getTrainerFirstName()
        );

        trainerWorkload.setTrainerLastName(
                request.getTrainerLastName()
        );

        trainerWorkload.setTrainerStatus(
                request.getIsActive()
        );

        return trainerWorkload;
    }

    private void updateTrainerProfile(
            TrainerWorkload trainerWorkload,
            TrainerWorkloadRequest request) {

        trainerWorkload.setTrainerFirstName(
                request.getTrainerFirstName()
        );

        trainerWorkload.setTrainerLastName(
                request.getTrainerLastName()
        );

        trainerWorkload.setTrainerStatus(
                request.getIsActive()
        );
    }

    private YearSummary findOrCreateYearSummary(
            TrainerWorkload trainerWorkload,
            int yearValue) {

        return trainerWorkload.getYears()
                .stream()
                .filter(year ->
                        year.getYear().equals(yearValue)
                )
                .findFirst()
                .orElseGet(() ->
                        createYearSummary(
                                trainerWorkload,
                                yearValue
                        )
                );
    }

    private YearSummary createYearSummary(
            TrainerWorkload trainerWorkload,
            int yearValue) {

        logger.debug(
                "Creating year summary. trainerUsername={}, year={}",
                trainerWorkload.getTrainerUsername(),
                yearValue
        );

        YearSummary yearSummary =
                new YearSummary();

        yearSummary.setYear(yearValue);

        trainerWorkload
                .getYears()
                .add(yearSummary);

        return yearSummary;
    }

    private MonthSummary findOrCreateMonthSummary(
            YearSummary yearSummary,
            int monthValue) {

        return yearSummary.getMonths()
                .stream()
                .filter(month ->
                        month.getMonth().equals(monthValue)
                )
                .findFirst()
                .orElseGet(() ->
                        createMonthSummary(
                                yearSummary,
                                monthValue
                        )
                );
    }

    private MonthSummary createMonthSummary(
            YearSummary yearSummary,
            int monthValue) {

        logger.debug(
                "Creating month summary. year={}, month={}",
                yearSummary.getYear(),
                monthValue
        );

        MonthSummary monthSummary =
                new MonthSummary();

        monthSummary.setMonth(monthValue);

        monthSummary.setTrainingSummaryDuration(0);

        yearSummary
                .getMonths()
                .add(monthSummary);

        return monthSummary;
    }

    private void updateTrainingDuration(
            MonthSummary monthSummary,
            TrainerWorkloadRequest request) {

        int currentDuration =
                monthSummary.getTrainingSummaryDuration();

        if (request.getActionType()
                == ActionType.ADD) {

            monthSummary.setTrainingSummaryDuration(
                    currentDuration
                            + request.getTrainingDuration()
            );

            return;
        }

        int updatedDuration =
                currentDuration
                        - request.getTrainingDuration();

        if (updatedDuration < 0) {

            logger.warn(
                    "Trainer workload update rejected. trainerUsername={}, currentDuration={}, requestedDeleteDuration={}",
                    request.getTrainerUsername(),
                    currentDuration,
                    request.getTrainingDuration()
            );

            throw new IllegalArgumentException(
                    "Training summary duration cannot be negative"
            );
        }

        monthSummary.setTrainingSummaryDuration(
                updatedDuration
        );
    }

    public Integer getMonthlyWorkload(
            String trainerUsername,
            int yearValue,
            int monthValue) {

        logger.info(
                "Getting monthly trainer workload. trainerUsername={}, year={}, month={}",
                trainerUsername,
                yearValue,
                monthValue
        );

        TrainerWorkload trainerWorkload =
                trainerWorkloadRepository
                        .findByTrainerUsername(
                                trainerUsername
                        )
                        .orElse(null);

        if (trainerWorkload == null) {

            logger.warn(
                    "Trainer workload not found. trainerUsername={}",
                    trainerUsername
            );

            return null;
        }

        Integer duration =
                trainerWorkload.getYears()
                        .stream()
                        .filter(year ->
                                year.getYear()
                                        .equals(yearValue)
                        )
                        .flatMap(year ->
                                year.getMonths().stream()
                        )
                        .filter(month ->
                                month.getMonth()
                                        .equals(monthValue)
                        )
                        .map(
                                MonthSummary::
                                        getTrainingSummaryDuration
                        )
                        .findFirst()
                        .orElse(null);

        if (duration == null) {

            logger.warn(
                    "Monthly trainer workload not found. trainerUsername={}, year={}, month={}",
                    trainerUsername,
                    yearValue,
                    monthValue
            );

        } else {

            logger.info(
                    "Monthly trainer workload retrieved successfully. trainerUsername={}, year={}, month={}, totalDuration={}",
                    trainerUsername,
                    yearValue,
                    monthValue,
                    duration
            );
        }

        return duration;
    }
}