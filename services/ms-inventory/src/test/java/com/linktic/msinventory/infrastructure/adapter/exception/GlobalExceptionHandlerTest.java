package com.linktic.msinventory.infrastructure.adapter.exception;

import com.linktic.msinventory.domain.exception.ExternalServiceException;
import com.linktic.msinventory.domain.exception.InsufficientStockException;
import com.linktic.msinventory.domain.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleValidationExceptions_ShouldReturnBadRequest() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "defaultMessage");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<Map<String, Object>> response =
                globalExceptionHandler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        List<Map<String, Object>> errors =
                (List<Map<String, Object>>) response.getBody().get("errors");
        assertEquals("Validation Error", errors.get(0).get("title"));
        assertEquals("defaultMessage", errors.get(0).get("detail"));
    }

    @Test
    void handleProductNotFoundException_ShouldReturnNotFound() {
        java.util.UUID productId = java.util.UUID.randomUUID();
        ProductNotFoundException ex = new ProductNotFoundException(productId);

        ResponseEntity<Map<String, Object>> response =
                globalExceptionHandler.handleProductNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        List<Map<String, Object>> errors =
                (List<Map<String, Object>>) response.getBody().get("errors");
        assertEquals("Not Found", errors.get(0).get("title"));
        assertEquals("Product not found with ID: " + productId, errors.get(0).get("detail"));
    }

    @Test
    void handleInsufficientStockException_ShouldReturnConflict() {
        InsufficientStockException ex = new InsufficientStockException("Insufficient stock");

        ResponseEntity<Map<String, Object>> response =
                globalExceptionHandler.handleInsufficientStockException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        List<Map<String, Object>> errors =
                (List<Map<String, Object>>) response.getBody().get("errors");
        assertEquals("Conflict", errors.get(0).get("title"));
        assertEquals("Insufficient stock", errors.get(0).get("detail"));
    }

    @Test
    void handleServiceUnavailableException_Timeout_ShouldReturnServiceUnavailable() {
        ExternalServiceException ex =
                new ExternalServiceException("Timeout calling Products Service");

        ResponseEntity<Map<String, Object>> response =
                globalExceptionHandler.handleServiceUnavailableException(ex);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        List<Map<String, Object>> errors =
                (List<Map<String, Object>>) response.getBody().get("errors");
        assertEquals("Service Unavailable", errors.get(0).get("title"));
    }

    @Test
    void handleServiceUnavailableException_Generic_ShouldReturnInternalServerError() {
        RuntimeException ex = new RuntimeException("Unexpected error");

        ResponseEntity<Map<String, Object>> response =
                globalExceptionHandler.handleServiceUnavailableException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        List<Map<String, Object>> errors =
                (List<Map<String, Object>>) response.getBody().get("errors");
        assertEquals("Internal Server Error", errors.get(0).get("title"));
        assertEquals("Unexpected error", errors.get(0).get("detail"));
    }
}
