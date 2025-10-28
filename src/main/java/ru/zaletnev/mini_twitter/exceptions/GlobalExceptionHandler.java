package ru.zaletnev.mini_twitter.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.zaletnev.mini_twitter.model.dto.ErrorResponse;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private ErrorResponse body(String code, String message, HttpStatus status, HttpServletRequest request) {
        return new ErrorResponse(code, message, status.value(), request.getRequestURI(), Instant.now());
    }

    //DTO VALIDATION (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {

        String joined = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body("Validation Error", joined, HttpStatus.BAD_REQUEST, request));
    }

    //AccessDenied (403)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(body("Access Denied", "Not enough rights", HttpStatus.FORBIDDEN, request));
    }

    //NOT FOUND (404) (...orElseThrow() etc.)
    @ExceptionHandler({NoSuchElementException.class, UsernameNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex, HttpServletRequest req) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(body("NOT_FOUND", "Resource not found", HttpStatus.NOT_FOUND, req));
    }

    // NOT AUTH (401)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuth(AuthenticationException ex, HttpServletRequest req) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(body("UNAUTHORIZED", "Authentication is needed", HttpStatus.UNAUTHORIZED, req));
    }

    // DOMEN`S CONFLICTS (409)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleConflict(IllegalStateException ex, HttpServletRequest req) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(body("CONFLICT", ex.getMessage(), HttpStatus.CONFLICT, req));
    }

    // UNEXPECTED EX (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest req) {
        String msg = "Internal Error. ID: " + UUID.randomUUID();
        log.error("Unexpected error at {}: {}", req.getRequestURI(), ex.toString(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body("INTERNAL_ERROR", msg, HttpStatus.INTERNAL_SERVER_ERROR, req));
    }
}
