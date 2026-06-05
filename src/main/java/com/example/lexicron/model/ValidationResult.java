package com.example.lexicron.model;

/**
 * Resultado de validación de la respuesta del usuario.
 * Indica si cada elemento (sujeto, verbo, complemento) fue correcto.
 *
 * @param subjectCorrect    true si el sujeto elegido es el correcto
 * @param verbCorrect       true si el verbo elegido es el correcto
 * @param complementCorrect true si el complemento elegido es el correcto
 */
public record ValidationResult(
    boolean subjectCorrect,
    boolean verbCorrect,
    boolean complementCorrect
) {
    /**
     * @return true solo si los tres elementos fueron correctos
     */
    public boolean allCorrect() {
        return subjectCorrect && verbCorrect && complementCorrect;
    }
}
