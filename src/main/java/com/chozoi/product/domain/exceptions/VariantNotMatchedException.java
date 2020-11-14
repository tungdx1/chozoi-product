package com.chozoi.product.domain.exceptions;

public class VariantNotMatchedException extends RuntimeException {

    private static final long serialVersionUID = -9174778551090045244L;

    public VariantNotMatchedException(String message) {
        super(message);
    }
}
