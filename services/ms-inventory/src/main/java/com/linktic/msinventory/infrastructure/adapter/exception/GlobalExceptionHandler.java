package com.linktic.msinventory.infrastructure.adapter.exception;

import com.linktic.msinventory.domain.exception.InsufficientStockException;
import com.linktic.msinventory.domain.exception.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("title", "Validation Error");
        String detail = ex.getBindingResult().getAllErrors().isEmpty() ? "Validation failed" : ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        error.put("detail", detail);
        error.put("status", "400");
        return new ResponseEntity<>(Collections.singletonMap("errors", Collections.singletonList(error)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProductNotFoundException(ProductNotFoundException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("title", "Not Found");
        error.put("detail", ex.getMessage());
        error.put("status", "404");
        return new ResponseEntity<>(Collections.singletonMap("errors", Collections.singletonList(error)), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientStockException(InsufficientStockException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("title", "Conflict");
        error.put("detail", ex.getMessage());
        error.put("status", "409");
        return new ResponseEntity<>(Collections.singletonMap("errors", Collections.singletonList(error)), HttpStatus.CONFLICT);
    }
}
