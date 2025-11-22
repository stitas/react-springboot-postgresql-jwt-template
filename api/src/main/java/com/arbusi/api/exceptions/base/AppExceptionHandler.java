package com.arbusi.api.exceptions.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class AppExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ExceptionDto> handleAppException(ApiException ex) {
        ExceptionDto body = new ExceptionDto(
                LocalDateTime.now(),
                ex.getStatus(),
                ex.getMessage()
        );

        return new ResponseEntity<>(body, ex.getStatus());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
        ExceptionDto body = new ExceptionDto(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN,
                ex.getMessage()
        );

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthException(AuthenticationException ex) {
        ExceptionDto body = new ExceptionDto(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED,
                ex.getMessage()
        );

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> handleAll(Exception ex) {
        log.error("Internal server error in exception handler:", ex);

        ExceptionDto body = new ExceptionDto(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage()
        );

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
