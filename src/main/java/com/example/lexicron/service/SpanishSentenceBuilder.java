package com.example.lexicron.service;

import com.example.lexicron.model.Round;
import com.example.lexicron.model.Conjugation;
import java.util.Map;

/**
 * Construye la frase en español a partir de los objetos de una ronda.
 * Toma el sujeto, la conjugación del verbo y el complemento, todos en español,
 * y los combina en una oración gramaticalmente correcta.
 */
public class SpanishSentenceBuilder {

    /**
     * Construye la frase en español correspondiente a la ronda.
     * La primera letra se capitaliza y la frase termina con un punto.
     *
     * @param round la ronda con los objetos correctos
     * @return la frase en español (ej: "Ella mira una película.")
     * @throws IllegalStateException si no se encuentra la conjugación para el sujeto indicado
     */
    public String buildSentence(Round round) {
        String subjectEs = round.subject().es();
        String verbEs = getVerbConjugationEs(round);
        String complementEs = round.complement().es();

        String sentence = subjectEs + " " + verbEs + " " + complementEs;
        sentence = capitalize(sentence.trim()) + ".";

        return sentence;
    }

    private String getVerbConjugationEs(Round round) {
        String subjectKey = resolveSubjectKey(round);
        if ("on".equals(subjectKey)) {
            subjectKey = "nous";
        }
        Conjugation conj = round.verb().conjugations().get(subjectKey);
        if (conj == null) {
            throw new IllegalStateException(
                "No conjugation found for subject '" + subjectKey + "' in verb '" + round.verb().infinitive() + "'"
            );
        }
        return conj.es();
    }

    private String resolveSubjectKey(Round round) {
        Map<String, Conjugation> conjugations = round.verb().conjugations();
        String key = round.subject().fr();
        if ("je".equals(key) && conjugations.get("je") == null && conjugations.containsKey("j'")) {
            return "j'";
        }
        return key;
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }
}
