package org.epam.trainerworkloadservice.component;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.epam.trainerworkloadservice.dto.ActionType;
import org.epam.trainerworkloadservice.dto.request.TrainerWorkloadRequest;
import org.epam.trainerworkloadservice.messaging.TrainerWorkloadConsumer;
import org.epam.trainerworkloadservice.model.MonthSummary;
import org.epam.trainerworkloadservice.model.TrainerWorkload;
import org.epam.trainerworkloadservice.model.YearSummary;
import org.epam.trainerworkloadservice.repository.TrainerWorkloadRepository;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TrainerWorkloadComponentSteps {

    private final TrainerWorkloadConsumer trainerWorkloadConsumer;
    private final TrainerWorkloadRepository trainerWorkloadRepository;

    private TrainerWorkloadRequest request;
    private Exception thrownException;

    public TrainerWorkloadComponentSteps(
            TrainerWorkloadConsumer trainerWorkloadConsumer,
            TrainerWorkloadRepository trainerWorkloadRepository
    ) {
        this.trainerWorkloadConsumer =
                trainerWorkloadConsumer;

        this.trainerWorkloadRepository =
                trainerWorkloadRepository;
    }

    @Before
    public void beforeScenario() {

        reset(trainerWorkloadRepository);

        request = null;
        thrownException = null;
    }

    @Given(
            "a valid ADD workload message for trainer {string} " +
                    "with duration {int} on {string}"
    )
    public void aValidAddWorkloadMessage(
            String trainerUsername,
            int duration,
            String trainingDate
    ) {

        request = createRequest(
                trainerUsername,
                duration,
                LocalDate.parse(trainingDate),
                ActionType.ADD
        );

        when(
                trainerWorkloadRepository
                        .findByTrainerUsername(
                                trainerUsername
                        )
        ).thenReturn(
                Optional.empty()
        );

        when(
                trainerWorkloadRepository.save(
                        any(TrainerWorkload.class)
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );
    }

    @Given(
            "an invalid workload message for trainer {string} " +
                    "with duration {int} on {string}"
    )
    public void anInvalidWorkloadMessage(
            String trainerUsername,
            int duration,
            String trainingDate
    ) {

        request = createRequest(
                trainerUsername,
                duration,
                LocalDate.parse(trainingDate),
                ActionType.ADD
        );
    }

    @When("the workload message is consumed")
    public void theWorkloadMessageIsConsumed() {

        try {

            trainerWorkloadConsumer
                    .receiveWorkloadUpdate(
                            request,
                            "component-test-transaction"
                    );

        } catch (Exception exception) {

            thrownException = exception;
        }
    }

    @Then("the workload message should be processed successfully")
    public void theWorkloadMessageShouldBeProcessedSuccessfully() {

        assertNull(
                thrownException,
                "Expected workload message to be processed successfully"
        );
    }

    @Then(
            "trainer {string} should have {int} minutes " +
                    "for year {int} and month {int}"
    )
    public void trainerShouldHaveMonthlyDuration(
            String trainerUsername,
            int expectedDuration,
            int expectedYear,
            int expectedMonth
    ) {

        ArgumentCaptor<TrainerWorkload> captor =
                ArgumentCaptor.forClass(
                        TrainerWorkload.class
                );

        verify(
                trainerWorkloadRepository
        ).save(
                captor.capture()
        );

        TrainerWorkload savedWorkload =
                captor.getValue();

        assertEquals(
                trainerUsername,
                savedWorkload.getTrainerUsername()
        );

        YearSummary yearSummary =
                savedWorkload
                        .getYears()
                        .stream()
                        .filter(
                                year ->
                                        year.getYear()
                                                .equals(expectedYear)
                        )
                        .findFirst()
                        .orElseThrow();

        MonthSummary monthSummary =
                yearSummary
                        .getMonths()
                        .stream()
                        .filter(
                                month ->
                                        month.getMonth()
                                                .equals(expectedMonth)
                        )
                        .findFirst()
                        .orElseThrow();

        assertEquals(
                expectedDuration,
                monthSummary.getTrainingSummaryDuration()
        );
    }

    @Then("the workload message should be rejected as invalid")
    public void workloadMessageShouldBeRejectedAsInvalid() {

        assertNotNull(
                thrownException,
                "Expected invalid message to be rejected"
        );

        assertInstanceOf(
                IllegalArgumentException.class,
                thrownException
        );

        assertTrue(
                thrownException
                        .getMessage()
                        .contains(
                                "Invalid trainer workload message"
                        )
        );
    }

    @Then("no trainer workload should be saved")
    public void noTrainerWorkloadShouldBeSaved() {

        verify(
                trainerWorkloadRepository,
                never()
        ).save(
                any(TrainerWorkload.class)
        );
    }

    private TrainerWorkloadRequest createRequest(
            String trainerUsername,
            int duration,
            LocalDate trainingDate,
            ActionType actionType
    ) {

        TrainerWorkloadRequest workloadRequest =
                new TrainerWorkloadRequest();

        workloadRequest.setTrainerUsername(
                trainerUsername
        );

        workloadRequest.setTrainerFirstName(
                "Cucumber"
        );

        workloadRequest.setTrainerLastName(
                "Trainer"
        );

        workloadRequest.setIsActive(
                true
        );

        workloadRequest.setTrainingDate(
                trainingDate
        );

        workloadRequest.setTrainingDuration(
                duration
        );

        workloadRequest.setActionType(
                actionType
        );

        return workloadRequest;
    }
}