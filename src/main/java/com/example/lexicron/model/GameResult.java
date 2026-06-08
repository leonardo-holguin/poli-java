package com.example.lexicron.model;

import java.util.List;

/**
 * Resultado completo de una partida.
 * Contiene la lista de resultados de cada ronda y el puntaje total acumulado.
 *
 * @param rounds     lista de resultados de cada ronda jugada
 * @param totalScore puntaje total acumulado al final de la partida
 */
public record GameResult(
    List<RoundResult> rounds,
    int totalScore
) {}
