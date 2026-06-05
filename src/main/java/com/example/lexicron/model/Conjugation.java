package com.example.lexicron.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Almacena la forma conjugada de un verbo en francés y su equivalente en español
 * para un sujeto específico.
 *
 * @param fr la forma conjugada en francés (ej: "mange", "finis")
 * @param es la traducción al español (ej: "como", "terminas")
 */
public record Conjugation(
    @JsonProperty("fr") String fr,
    @JsonProperty("es") String es
) {}
