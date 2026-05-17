package com.orvion.common.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends OrvionException {

    public BusinessException(String errorCode, String message) {
        super(errorCode, message, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    public BusinessException(String message) {
        super("BUSINESS_ERROR", message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
