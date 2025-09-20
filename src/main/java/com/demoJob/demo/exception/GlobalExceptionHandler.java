package com.demoJob.demo.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;
import static org.springframework.http.HttpStatus.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //Handler chung cho tất cả ApiException
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

    //Validation error
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
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)   // chỉ lấy message
                    .collect(Collectors.joining("; "));
            error = "INVALID_PAYLOAD";
        } else if (e instanceof MissingServletRequestParameterException msrp) {
            message = msrp.getParameterName() + " parameter is missing";
            error = "MISSING_PARAMETER";
        } else if (e instanceof ConstraintViolationException cve) {
            message = cve.getMessage();
            error = "INVALID_PARAMETER";
        } else {
            message = e.getMessage();
            error = "VALIDATION_ERROR";
        }

        return buildErrorResponse(e, request, BAD_REQUEST, error, message);
    }

    //Auth error
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(UNAUTHORIZED)
    public ErrorResponse handleBadCredentials(BadCredentialsException e, WebRequest request) {
        return buildErrorResponse(e, request, UNAUTHORIZED, "BAD_CREDENTIALS", e.getMessage());
    }

    //Fallback
    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAllUncaughtException(Exception e, WebRequest request) {
        log.error("Unhandled exception occurred", e);
        return buildErrorResponse(e, request, INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", e.getMessage());
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
        ErrorResponse response = new ErrorResponse();
        response.setTimestamp(new Date());
        response.setStatus(status.value());
        response.setMessage(message);
        return response;
    }
}
