package org.epam.gym_crm_system1.client;

import jakarta.servlet.http.HttpServletRequest;
import org.epam.gym_crm_system1.dto.request.TrainerWorkloadRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Component
public class TrainerWorkloadClient {

    private static final Logger logger =
            LoggerFactory.getLogger(TrainerWorkloadClient.class);

    private static final String WORKLOAD_SERVICE_URL =
            "http://trainer-workload-service/api/workloads";

    private static final String TRANSACTION_ID =
            "transactionId";

    private static final String TRANSACTION_HEADER =
            "X-Transaction-Id";

    private final RestClient restClient;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    public TrainerWorkloadClient(
            @LoadBalanced RestClient.Builder restClientBuilder,
            CircuitBreakerFactory<?, ?> circuitBreakerFactory) {

        this.restClient = restClientBuilder.build();
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    public void updateWorkload(TrainerWorkloadRequest request) {

        String authorizationHeader =
                getAuthorizationHeader();

        String transactionId =
                MDC.get(TRANSACTION_ID);

        if (transactionId == null || transactionId.isBlank()) {
            transactionId = UUID.randomUUID().toString();
        }

        String downstreamTransactionId = transactionId;

        circuitBreakerFactory
                .create("trainerWorkloadService")
                .run(
                        () -> {
                            restClient.post()
                                    .uri(WORKLOAD_SERVICE_URL)
                                    .header(
                                            HttpHeaders.AUTHORIZATION,
                                            authorizationHeader
                                    )
                                    .header(
                                            TRANSACTION_HEADER,
                                            downstreamTransactionId
                                    )
                                    .body(request)
                                    .retrieve()
                                    .toBodilessEntity();

                            return null;
                        },
                        throwable -> {
                            logger.error(
                                    "Trainer workload service call failed. transactionId={}, error={}",
                                    downstreamTransactionId,
                                    throwable.getMessage()
                            );

                            throw new IllegalStateException(
                                    "Trainer workload service is unavailable",
                                    throwable
                            );
                        }
                );
    }

    private String getAuthorizationHeader() {

        ServletRequestAttributes attributes =
                (ServletRequestAttributes)
                        RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            throw new IllegalStateException(
                    "No HTTP request available"
            );
        }

        HttpServletRequest request =
                attributes.getRequest();

        String authorizationHeader =
                request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {

            throw new IllegalStateException(
                    "Bearer token is required"
            );
        }

        return authorizationHeader;
    }
}