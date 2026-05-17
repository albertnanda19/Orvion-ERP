package com.orvion.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends OrvionException {

    public ResourceNotFoundException(String message) {
        super("RESOURCE_NOT_FOUND", message, HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String resourceType, String identifier) {
        super(
            "RESOURCE_NOT_FOUND",
            resourceType + " not found with identifier: " + identifier,
            HttpStatus.NOT_FOUND
        );
    }

    public ResourceNotFoundException(String resourceType, String field, String value) {
        super(
            "RESOURCE_NOT_FOUND",
            resourceType + " not found with " + field + ": " + value,
            HttpStatus.NOT_FOUND
        );
    }
}
