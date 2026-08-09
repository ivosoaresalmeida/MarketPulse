package com.ialmeida.marketpulse.portfolio.exception;

import com.ialmeida.marketpulse.portfolio.dto.error.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiErrorResponse handleValidationException(
        MethodArgumentNotValidException exception,
        HttpServletRequest request) {

        List<ApiErrorResponse.FieldError> errors = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> new ApiErrorResponse.FieldError(
                error.getField(),
                error.getDefaultMessage()
            ))
            .toList();

        return new ApiErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "The request contains invalid data.",
            errors,
            request.getRequestURI(),
            Instant.now()
        );
    }

    @ExceptionHandler(PortfolioNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handlePortfolioNotFound(
        PortfolioNotFoundException exception,
        HttpServletRequest request) {

        return new ApiErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            exception.getMessage(),
            List.of(),
            request.getRequestURI(),
            Instant.now()
        );
    }
}