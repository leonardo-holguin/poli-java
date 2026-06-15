package com.example.lexicron.service;

import com.example.lexicron.model.Conjugation;
import com.example.lexicron.model.Round;
import com.example.lexicron.model.Subject;
import com.example.lexicron.model.Verb;
import com.example.lexicron.model.VerbOption;

/**
 * Formatea textos en francés a partir de los objetos del modelo.
 * Incluye la resolución de la clave de sujeto (incluyendo {@code j'}) y
 * la conjugación de verbos para construir frases y opciones de juego.
 */
public class FrenchFormatter {

    /**
     * Obtiene la forma conjugada en francés de un verbo para un sujeto dado.
     *
     * @param verb    el verbo en infinitivo
     * @param subject el sujeto para conjugar
     * @return la forma conjugada en francés, o el infinitivo si no hay conjugación
     */
    public String getFrenchConjugation(Verb verb, Subject subject) {
        String key = resolveSubjectKey(subject, verb);
        Conjugation conj = verb.conjugations().get(key);
        return conj != null ? conj.fr() : verb.infinitive();
    }

    /**
     * Genera el texto a mostrar para una opción de verbo.
     * Conjuga el verbo según el sujeto asociado.
     *
     * @param vo la opción de verbo con su sujeto asociado
     * @return el texto formateado (ej: "regarde")
     */
    public String displayVerbOption(VerbOption vo) {
        return getFrenchConjugation(vo.verb(), vo.conjugatedFor());
    }

    /**
     * Resuelve la clave de sujeto correcta para buscar la conjugación,
     * usando {@code j'} cuando el verbo no tiene conjugación para {@code je}.
     *
     * @param subject el sujeto a resolver
     * @param verb    el verbo cuya conjugación se busca
     * @return "j'" o el valor de {@code subject.fr()}
     */
    public String resolveSubjectKey(Subject subject, Verb verb) {
        String key = subject.fr();
        if ("je".equals(key) && verb.conjugations().get("je") == null
                && verb.conjugations().containsKey("j'")) {
            return "j'";
        }
        return key;
    }

    /**
     * Construye la frase correcta en francés para una ronda dada.
     *
     * @param round la ronda con la combinación correcta
     * @return la frase en francés (ej: "je regarde un film")
     */
    public String buildCorrectSentence(Round round) {
        String subjectKey = resolveSubjectKey(round.subject(), round.verb());
        String verbFr = getFrenchConjugation(round.verb(), round.subject());
        return subjectKey + " " + verbFr + " " + round.complement().fr();
    }
}
