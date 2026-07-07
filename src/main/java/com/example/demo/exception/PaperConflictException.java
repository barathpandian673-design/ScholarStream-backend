package com.example.demo.exception;

/**
 * Thrown when a requested operation conflicts with the current state
 * of a paper (e.g. a duplicate submission). Handled by
 * GlobalExceptionHandler and translated into an HTTP 409 response.
 */
public class PaperConflictException extends RuntimeException {

    public PaperConflictException(String message) {
        super(message);
    }
}
