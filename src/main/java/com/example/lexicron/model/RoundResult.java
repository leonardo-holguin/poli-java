package com.example.lexicron.model;

/**
 * Resultado de una ronda individual del juego.
 * Guarda la ronda que se presentó, el resultado de la validación
 * y los puntos obtenidos en esa ronda.
 *
 * @param round            la ronda que se jugó (combinación correcta)
 * @param validationResult resultado de la validación de la respuesta del usuario
 * @param points           puntos obtenidos en esta ronda (0, 40, 70 o 100)
 */
public record RoundResult(
    Round round,
    ValidationResult validationResult,
    int points
) {}
