package com.demoJob.demo.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle Validation Exceptions
     */
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            MissingServletRequestParameterException.class
    })
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(Exception e, WebRequest request) {
        String message;
        String error;

        if (e instanceof MethodArgumentNotValidException manve) {
            message = manve.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(err -> err.getField() + ": " + err.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            error = "Invalid Payload";
        } else if (e instanceof MissingServletRequestParameterException msrp) {
            message = msrp.getParameterName() + " parameter is missing";
            error = "Missing Parameter";
        } else if (e instanceof ConstraintViolationException cve) {
            message = cve.getMessage();
            error = "Invalid Parameter";
        } else {
            message = e.getMessage();
            error = "Validation Error";
        }

        return buildErrorResponse(e, request, BAD_REQUEST, error, message);
    }

    /**
     * Handle Not Found Exception
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException e, WebRequest request) {
        return buildErrorResponse(e, request, NOT_FOUND, NOT_FOUND.getReasonPhrase(), e.getMessage());
    }

    /**
     * Handle Conflict Exception (e.g. duplicate)
     */
    @ExceptionHandler(InvalidDataException.class)
    @ResponseStatus(CONFLICT)
    public ErrorResponse handleInvalidDataException(InvalidDataException e, WebRequest request) {
        return buildErrorResponse(e, request, CONFLICT, CONFLICT.getReasonPhrase(), e.getMessage());
    }

    /**
     * Handle Unauthorized Exception (e.g. blacklisted token)
     */
    @ExceptionHandler(TokenBlacklistedException.class)
    @ResponseStatus(UNAUTHORIZED)
    public ErrorResponse handleTokenBlacklisted(TokenBlacklistedException e, WebRequest request) {
        return buildErrorResponse(e, request, UNAUTHORIZED, UNAUTHORIZED.getReasonPhrase(), e.getMessage());
    }

    /**
     * Fallback Handler - Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAllUncaughtException(Exception e, WebRequest request) {
        log.error("Unhandled exception occurred", e);
        return buildErrorResponse(e, request, INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR.getReasonPhrase(), e.getMessage());
    }

    /**
     * Utility method to build standard ErrorResponse
     */
    private ErrorResponse buildErrorResponse(Exception e, WebRequest request, HttpStatus status, String error, String message) {
        ErrorResponse response = new ErrorResponse();
        response.setTimestamp(new Date());
        response.setStatus(status.value());
        response.setError(error);
        response.setMessage(message);
        response.setPath(request.getDescription(false).replace("uri=", "").replace("url=", ""));
        return response;
    }
}
