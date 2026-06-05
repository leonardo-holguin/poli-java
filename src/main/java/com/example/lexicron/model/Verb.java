package com.example.lexicron.model;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representa un verbo francés con todas sus conjugaciones en presente simple.
 * Las conjugaciones se almacenan en un Map donde la clave es el pronombre sujeto
 * (ej: "je", "tu", "il") y el valor es un objeto {@link Conjugation} (o null si
 * el sujeto no aplica para ese verbo).
 *
 * @param infinitive  el infinitivo del verbo en francés (ej: "manger")
 * @param es          la traducción del infinitivo al español (ej: "comer")
 * @param conjugations mapa de conjugaciones por pronombre sujeto
 */
public record Verb(
    @JsonProperty("infinitive") String infinitive,
    @JsonProperty("es") String es,
    @JsonProperty("conjugations") Map<String, Conjugation> conjugations
) {
    @Override
    public String toString() {
        return infinitive;
    }
}
