package com.example.lexicron.service;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.example.lexicron.exception.LexicronException;
import com.example.lexicron.model.Complement;
import com.example.lexicron.model.Conjugation;
import com.example.lexicron.model.Round;
import com.example.lexicron.model.RoundOptions;
import com.example.lexicron.model.Subject;
import com.example.lexicron.model.Verb;

class RoundGeneratorTest {

    private final List<Subject> subjects = List.of(
        new Subject("je", "yo"),
        new Subject("tu", "tú"),
        new Subject("il", "él"),
        new Subject("elle", "ella"),
        new Subject("nous", "nosotros"),
        new Subject("vous", "usted"),
        new Subject("ils", "ellos"),
        new Subject("elles", "ellas")
    );

    private final List<Verb> verbs = List.of(
        new Verb("regarder", "mirar", Map.of(
            "je", new Conjugation("regarde", "miro"),
            "tu", new Conjugation("regardes", "miras"),
            "il", new Conjugation("regarde", "mira"),
            "elle", new Conjugation("regarde", "mira"),
            "nous", new Conjugation("regardons", "miramos"),
            "vous", new Conjugation("regardez", "mira"),
            "ils", new Conjugation("regardent", "miran"),
            "elles", new Conjugation("regardent", "miran")
        )),
        new Verb("manger", "comer", Map.of(
            "je", new Conjugation("mange", "como"),
            "tu", new Conjugation("manges", "comes"),
            "il", new Conjugation("mange", "come"),
            "elle", new Conjugation("mange", "come"),
            "nous", new Conjugation("mangeons", "comemos"),
            "vous", new Conjugation("mangez", "come"),
            "ils", new Conjugation("mangent", "comen"),
            "elles", new Conjugation("mangent", "comen")
        )),
        new Verb("lire", "leer", Map.of(
            "je", new Conjugation("lis", "leo"),
            "tu", new Conjugation("lis", "lees"),
            "il", new Conjugation("lit", "lee"),
            "elle", new Conjugation("lit", "lee"),
            "nous", new Conjugation("lisons", "leemos"),
            "vous", new Conjugation("lisez", "lee"),
            "ils", new Conjugation("lisent", "leen"),
            "elles", new Conjugation("lisent", "leen")
        )),
        new Verb("écrire", "escribir", Map.of(
            "j'", new Conjugation("écris", "escribo"),
            "tu", new Conjugation("écris", "escribes"),
            "il", new Conjugation("écrit", "escribe"),
            "elle", new Conjugation("écrit", "escribe"),
            "nous", new Conjugation("écrivons", "escribimos"),
            "vous", new Conjugation("écrivez", "escribe"),
            "ils", new Conjugation("écrivent", "escriben"),
            "elles", new Conjugation("écrivent", "escriben")
        ))
    );

    private final List<Complement> complements = List.of(
        new Complement("un film", "una película", List.of("regarder", "voir")),
        new Complement("un gâteau", "un pastel", List.of("manger", "faire", "préparer")),
        new Complement("un livre", "un libro", List.of("lire", "étudier")),
        new Complement("une lettre", "una carta", List.of("écrire", "envoyer", "recevoir"))
    );

    @Test
    void generateRound_returnsValidRound() {
        RoundGenerator generator = new RoundGenerator(subjects, verbs, complements);
        Round round = generator.generateRound();

        assertNotNull(round);
        assertNotNull(round.subject());
        assertNotNull(round.verb());
        assertNotNull(round.complement());

        assertTrue(subjects.contains(round.subject()));
        assertTrue(verbs.contains(round.verb()));
    }

    @Test
    void generateRound_complementIsCompatibleWithVerb() {
        RoundGenerator generator = new RoundGenerator(subjects, verbs, complements);
        Round round = generator.generateRound();

        String verbInfinitive = round.verb().infinitive();
        List<String> accepted = round.complement().acceptedVerbs();

        assertTrue(accepted.contains(verbInfinitive),
            () -> "Complement '" + round.complement().fr() + "' does not accept verb '" + verbInfinitive + "'");
    }

    @Test
    void generateRound_multipleCallsProduceVariedResults() {
        RoundGenerator generator = new RoundGenerator(subjects, verbs, complements);

        Round r1 = generator.generateRound();
        Round r2 = generator.generateRound();

        boolean sameSubject = r1.subject().equals(r2.subject());
        boolean sameVerb = r1.verb().equals(r2.verb());
        boolean sameComplement = r1.complement().equals(r2.complement());

        assertFalse(sameSubject && sameVerb && sameComplement,
            "Two consecutive rounds should not all be identical");
    }

    @Test
    void generateOptions_returnsFourOptionsPerCategory() {
        RoundGenerator generator = new RoundGenerator(subjects, verbs, complements);
        Round round = generator.generateRound();
        RoundOptions options = generator.generateOptions(round);

        assertEquals(4, options.subjectOptions().size());
        assertEquals(4, options.verbOptions().size());
        assertEquals(4, options.complementOptions().size());
    }

    @Test
    void generateOptions_correctAnswerIsIncluded() {
        RoundGenerator generator = new RoundGenerator(subjects, verbs, complements);
        Round round = generator.generateRound();
        RoundOptions options = generator.generateOptions(round);

        assertTrue(options.subjectOptions().contains(round.subject()));
        assertTrue(options.verbOptions().contains(round.verb()));
        assertTrue(options.complementOptions().contains(round.complement()));
    }

    @Test
    void generateOptions_optionsAreShuffled() {
        RoundGenerator generator = new RoundGenerator(subjects, verbs, complements);
        Round round = generator.generateRound();

        boolean allInOrder = true;
        for (int i = 0; i < 10; i++) {
            RoundOptions options = generator.generateOptions(round);
            if (!options.subjectOptions().get(0).equals(round.subject())) {
                allInOrder = false;
                break;
            }
        }
        assertFalse(allInOrder, "Correct subject should not always be first");
    }

    @Test
    void generateOptions_allOptionsComeFromOriginalList() {
        RoundGenerator generator = new RoundGenerator(subjects, verbs, complements);
        Round round = generator.generateRound();
        RoundOptions options = generator.generateOptions(round);

        for (Subject s : options.subjectOptions()) {
            assertTrue(subjects.contains(s));
        }
        for (Verb v : options.verbOptions()) {
            assertTrue(verbs.contains(v));
        }
        for (Complement c : options.complementOptions()) {
            assertTrue(complements.contains(c));
        }
    }

    @Test
    void generateRound_throwsWhenNoCompatibleComplement() {
        List<Verb> lonelyVerb = List.of(
            new Verb("courir", "correr", Map.of(
                "je", new Conjugation("cours", "corro"),
                "tu", new Conjugation("cours", "corres"),
                "il", new Conjugation("court", "corre"),
                "elle", new Conjugation("court", "corre"),
                "nous", new Conjugation("courons", "corremos"),
                "vous", new Conjugation("courez", "corre"),
                "ils", new Conjugation("courent", "corren"),
                "elles", new Conjugation("courent", "corren")
            ))
        );

        RoundGenerator generator = new RoundGenerator(subjects, lonelyVerb, complements);

        assertThrows(LexicronException.class, () -> generator.generateRound());
    }
}
