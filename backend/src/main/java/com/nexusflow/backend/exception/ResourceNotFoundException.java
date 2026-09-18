package com.nexusflow.backend.exception;

/**
 * Se lanza cuando se referencia un recurso (Customer, Product, Order...)
 * que no existe. El GlobalExceptionHandler la traduce a HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

}
