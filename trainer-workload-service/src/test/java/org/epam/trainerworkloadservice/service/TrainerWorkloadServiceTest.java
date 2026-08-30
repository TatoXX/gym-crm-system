package org.epam.trainerworkloadservice.service;

import org.epam.trainerworkloadservice.dto.ActionType;
import org.epam.trainerworkloadservice.dto.request.TrainerWorkloadRequest;
import org.epam.trainerworkloadservice.model.MonthSummary;
import org.epam.trainerworkloadservice.model.TrainerWorkload;
import org.epam.trainerworkloadservice.model.YearSummary;
import org.epam.trainerworkloadservice.repository.TrainerWorkloadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadServiceTest {

    @Mock
    private TrainerWorkloadRepository trainerWorkloadRepository;

    @InjectMocks
    private TrainerWorkloadService trainerWorkloadService;

    @Test
    void shouldCreateTrainerWorkloadWhenAddingFirstTraining() {

        TrainerWorkloadRequest request =
                createRequest(ActionType.ADD, 40);

        when(trainerWorkloadRepository
                .findByTrainerUsername("Test.Trainer"))
                .thenReturn(Optional.empty());

        trainerWorkloadService.updateWorkload(request);

        ArgumentCaptor<TrainerWorkload> captor =
                ArgumentCaptor.forClass(TrainerWorkload.class);

        verify(trainerWorkloadRepository)
                .save(captor.capture());

        TrainerWorkload savedWorkload = captor.getValue();

        assertEquals(
                "Test.Trainer",
                savedWorkload.getTrainerUsername()
        );

        assertEquals(
                "Test",
                savedWorkload.getTrainerFirstName()
        );

        assertEquals(
                "Trainer",
                savedWorkload.getTrainerLastName()
        );

        assertTrue(savedWorkload.getTrainerStatus());

        assertEquals(1, savedWorkload.getYears().size());

        YearSummary yearSummary =
                savedWorkload.getYears().get(0);

        assertEquals(2026, yearSummary.getYear());
        assertEquals(1, yearSummary.getMonths().size());

        MonthSummary monthSummary =
                yearSummary.getMonths().get(0);

        assertEquals(8, monthSummary.getMonth());
        assertEquals(
                40,
                monthSummary.getTrainingSummaryDuration()
        );
    }

    @Test
    void shouldAddDurationToExistingMonthlyWorkload() {

        TrainerWorkload existingWorkload =
                createExistingWorkload(60);

        when(trainerWorkloadRepository
                .findByTrainerUsername("Test.Trainer"))
                .thenReturn(Optional.of(existingWorkload));

        TrainerWorkloadRequest request =
                createRequest(ActionType.ADD, 30);

        trainerWorkloadService.updateWorkload(request);

        MonthSummary monthSummary =
                existingWorkload
                        .getYears()
                        .get(0)
                        .getMonths()
                        .get(0);

        assertEquals(
                90,
                monthSummary.getTrainingSummaryDuration()
        );

        verify(trainerWorkloadRepository)
                .save(existingWorkload);
    }

    @Test
    void shouldSubtractDurationWhenTrainingIsDeleted() {

        TrainerWorkload existingWorkload =
                createExistingWorkload(90);

        when(trainerWorkloadRepository
                .findByTrainerUsername("Test.Trainer"))
                .thenReturn(Optional.of(existingWorkload));

        TrainerWorkloadRequest request =
                createRequest(ActionType.DELETE, 30);

        trainerWorkloadService.updateWorkload(request);

        MonthSummary monthSummary =
                existingWorkload
                        .getYears()
                        .get(0)
                        .getMonths()
                        .get(0);

        assertEquals(
                60,
                monthSummary.getTrainingSummaryDuration()
        );

        verify(trainerWorkloadRepository)
                .save(existingWorkload);
    }

    @Test
    void shouldThrowExceptionWhenDeleteWouldMakeDurationNegative() {

        TrainerWorkload existingWorkload =
                createExistingWorkload(20);

        when(trainerWorkloadRepository
                .findByTrainerUsername("Test.Trainer"))
                .thenReturn(Optional.of(existingWorkload));

        TrainerWorkloadRequest request =
                createRequest(ActionType.DELETE, 30);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> trainerWorkloadService
                                .updateWorkload(request)
                );

        assertEquals(
                "Training summary duration cannot be negative",
                exception.getMessage()
        );

        verify(
                trainerWorkloadRepository,
                never()
        ).save(any());
    }

    @Test
    void shouldReturnMonthlyWorkload() {

        TrainerWorkload existingWorkload =
                createExistingWorkload(75);

        when(trainerWorkloadRepository
                .findByTrainerUsername("Test.Trainer"))
                .thenReturn(Optional.of(existingWorkload));

        Integer duration =
                trainerWorkloadService.getMonthlyWorkload(
                        "Test.Trainer",
                        2026,
                        8
                );

        assertEquals(75, duration);
    }

    @Test
    void shouldReturnNullWhenTrainerWorkloadDoesNotExist() {

        when(trainerWorkloadRepository
                .findByTrainerUsername("Unknown.Trainer"))
                .thenReturn(Optional.empty());

        Integer duration =
                trainerWorkloadService.getMonthlyWorkload(
                        "Unknown.Trainer",
                        2026,
                        8
                );

        assertNull(duration);
    }

    @Test
    void shouldReturnNullWhenMonthlyWorkloadDoesNotExist() {

        TrainerWorkload existingWorkload =
                createExistingWorkload(75);

        when(trainerWorkloadRepository
                .findByTrainerUsername("Test.Trainer"))
                .thenReturn(Optional.of(existingWorkload));

        Integer duration =
                trainerWorkloadService.getMonthlyWorkload(
                        "Test.Trainer",
                        2026,
                        9
                );

        assertNull(duration);
    }

    private TrainerWorkloadRequest createRequest(
            ActionType actionType,
            int duration) {

        TrainerWorkloadRequest request =
                new TrainerWorkloadRequest();

        request.setTrainerUsername("Test.Trainer");
        request.setTrainerFirstName("Test");
        request.setTrainerLastName("Trainer");
        request.setIsActive(true);
        request.setTrainingDate(
                LocalDate.of(2026, 8, 30)
        );
        request.setTrainingDuration(duration);
        request.setActionType(actionType);

        return request;
    }

    private TrainerWorkload createExistingWorkload(
            int duration) {

        TrainerWorkload trainerWorkload =
                new TrainerWorkload();

        trainerWorkload.setTrainerUsername("Test.Trainer");
        trainerWorkload.setTrainerFirstName("Test");
        trainerWorkload.setTrainerLastName("Trainer");
        trainerWorkload.setTrainerStatus(true);

        YearSummary yearSummary =
                new YearSummary();

        yearSummary.setYear(2026);
        yearSummary.setTrainerWorkload(
                trainerWorkload
        );

        MonthSummary monthSummary =
                new MonthSummary();

        monthSummary.setMonth(8);
        monthSummary.setTrainingSummaryDuration(
                duration
        );
        monthSummary.setYearSummary(yearSummary);

        yearSummary
                .getMonths()
                .add(monthSummary);

        trainerWorkload
                .getYears()
                .add(yearSummary);

        return trainerWorkload;
    }
}