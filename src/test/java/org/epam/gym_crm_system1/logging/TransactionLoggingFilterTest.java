package org.epam.gym_crm_system1.logging;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TransactionLoggingFilterTest {

    private final TransactionLoggingFilter transactionLoggingFilter = new TransactionLoggingFilter();

    @Test
    void doFilter_WhenTransactionIdHeaderMissing_ShouldGenerateTransactionId()
            throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/trainees");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        transactionLoggingFilter.doFilter(request, response, filterChain);

        String transactionId = response.getHeader("X-Transaction-Id");

        assertNotNull(transactionId);
        assertFalse(transactionId.isBlank());
    }

    @Test
    void doFilter_WhenTransactionIdHeaderExists_ShouldUseExistingTransactionId()
            throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/trainees");
        request.addHeader("X-Transaction-Id", "test-transaction-id");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        transactionLoggingFilter.doFilter(request, response, filterChain);

        assertEquals("test-transaction-id", response.getHeader("X-Transaction-Id"));
    }

    @Test
    void doFilter_ShouldContinueFilterChainAndReturnOkStatus()
            throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        transactionLoggingFilter.doFilter(request, response, filterChain);

        assertEquals(200, response.getStatus());
    }
}