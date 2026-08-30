package org.epam.trainerworkloadservice.logging;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

class TransactionLoggingFilterTest {

    private final TransactionLoggingFilter filter =
            new TransactionLoggingFilter();

    @Test
    void shouldReuseIncomingTransactionId()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setMethod("GET");

        request.setRequestURI(
                "/api/workloads/Test.Trainer"
                        + "/years/2026/months/8"
        );

        request.addHeader(
                "X-Transaction-Id",
                "test-transaction-123"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain =
                (servletRequest, servletResponse) -> {

                    servletResponse
                            .getWriter()
                            .write("40");
                };

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertEquals(
                "test-transaction-123",
                response.getHeader(
                        "X-Transaction-Id"
                )
        );

        assertEquals(
                "40",
                response.getContentAsString()
        );

        assertNull(
                MDC.get("transactionId")
        );
    }

    @Test
    void shouldGenerateTransactionIdWhenHeaderMissing()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setMethod("GET");

        request.setRequestURI(
                "/api/workloads/Test.Trainer"
                        + "/years/2026/months/8"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain =
                (servletRequest, servletResponse) -> {

                    servletResponse
                            .getWriter()
                            .write("40");
                };

        filter.doFilter(
                request,
                response,
                filterChain
        );

        String transactionId =
                response.getHeader(
                        "X-Transaction-Id"
                );

        assertNotNull(transactionId);
        assertFalse(transactionId.isBlank());

        assertDoesNotThrow(
                () ->
                        java.util.UUID.fromString(
                                transactionId
                        )
        );

        assertEquals(
                "40",
                response.getContentAsString()
        );

        assertNull(
                MDC.get("transactionId")
        );
    }
}