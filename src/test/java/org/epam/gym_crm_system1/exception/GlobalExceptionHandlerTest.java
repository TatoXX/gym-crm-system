package org.epam.gym_crm_system1.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.epam.gym_crm_system1.dto.response.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleInvalidCredentialsException_ShouldReturnUnauthorized() {
        HttpServletRequest request = createRequest("/api/login");

        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleInvalidCredentialsException(
                        new InvalidCredentialsException("Invalid username or password"),
                        request
                );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().getStatus());
        assertEquals("Unauthorized", response.getBody().getError());
        assertEquals("Invalid username or password", response.getBody().getMessage());
        assertEquals("/api/login", response.getBody().getPath());
    }

    @Test
    void handleCustomValidationException_ShouldReturnBadRequest() {
        HttpServletRequest request = createRequest("/api/trainees");

        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleCustomValidationException(
                        new ValidationException("First name is required"),
                        request
                );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("First name is required", response.getBody().getMessage());
        assertEquals("/api/trainees", response.getBody().getPath());
    }

    @Test
    void handleEntityNotFoundException_ShouldReturnNotFound() {
        HttpServletRequest request = createRequest("/api/trainers");

        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleEntityNotFoundException(
                        new EntityNotFoundException("Trainer not found"),
                        request
                );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Not Found", response.getBody().getError());
        assertEquals("Trainer not found", response.getBody().getMessage());
        assertEquals("/api/trainers", response.getBody().getPath());
    }

    @Test
    void handleRuntimeException_ShouldReturnBadRequest() {
        HttpServletRequest request = createRequest("/api/test");

        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleRuntimeException(
                        new RuntimeException("Runtime error"),
                        request
                );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Runtime error", response.getBody().getMessage());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void handleValidationException_ShouldReturnBadRequestWithFieldErrors() throws Exception {
        HttpServletRequest request = createRequest("/api/trainees");

        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "traineeRequest");

        bindingResult.addError(
                new FieldError("traineeRequest", "firstName", "First name is required")
        );

        MethodParameter methodParameter = new MethodParameter(
                GlobalExceptionHandlerTest.class.getDeclaredMethod("dummyMethod", String.class),
                0
        );

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<ErrorResponse> response =
                globalExceptionHandler.handleValidationException(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("firstName"));
        assertTrue(response.getBody().getMessage().contains("First name is required"));
        assertEquals("/api/trainees", response.getBody().getPath());
    }

    private HttpServletRequest createRequest(String uri) {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(uri);
        return request;
    }

    @SuppressWarnings("unused")
    private void dummyMethod(String value) {
    }
}