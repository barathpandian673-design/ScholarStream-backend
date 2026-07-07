package com.example.demo.exception;

/**
 * Thrown when a paper cannot be located by its id (or another
 * resource lookup comes up empty). Handled by GlobalExceptionHandler
 * and translated into an HTTP 404 response.
 */
public class PaperNotFoundException extends RuntimeException {

    public PaperNotFoundException(String message) {
        super(message);
    }
}
