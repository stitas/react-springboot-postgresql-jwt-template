package com.arbusi.api.exceptions;

import com.arbusi.api.exceptions.base.ApiException;
import org.springframework.http.HttpStatus;

public class NotAllowedException extends ApiException {
    public NotAllowedException(String message) {
        super(message, HttpStatus.METHOD_NOT_ALLOWED);
    }
}
