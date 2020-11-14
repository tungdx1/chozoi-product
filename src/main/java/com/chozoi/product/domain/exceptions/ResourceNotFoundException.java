package com.chozoi.product.domain.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -3020415123850778435L;

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
