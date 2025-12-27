package com.template.api.exceptions.base;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ExceptionDto(
        LocalDateTime timestamp,
        HttpStatus status,
        String error
) {}