package com.example.lexicron.service;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;

import com.example.lexicron.model.*;

import static org.junit.jupiter.api.Assertions.*;

class RoundValidatorTest {

    private final Round correctRound = new Round(
        new Subject("je", "yo"),
        new Verb("manger", "comer", Map.of("je", new Conjugation("mange", "como"))),
        new Complement("un gâteau", "un pastel", List.of("manger"))
    );

    private final RoundValidator validator = new RoundValidator();

    @Test
    void validate_allCorrect() {
        ValidationResult result = validator.validate(
            correctRound,
            new Subject("je", "yo"),
            new Verb("manger", "comer", Map.of("je", new Conjugation("mange", "como"))),
            new Complement("un gâteau", "un pastel", List.of("manger"))
        );

        assertTrue(result.subjectCorrect());
        assertTrue(result.verbCorrect());
        assertTrue(result.complementCorrect());
        assertTrue(result.allCorrect());
    }

    @Test
    void validate_wrongSubject() {
        ValidationResult result = validator.validate(
            correctRound,
            new Subject("tu", "tú"),
            new Verb("manger", "comer", Map.of("je", new Conjugation("mange", "como"))),
            new Complement("un gâteau", "un pastel", List.of("manger"))
        );

        assertFalse(result.subjectCorrect());
        assertTrue(result.verbCorrect());
        assertTrue(result.complementCorrect());
        assertFalse(result.allCorrect());
    }

    @Test
    void validate_wrongVerb() {
        ValidationResult result = validator.validate(
            correctRound,
            new Subject("je", "yo"),
            new Verb("lire", "leer", Map.of("je", new Conjugation("lis", "leo"))),
            new Complement("un gâteau", "un pastel", List.of("manger"))
        );

        assertTrue(result.subjectCorrect());
        assertFalse(result.verbCorrect());
        assertTrue(result.complementCorrect());
        assertFalse(result.allCorrect());
    }

    @Test
    void validate_wrongComplement() {
        ValidationResult result = validator.validate(
            correctRound,
            new Subject("je", "yo"),
            new Verb("manger", "comer", Map.of("je", new Conjugation("mange", "como"))),
            new Complement("un film", "una película", List.of("regarder"))
        );

        assertTrue(result.subjectCorrect());
        assertTrue(result.verbCorrect());
        assertFalse(result.complementCorrect());
        assertFalse(result.allCorrect());
    }

    @Test
    void validate_allWrong() {
        ValidationResult result = validator.validate(
            correctRound,
            new Subject("ils", "ellos"),
            new Verb("courir", "correr", Map.of("je", new Conjugation("cours", "corro"))),
            new Complement("un film", "una película", List.of("regarder"))
        );

        assertFalse(result.subjectCorrect());
        assertFalse(result.verbCorrect());
        assertFalse(result.complementCorrect());
        assertFalse(result.allCorrect());
    }
}
