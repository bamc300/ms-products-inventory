package com.linktic.msinventory.infrastructure.adapter.exception;

import com.linktic.msinventory.domain.exception.ExternalServiceException;
import com.linktic.msinventory.domain.exception.InsufficientStockException;
import com.linktic.msinventory.domain.exception.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final String TITLE = "title";
        private static final String DETAIL = "detail";
        private static final String STATUS = "status";
        private static final String ERRORS = "errors";

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, Object>> handleValidationExceptions(
                        MethodArgumentNotValidException ex) {
                Map<String, Object> error = new HashMap<>();
                error.put(TITLE, "Validation Error");
                String detail = ex.getBindingResult().getAllErrors().isEmpty() ? "Validation failed"
                                : ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
                error.put(DETAIL, detail);
                error.put(STATUS, "400");
                return new ResponseEntity<>(
                                Collections.singletonMap(ERRORS, Collections.singletonList(error)),
                                HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(ProductNotFoundException.class)
        public ResponseEntity<Map<String, Object>> handleProductNotFoundException(
                        ProductNotFoundException ex) {
                Map<String, Object> error = new HashMap<>();
                error.put(TITLE, "Not Found");
                error.put(DETAIL, ex.getMessage());
                error.put(STATUS, "404");
                return new ResponseEntity<>(
                                Collections.singletonMap(ERRORS, Collections.singletonList(error)),
                                HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(InsufficientStockException.class)
        public ResponseEntity<Map<String, Object>> handleInsufficientStockException(
                        InsufficientStockException ex) {
                Map<String, Object> error = new HashMap<>();
                error.put(TITLE, "Conflict");
                error.put(DETAIL, ex.getMessage());
                error.put(STATUS, "409");
                return new ResponseEntity<>(
                                Collections.singletonMap(ERRORS, Collections.singletonList(error)),
                                HttpStatus.CONFLICT);
        }

        @ExceptionHandler({ResourceAccessException.class, HttpServerErrorException.class,
                        ExternalServiceException.class, RuntimeException.class})
        public ResponseEntity<Map<String, Object>> handleServiceUnavailableException(Exception ex) {
                // If the message contains "Timeout calling Products Service", it's likely a 503
                // scenario
                if (ex.getMessage() != null && (ex.getMessage().contains("Timeout")
                                || ex.getMessage().contains("Error calling Products Service"))) {
                        Map<String, Object> error = new HashMap<>();
                        error.put(TITLE, "Service Unavailable");
                        error.put(DETAIL, "Dependency service is unavailable or timed out: "
                                        + ex.getMessage());
                        error.put(STATUS, "503");
                        return new ResponseEntity<>(
                                        Collections.singletonMap(ERRORS,
                                                        Collections.singletonList(error)),
                                        HttpStatus.SERVICE_UNAVAILABLE);
                }
                // Fallback for other RuntimeExceptions (Internal Server Error)
                Map<String, Object> error = new HashMap<>();
                error.put(TITLE, "Internal Server Error");
                error.put(DETAIL, ex.getMessage());
                error.put(STATUS, "500");
                return new ResponseEntity<>(
                                Collections.singletonMap(ERRORS, Collections.singletonList(error)),
                                HttpStatus.INTERNAL_SERVER_ERROR);
        }
}
