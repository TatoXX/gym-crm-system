package org.epam.gym_crm_system1.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class TransactionLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(TransactionLoggingFilter.class);

    private static final String TRANSACTION_ID = "transactionId";
    private static final String TRANSACTION_HEADER = "X-Transaction-Id";
    private static final int REQUEST_CACHE_LIMIT = 1024 * 1024;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String transactionId = request.getHeader(TRANSACTION_HEADER);

        if (transactionId == null || transactionId.isBlank()) {
            transactionId = UUID.randomUUID().toString();
        }

        MDC.put(TRANSACTION_ID, transactionId);
        response.setHeader(TRANSACTION_HEADER, transactionId);

        ContentCachingRequestWrapper wrappedRequest =
                new ContentCachingRequestWrapper(request, REQUEST_CACHE_LIMIT);

        ContentCachingResponseWrapper wrappedResponse =
                new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        try {
            logger.info("Transaction started. method={}, uri={}, query={}",
                    wrappedRequest.getMethod(),
                    wrappedRequest.getRequestURI(),
                    maskSensitiveData(wrappedRequest.getQueryString())
            );

            filterChain.doFilter(wrappedRequest, wrappedResponse);

            long duration = System.currentTimeMillis() - startTime;

            logger.info("REST call finished. method={}, uri={}, query={}, requestBody={}, status={}, responseBody={}, durationMs={}",
                    wrappedRequest.getMethod(),
                    wrappedRequest.getRequestURI(),
                    maskSensitiveData(wrappedRequest.getQueryString()),
                    getRequestBody(wrappedRequest),
                    wrappedResponse.getStatus(),
                    getResponseBody(wrappedResponse),
                    duration
            );

        } catch (Exception exception) {
            long duration = System.currentTimeMillis() - startTime;

            logger.error("REST call failed. method={}, uri={}, query={}, requestBody={}, errorMessage={}, durationMs={}",
                    wrappedRequest.getMethod(),
                    wrappedRequest.getRequestURI(),
                    maskSensitiveData(wrappedRequest.getQueryString()),
                    getRequestBody(wrappedRequest),
                    exception.getMessage(),
                    duration
            );

            throw exception;

        } finally {
            wrappedResponse.copyBodyToResponse();
            MDC.clear();
        }
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();

        if (content.length == 0) {
            return "";
        }

        String body = new String(content, StandardCharsets.UTF_8);
        return maskSensitiveData(body);
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray();

        if (content.length == 0) {
            return "";
        }

        String body = new String(content, StandardCharsets.UTF_8);
        return maskSensitiveData(body);
    }

    private String maskSensitiveData(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }

        return text
                .replaceAll("(?i)(password=)[^&\\s]*", "$1***")
                .replaceAll("(?i)(oldPassword=)[^&\\s]*", "$1***")
                .replaceAll("(?i)(newPassword=)[^&\\s]*", "$1***")
                .replaceAll("(?i)\"password\"\\s*:\\s*\"[^\"]*\"", "\"password\":\"***\"")
                .replaceAll("(?i)\"oldPassword\"\\s*:\\s*\"[^\"]*\"", "\"oldPassword\":\"***\"")
                .replaceAll("(?i)\"newPassword\"\\s*:\\s*\"[^\"]*\"", "\"newPassword\":\"***\"");
    }
}