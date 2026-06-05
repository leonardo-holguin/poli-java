package com.example.lexicron.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representa un complemento de frase con su traducción y la lista de verbos
 * que son compatibles con él. Se usa para construir oraciones completas donde
 * el complemento debe concordar con el verbo elegido.
 *
 * @param fr           el complemento en francés (ej: "un film")
 * @param es           la traducción al español (ej: "una película")
 * @param acceptedVerbs lista de infinitivos de verbos aceptados para este complemento
 */
public record Complement(
    @JsonProperty("fr") String fr,
    @JsonProperty("es") String es,
    @JsonProperty("acceptedVerbs") List<String> acceptedVerbs
) {
    @Override
    public String toString() {
        return fr;
    }
}
