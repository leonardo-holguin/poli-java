package com.example.lexicron.service;

import com.example.lexicron.model.ValidationResult;

/**
 * Calcula la puntuación de una ronda según la cantidad de aciertos.
 * <ul>
 *   <li>3 aciertos (todo correcto): 100 puntos</li>
 *   <li>2 aciertos: 70 puntos</li>
 *   <li>1 acierto: 40 puntos</li>
 *   <li>0 aciertos: 0 puntos</li>
 * </ul>
 */
public class ScoreCalculator {

    /**
     * Calcula los puntos obtenidos en una ronda.
     *
     * @param result resultado de la validación de la respuesta del usuario
     * @return puntuación según la cantidad de aciertos
     */
    public int calculatePoints(ValidationResult result) {
        int correctCount = 0;
        if (result.subjectCorrect()) correctCount++;
        if (result.verbCorrect()) correctCount++;
        if (result.complementCorrect()) correctCount++;

        return switch (correctCount) {
            case 3 -> 100;
            case 2 -> 70;
            case 1 -> 40;
            default -> 0;
        };
    }
}
