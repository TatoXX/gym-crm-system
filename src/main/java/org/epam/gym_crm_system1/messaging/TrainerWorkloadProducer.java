package org.epam.gym_crm_system1.messaging;

import org.epam.gym_crm_system1.dto.request.TrainerWorkloadRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TrainerWorkloadProducer {

    private static final Logger logger =
            LoggerFactory.getLogger(TrainerWorkloadProducer.class);

    private static final String TRANSACTION_ID =
            "transactionId";

    private final JmsTemplate jmsTemplate;
    private final String trainerWorkloadQueue;

    public TrainerWorkloadProducer(
            JmsTemplate jmsTemplate,
            @Value("${app.messaging.trainer-workload-queue}")
            String trainerWorkloadQueue) {

        this.jmsTemplate = jmsTemplate;
        this.trainerWorkloadQueue = trainerWorkloadQueue;
    }

    public void sendWorkloadUpdate(
            TrainerWorkloadRequest request) {

        String transactionId =
                MDC.get(TRANSACTION_ID);

        if (transactionId == null ||
                transactionId.isBlank()) {

            transactionId =
                    UUID.randomUUID().toString();
        }

        String messageTransactionId =
                transactionId;

        logger.info(
                "Publishing trainer workload message. trainerUsername={}, actionType={}, transactionId={}",
                request.getTrainerUsername(),
                request.getActionType(),
                messageTransactionId
        );

        jmsTemplate.convertAndSend(
                trainerWorkloadQueue,
                request,
                message -> {

                    message.setStringProperty(
                            TRANSACTION_ID,
                            messageTransactionId
                    );

                    return message;
                }
        );

        logger.info(
                "Trainer workload message published successfully. trainerUsername={}, transactionId={}",
                request.getTrainerUsername(),
                messageTransactionId
        );
    }
}