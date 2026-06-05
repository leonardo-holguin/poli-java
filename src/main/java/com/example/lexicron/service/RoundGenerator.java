package com.example.lexicron.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import com.example.lexicron.model.Complement;
import com.example.lexicron.model.Conjugation;
import com.example.lexicron.model.Round;
import com.example.lexicron.model.RoundOptions;
import com.example.lexicron.model.Subject;
import com.example.lexicron.model.Verb;
import com.example.lexicron.model.VerbOption;
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
        Verb verb = pickVerbWithComplement();
        Subject subject = pickSubjectFor(verb);
        Complement complement = pickComplementFor(verb);

        return new Round(subject, verb, complement);
    }

    private Subject pickSubjectFor(Verb verb) {
        List<Subject> compatible = subjects.stream()
            .filter(s -> hasConjugationFor(s, verb))
            .collect(Collectors.toList());
        return pickRandom(compatible);
    }

    private boolean hasConjugationFor(Subject subject, Verb verb) {
        return verb.conjugations().get(subject.fr()) != null;
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
        List<Subject> subjectOptions = buildSubjectOptions(correctRound);
        List<VerbOption> verbOptions = buildVerbOptions(correctRound);
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

    private List<Subject> buildSubjectOptions(Round correctRound) {
        Subject correct = correctRound.subject();
        List<Subject> distractors = subjects.stream()
            .filter(s -> !s.equals(correct))
            .filter(s -> !sonEquivalentes(s, correct))
            .collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(distractors, random);

        List<Subject> options = new ArrayList<>();
        options.add(correct);
        options.addAll(distractors.stream().limit(3).toList());
        Collections.shuffle(options, random);

        return options;
    }

    private static boolean sonEquivalentes(Subject a, Subject b) {
        String af = a.fr();
        String bf = b.fr();
        return ("on".equals(af) && "nous".equals(bf))
            || ("nous".equals(af) && "on".equals(bf));
    }

    private List<VerbOption> buildVerbOptions(Round correctRound) {
        List<Verb> distractors = verbs.stream()
            .filter(v -> !v.equals(correctRound.verb()))
            .filter(v -> v.conjugations().values().stream().anyMatch(c -> c != null))
            .collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(distractors, random);

        List<VerbOption> options = new ArrayList<>();
        options.add(new VerbOption(correctRound.verb(), correctRound.subject()));

        for (Verb distractor : distractors.stream().limit(3).toList()) {
            Subject randomSubject = pickRandomSubjectFor(distractor, correctRound.subject());
            options.add(new VerbOption(distractor, randomSubject));
        }

        Collections.shuffle(options, random);
        return options;
    }

    private Subject pickRandomSubjectFor(Verb verb, Subject excludeSubject) {
        List<Subject> valid = subjects.stream()
            .filter(s -> !s.equals(excludeSubject))
            .filter(s -> hasConjugationFor(s, verb))
            .collect(Collectors.toList());

        if (valid.isEmpty()) {
            valid = subjects.stream()
                .filter(s -> hasConjugationFor(s, verb))
                .collect(Collectors.toList());
        }

        return pickRandom(valid);
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
