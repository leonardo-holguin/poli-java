package com.example.lexicron.model;

/**
 * Contiene la combinación correcta de sujeto, verbo y complemento para una ronda.
 * El objetivo del juego es que el usuario seleccione estos tres elementos
 * a partir de las opciones disponibles.
 *
 * @param subject    el sujeto correcto de la ronda
 * @param verb       el verbo correcto de la ronda
 * @param complement el complemento correcto de la ronda
 */
public record Round(
    Subject subject,
    Verb verb,
    Complement complement
) {}
