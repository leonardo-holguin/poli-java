package com.example.lexicron.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representa un pronombre sujeto del francés con su traducción al español.
 *
 * @param fr el pronombre en francés (ej: "je", "tu", "elle")
 * @param es la traducción al español (ej: "yo", "tú", "ella")
 */
public record Subject(
    @JsonProperty("fr") String fr,
    @JsonProperty("es") String es
) {
    @Override
    public String toString() {
        return fr;
    }
}
