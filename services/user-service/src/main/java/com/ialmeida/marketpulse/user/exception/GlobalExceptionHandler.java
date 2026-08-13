package com.ialmeida.marketpulse.user.exception;

import com.ialmeida.marketpulse.user.dto.error.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
        MethodArgumentNotValidException exception,
        HttpServletRequest request
    ) {
        List<ApiErrorResponse.FieldError> errors = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> new ApiErrorResponse.FieldError(
                error.getField(),
                error.getDefaultMessage()
            ))
            .toList();

        ApiErrorResponse response = new ApiErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "The request contains invalid data.",
            errors,
            request.getRequestURI(),
            Instant.now()
        );

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFound(
        UserNotFoundException exception,
        HttpServletRequest request
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            exception.getMessage(),
            List.of(),
            request.getRequestURI(),
            Instant.now()
        );

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(response);
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateUser(
        DuplicateUserException exception,
        HttpServletRequest request
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
            HttpStatus.CONFLICT.value(),
            exception.getMessage(),
            List.of(),
            request.getRequestURI(),
            Instant.now()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(
        Exception exception,
        HttpServletRequest request
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected error occurred.",
            List.of(),
            request.getRequestURI(),
            Instant.now()
        );

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response);
    }

}
