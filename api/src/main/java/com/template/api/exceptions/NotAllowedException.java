package com.template.api.exceptions;

import com.template.api.exceptions.base.ApiException;
import org.springframework.http.HttpStatus;

public class NotAllowedException extends ApiException {
    public NotAllowedException(String message) {
        super(message, HttpStatus.METHOD_NOT_ALLOWED);
    }
}
