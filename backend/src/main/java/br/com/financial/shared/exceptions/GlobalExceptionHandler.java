package br.com.financial.shared.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(InvalidRequestException.class)
  public ResponseEntity<ApiErrorResponse> handleInvalidRequest(
      InvalidRequestException ex, HttpServletRequest request) {

    ApiErrorResponse response =
        new ApiErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI());
    return ResponseEntity.badRequest().body(response);
  }

  // Handles 404 Not Found (e.g., Account ID does not exist)
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
      ResourceNotFoundException ex, HttpServletRequest request) {

    ApiErrorResponse response =
        new ApiErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  // Handles 422 Unprocessable Entity (e.g., Transfer amount exceeds balance)
  @ExceptionHandler(BusinessRuleException.class)
  public ResponseEntity<ApiErrorResponse> handleBusinessRule(
      BusinessRuleException ex, HttpServletRequest request) {

    ApiErrorResponse response =
        new ApiErrorResponse(
            HttpStatus.UNPROCESSABLE_CONTENT.value(),
            HttpStatus.UNPROCESSABLE_CONTENT.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI());
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(response);
  }

  // Handles 400 Bad Request for Bean Validation (@Valid failures)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex, HttpServletRequest request) {

    ApiErrorResponse response =
        new ApiErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "Validation failed",
            request.getRequestURI());

    Map<String, String> errors = new HashMap<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      errors.put(error.getField(), error.getDefaultMessage());
    }
    response.setValidationErrors(errors);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  // Handles 500 Internal Server Error (Fallback for unhandled exceptions)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleAllUncaughtException(
      Exception ex, HttpServletRequest request) {

    logger.error("Unhandled exception while handling request {}", request.getRequestURI(), ex);

    ApiErrorResponse response =
        new ApiErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            "An unexpected internal server error occurred",
            request.getRequestURI());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }
}
