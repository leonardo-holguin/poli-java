package com.example.lexicron.model;

import java.util.List;

/**
 * Agrupa las opciones que se le presentan al usuario en una ronda.
 * Contiene la respuesta correcta y cuatro opciones mezcladas por cada categoría
 * (sujeto, verbo, complemento), de las cuales el usuario debe seleccionar una.
 *
 * @param correctRound       la combinación correcta de la ronda
 * @param subjectOptions     lista de 4 sujetos (uno correcto + 3 distractores)
 * @param verbOptions        lista de 4 verbos (uno correcto + 3 distractores)
 * @param complementOptions  lista de 4 complementos (uno correcto + 3 distractores)
 */
public record RoundOptions(
    Round correctRound,
    List<Subject> subjectOptions,
    List<Verb> verbOptions,
    List<Complement> complementOptions
) {}
