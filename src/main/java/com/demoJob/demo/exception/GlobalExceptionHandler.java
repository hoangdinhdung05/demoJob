package com.demoJob.demo.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handler cho ApiException
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex, WebRequest request) {
        List<String> messages = List.of(ex.getMessage());
        ErrorResponse response = buildErrorResponse(
                ex, request,
                HttpStatus.valueOf(ex.getStatusCode()),
                ex.getErrorCode(),
                messages
        );
        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }

    // Handler cho validation errors
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            MissingServletRequestParameterException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(Exception e, WebRequest request) {
        List<String> messages;
        String error;

        if (e instanceof MethodArgumentNotValidException manve) {
            messages = manve.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            error = "INVALID_PAYLOAD";
        } else if (e instanceof MissingServletRequestParameterException msrp) {
            messages = List.of(msrp.getParameterName() + " parameter is missing");
            error = "MISSING_PARAMETER";
        } else if (e instanceof ConstraintViolationException cve) {
            messages = List.of(cve.getMessage());
            error = "INVALID_PARAMETER";
        } else {
            messages = List.of(e.getMessage());
            error = "VALIDATION_ERROR";
        }

        return buildErrorResponse(e, request, HttpStatus.BAD_REQUEST, error, messages);
    }

    // Handler cho lỗi auth
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleBadCredentials(BadCredentialsException e, WebRequest request) {
        List<String> messages = List.of(e.getMessage());
        return buildErrorResponse(e, request, HttpStatus.UNAUTHORIZED, "BAD_CREDENTIALS", messages);
    }

    // Handler fallback cho tất cả lỗi khác
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAllUncaughtException(Exception e, WebRequest request) {
        log.error("Unhandled exception occurred", e);
        List<String> messages = List.of(e.getMessage());
        return buildErrorResponse(e, request, HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", messages);
    }

    // Build ErrorResponse
    private ErrorResponse buildErrorResponse(Exception e, WebRequest request,
                                             HttpStatus status,
                                             String error,
                                             List<String> messages) {
        ErrorResponse response = new ErrorResponse();
        response.setTimestamp(new Date());
        response.setStatus(status.value());
        response.setMessage(messages);
        return response;
    }
}
