package org.safa.maintenanceserviceapigateaway.exception;

import org.safa.maintenanceserviceapigateaway.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.concurrent.TimeoutException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ApiResponse<Void>> handleTimeout(TimeoutException ex) {
        return build(
                HttpStatus.GATEWAY_TIMEOUT,
                "Gateway timeout"
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(
            IllegalArgumentException ex
    ) {
        return build(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<Void>> handleNullPointer(
            NullPointerException ex
    ) {
        return build(
                HttpStatus.BAD_REQUEST,
                "Invalid request"
        );
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiResponse<Void>> handleSecurityException(
            SecurityException ex
    ) {
        return build(
                HttpStatus.FORBIDDEN,
                "Access denied"
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnknownException(
            Exception ex
    ) {
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected error"
        );
    }

    private ResponseEntity<ApiResponse<Void>> build(
            HttpStatus status,
            String message
    ) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(status.value())
                .data(null)
                .message(message)
                .build();

        return ResponseEntity
                .status(status)
                .body(response);
    }
}