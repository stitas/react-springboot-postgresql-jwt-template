package com.arbusi.api.exceptions;

import com.arbusi.api.exceptions.base.ApiException;
import org.springframework.http.HttpStatus;

public class ConflictException extends ApiException {
  public ConflictException(String message) {
    super(message, HttpStatus.CONFLICT);
  }
}
