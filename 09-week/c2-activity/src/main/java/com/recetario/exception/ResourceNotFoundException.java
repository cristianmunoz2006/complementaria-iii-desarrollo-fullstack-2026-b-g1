package com.recetario.exception;

/**
 * Se lanza cuando se busca/actualiza/borra una receta cuyo id no existe.
 * El GlobalExceptionHandler la traduce a un 404.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
