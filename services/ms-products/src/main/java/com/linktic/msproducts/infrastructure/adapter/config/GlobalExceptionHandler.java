package com.linktic.msproducts.infrastructure.adapter.config;

import com.linktic.msproducts.application.dto.ErrorResponseDto;
import com.linktic.msproducts.domain.exception.ProductNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleProductNotFound(ProductNotFoundException ex) {
    return buildErrorResponse(HttpStatus.NOT_FOUND, "Product not found", ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDto> handleValidationErrors(
      MethodArgumentNotValidException ex) {
    List<ErrorResponseDto.ErrorDetail> errors = ex.getBindingResult().getFieldErrors().stream()
        .map(err -> ErrorResponseDto.ErrorDetail.builder()
            .status(String.valueOf(HttpStatus.BAD_REQUEST.value())).title("Validation Error")
            .detail(err.getField() + ": " + err.getDefaultMessage()).build())
        .collect(Collectors.toList());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponseDto.builder().errors(errors).build());
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponseDto> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex) {
    return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
        ex.getMessage());
  }

  private ResponseEntity<ErrorResponseDto> buildErrorResponse(HttpStatus status, String title,
      String detail) {
    ErrorResponseDto response =
        ErrorResponseDto.builder()
            .errors(Collections.singletonList(ErrorResponseDto.ErrorDetail.builder()
                .status(String.valueOf(status.value())).title(title).detail(detail).build()))
            .build();
    return ResponseEntity.status(status).body(response);
  }
}
