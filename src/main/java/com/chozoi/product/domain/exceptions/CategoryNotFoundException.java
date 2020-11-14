package com.chozoi.product.domain.exceptions;

public class CategoryNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 3699645497547142737L;

    public CategoryNotFoundException(String message) {
        super(message);
    }
}
