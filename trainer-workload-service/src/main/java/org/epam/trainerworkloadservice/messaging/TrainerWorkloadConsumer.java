package org.epam.trainerworkloadservice.messaging;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.epam.trainerworkloadservice.dto.request.TrainerWorkloadRequest;
import org.epam.trainerworkloadservice.service.TrainerWorkloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class TrainerWorkloadConsumer {

    private static final Logger logger =
            LoggerFactory.getLogger(TrainerWorkloadConsumer.class);

    private static final String TRANSACTION_ID =
            "transactionId";

    private final TrainerWorkloadService trainerWorkloadService;
    private final Validator validator;

    public TrainerWorkloadConsumer(
            TrainerWorkloadService trainerWorkloadService,
            Validator validator) {

        this.trainerWorkloadService = trainerWorkloadService;
        this.validator = validator;
    }

    @JmsListener(
            destination = "${app.messaging.trainer-workload-queue}"
    )
    public void receiveWorkloadUpdate(
            TrainerWorkloadRequest request,
            @Header(
                    name = TRANSACTION_ID,
                    required = false
            )
            String transactionId) {

        String effectiveTransactionId =
                transactionId;

        if (effectiveTransactionId == null ||
                effectiveTransactionId.isBlank()) {

            effectiveTransactionId =
                    UUID.randomUUID().toString();
        }

        MDC.put(
                TRANSACTION_ID,
                effectiveTransactionId
        );

        try {

            logger.info(
                    "Received trainer workload message. trainerUsername={}, actionType={}",
                    request.getTrainerUsername(),
                    request.getActionType()
            );

            validateMessage(request);

            trainerWorkloadService.updateWorkload(request);

            logger.info(
                    "Trainer workload message processed successfully. trainerUsername={}",
                    request.getTrainerUsername()
            );

        } finally {

            MDC.remove(TRANSACTION_ID);
        }
    }

    private void validateMessage(
            TrainerWorkloadRequest request) {

        Set<ConstraintViolation<TrainerWorkloadRequest>>
                violations =
                validator.validate(request);

        if (!violations.isEmpty()) {

            String validationErrors =
                    violations.stream()
                            .map(violation ->
                                    violation.getPropertyPath()
                                            + ": "
                                            + violation.getMessage())
                            .collect(Collectors.joining(", "));

            logger.error(
                    "Invalid trainer workload message: {}",
                    validationErrors
            );

            throw new IllegalArgumentException(
                    "Invalid trainer workload message: "
                            + validationErrors
            );
        }
    }
}