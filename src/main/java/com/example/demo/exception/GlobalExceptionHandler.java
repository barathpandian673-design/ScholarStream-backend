package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Central place where domain exceptions are translated into
 * consistent JSON error responses of the shape { "message": "..." }.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaperNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePaperNotFound(PaperNotFoundException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PaperConflictException.class)
    public ResponseEntity<Map<String, String>> handlePaperConflict(PaperConflictException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }
}
