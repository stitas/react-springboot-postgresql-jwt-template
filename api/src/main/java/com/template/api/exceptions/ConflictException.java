package com.template.api.exceptions;

import com.template.api.exceptions.base.ApiException;
import org.springframework.http.HttpStatus;

public class ConflictException extends ApiException {
  public ConflictException(String message) {
    super(message, HttpStatus.CONFLICT);
  }
}
