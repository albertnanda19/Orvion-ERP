package com.orvion.common.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends OrvionException {

    public ValidationException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.BAD_REQUEST);
    }

    public ValidationException(String message) {
        super("VALIDATION_ERROR", message, HttpStatus.BAD_REQUEST);
    }
}
