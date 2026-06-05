package com.example.lexicron.exception;

/**
 * Excepción personalizada para errores del juego Lexicron.
 * Se usa principalmente para errores de carga de datos o de generación
 * de rondas cuando no hay complementos compatibles.
 */
public class LexicronException extends RuntimeException {
    public LexicronException(String message) {
        super(message);
    }

    public LexicronException(String message, Throwable cause) {
        super(message, cause);
    }
}
