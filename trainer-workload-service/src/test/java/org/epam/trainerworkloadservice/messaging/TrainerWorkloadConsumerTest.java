package org.epam.trainerworkloadservice.messaging;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.epam.trainerworkloadservice.dto.ActionType;
import org.epam.trainerworkloadservice.dto.request.TrainerWorkloadRequest;
import org.epam.trainerworkloadservice.service.TrainerWorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadConsumerTest {

    @Mock
    private TrainerWorkloadService trainerWorkloadService;

    private TrainerWorkloadConsumer trainerWorkloadConsumer;

    @BeforeEach
    void setUp() {
        Validator validator =
                Validation.buildDefaultValidatorFactory()
                        .getValidator();

        trainerWorkloadConsumer =
                new TrainerWorkloadConsumer(
                        trainerWorkloadService,
                        validator
                );
    }

    @Test
    void receiveWorkloadUpdate_WhenMessageValid_ShouldUpdateWorkload() {

        TrainerWorkloadRequest request =
                createValidRequest();

        trainerWorkloadConsumer.receiveWorkloadUpdate(
                request,
                "test-transaction-id"
        );

        verify(trainerWorkloadService)
                .updateWorkload(request);
    }

    @Test
    void receiveWorkloadUpdate_WhenMessageInvalid_ShouldRejectMessage() {

        TrainerWorkloadRequest request =
                createValidRequest();

        request.setTrainerUsername(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> trainerWorkloadConsumer.receiveWorkloadUpdate(
                        request,
                        "test-transaction-id"
                )
        );

        verifyNoInteractions(trainerWorkloadService);
    }

    private TrainerWorkloadRequest createValidRequest() {

        TrainerWorkloadRequest request =
                new TrainerWorkloadRequest();

        request.setTrainerUsername("Test.Trainer");
        request.setTrainerFirstName("Test");
        request.setTrainerLastName("Trainer");
        request.setIsActive(true);
        request.setTrainingDate(
                LocalDate.of(2026, 8, 31)
        );
        request.setTrainingDuration(60);
        request.setActionType(ActionType.ADD);

        return request;
    }
}