package com.example.lexicron.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import com.example.lexicron.model.Subject;
import com.example.lexicron.model.Verb;
import com.example.lexicron.model.Complement;
import com.example.lexicron.model.Round;
import com.example.lexicron.model.RoundOptions;
import com.example.lexicron.exception.LexicronException;

/**
 * Genera rondas de juego combinando sujetos, verbos y complementos aleatorios.
 * Se asegura de que el complemento sea compatible con el verbo elegido
 * mediante la lista de verbos aceptados de cada complemento.
 */
public class RoundGenerator {

    private final List<Subject> subjects;
    private final List<Verb> verbs;
    private final List<Complement> complements;
    private final Random random;

    /**
     * @param subjects    lista completa de sujetos disponibles
     * @param verbs       lista completa de verbos disponibles
     * @param complements lista completa de complementos disponibles
     */
    public RoundGenerator(List<Subject> subjects, List<Verb> verbs, List<Complement> complements) {
        this.subjects = subjects;
        this.verbs = verbs;
        this.complements = complements;
        this.random = new Random();
    }

    /**
     * Genera una ronda eligiendo un sujeto, un verbo y un complemento compatible al azar.
     * Solo se eligen verbos que tengan al menos un complemento compatible, por lo que
     * {@code pickComplementFor} siempre encontrará una coincidencia.
     *
     * @return una nueva ronda con la combinación correcta
     * @throws LexicronException si ning&uacute;n verbo tiene complementos compatibles
     */
    public Round generateRound() {
        Subject subject = pickRandom(subjects);
        Verb verb = pickVerbWithComplement();
        Complement complement = pickComplementFor(verb);

        return new Round(subject, verb, complement);
    }

    /**
     * Genera las opciones que se mostrarán al usuario para una ronda.
     * Para cada categoría (sujeto, verbo, complemento) se incluye la respuesta
     * correcta mezclada con tres distractores aleatorios.
     *
     * @param correctRound la ronda correcta generada previamente
     * @return objeto con las listas de opciones mezcladas por categoría
     */
    public RoundOptions generateOptions(Round correctRound) {
        List<Subject> subjectOptions = buildOptions(subjects, correctRound.subject());
        List<Verb> verbOptions = buildOptions(verbs, correctRound.verb());
        List<Complement> complementOptions = buildOptions(complements, correctRound.complement());

        return new RoundOptions(correctRound, subjectOptions, verbOptions, complementOptions);
    }

    private <T> List<T> buildOptions(List<T> allItems, T correctItem) {
        List<T> distractors = allItems.stream()
            .filter(item -> !item.equals(correctItem))
            .collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(distractors, random);

        List<T> options = new ArrayList<>();
        options.add(correctItem);
        options.addAll(distractors.stream().limit(3).toList());
        Collections.shuffle(options, random);

        return options;
    }

    /**
     * Elige un verbo al azar que tenga al menos un complemento compatible.
     *
     * @return un verbo con complementos disponibles
     * @throws LexicronException si ning&uacute;n verbo tiene complementos compatibles
     */
    private Verb pickVerbWithComplement() {
        List<Verb> compatible = verbs.stream()
            .filter(v -> complements.stream().anyMatch(c -> c.acceptedVerbs().contains(v.infinitive())))
            .collect(Collectors.toList());

        if (compatible.isEmpty()) {
            throw new LexicronException("No verb has any compatible complement");
        }

        return pickRandom(compatible);
    }

    private Complement pickComplementFor(Verb verb) {
        List<Complement> valid = complements.stream()
            .filter(c -> c.acceptedVerbs().contains(verb.infinitive()))
            .collect(Collectors.toList());

        return pickRandom(valid);
    }

    private <T> T pickRandom(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }
}
