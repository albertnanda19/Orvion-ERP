package com.orvion.common.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends OrvionException {

    public UnauthorizedException(String message) {
        super("UNAUTHORIZED", message, HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedException() {
        super("UNAUTHORIZED", "Authentication is required", HttpStatus.UNAUTHORIZED);
    }
}
