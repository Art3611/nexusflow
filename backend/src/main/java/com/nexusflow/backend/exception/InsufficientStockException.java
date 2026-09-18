package com.nexusflow.backend.exception;

/**
 * Se lanza cuando se intenta pedir mas unidades de un producto de las
 * que hay en stock. El GlobalExceptionHandler la traduce a HTTP 409
 * (Conflict): la peticion es valida en su forma, pero entra en conflicto
 * con el estado actual del sistema.
 */
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }

}
