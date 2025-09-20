package com.demoJob.demo.exception;

import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handler cho ApiException
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex, WebRequest request) {
        ErrorResponse response = buildErrorResponse(
                ex, request,
                HttpStatus.valueOf(ex.getStatusCode()),
                ex.getErrorCode(),
                ex.getMessage()
        );
        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException exception) {
        log.info("(handleValidationExceptions)exception: {}", exception.getMessage());

        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        log.info("(handleValidationExceptions) {}", errors);

        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .message(String.join("; ", errors.values()))
                .build();
    }

    // Handler cho lỗi auth
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleBadCredentials(BadCredentialsException e, WebRequest request) {
        List<String> messages = List.of(e.getMessage());
        return buildErrorResponse(e, request, HttpStatus.UNAUTHORIZED, "BAD_CREDENTIALS", e.getMessage());
    }

    // Handler fallback cho tất cả lỗi khác
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAllUncaughtException(Exception e, WebRequest request) {
        log.error("Unhandled exception occurred", e);
        return buildErrorResponse(e, request, HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", e.getMessage());
    }

    //Enums
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException ife && ife.getTargetType() != null && ife.getTargetType().isEnum()) {
            String fieldName = ife.getPath().isEmpty() ? "unknown" : ife.getPath().get(0).getFieldName();
            String invalidValue = String.valueOf(ife.getValue());
            String acceptedValues = Arrays.toString(ife.getTargetType().getEnumConstants());

            String message = String.format(
                    "Invalid value '%s' for field '%s'. Accepted values are: %s",
                    invalidValue, fieldName, acceptedValues
            );

            return buildErrorResponse(ex, request, BAD_REQUEST, "ENUM_INVALID", message);
        }

        // fallback for other parse errors
        return buildErrorResponse(ex, request, BAD_REQUEST, "INVALID_JSON", "Invalid request payload");
    }



    private ErrorResponse buildErrorResponse(Exception e, WebRequest request,
                                             HttpStatus status,
                                             String error,
                                             String message) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .message(message)
                .build();
    }
}
