package com.intern.order.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles validation errors (@Valid).
     * @param ex the exception
     * @return a map of field errors and a 400 Bad Request status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        log.error("Validation error: {}", errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles resource not found errors.
     * @param ex ProductNotFoundException or OrderNotFoundException
     * @return an error message and a 404 Not Found status
     */
    @ExceptionHandler({ProductNotFoundException.class, OrderNotFoundException.class})
    public ResponseEntity<Object> handleResourceNotFoundException(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        log.error("Resource not found: {}", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles stock-related errors.
     * @param ex InsufficientStockException
     * @return an error message and a 409 Conflict status
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Object> handleInsufficientStockException(InsufficientStockException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        log.error("Stock conflict: {}", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
    
    /**
     * Handles business logic validation errors.
     * @param ex InvalidOrderStatusException or IllegalStateException
     * @return an error message and a 400 Bad Request status
     */
    @ExceptionHandler({InvalidOrderStatusException.class, IllegalStateException.class})
    public ResponseEntity<Object> handleInvalidStatusException(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        log.error("Invalid request logic: {}", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles any other unhandled exceptions as a fallback.
     * @param ex the exception
     * @return a generic error message and a 500 Internal Server Error status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "An unexpected internal server error occurred.");
        log.error("Unhandled exception occurred", ex); // Log the full stack trace
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}