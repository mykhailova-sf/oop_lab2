package com.laba.products.security.exception;

public class ValidationException extends RuntimeException
{
    public ValidationException(String message) {
        super(message);
    }
}
