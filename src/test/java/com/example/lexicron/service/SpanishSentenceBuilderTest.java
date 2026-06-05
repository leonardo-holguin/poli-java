package com.example.lexicron.service;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;

import com.example.lexicron.model.*;

import static org.junit.jupiter.api.Assertions.*;

class SpanishSentenceBuilderTest {

    private final SpanishSentenceBuilder builder = new SpanishSentenceBuilder();

    @Test
    void buildSentence_formsCorrectSpanishSentence() {
        Subject subject = new Subject("elle", "ella");
        Verb verb = new Verb("regarder", "mirar", Map.of(
            "elle", new Conjugation("regarde", "mira")
        ));
        Complement complement = new Complement("un film", "una película", List.of("regarder"));

        Round round = new Round(subject, verb, complement);
        String sentence = builder.buildSentence(round);

        assertEquals("Ella mira una película.", sentence);
    }

    @Test
    void buildSentence_startsWithCapitalLetter() {
        Subject subject = new Subject("je", "yo");
        Verb verb = new Verb("manger", "comer", Map.of(
            "je", new Conjugation("mange", "como")
        ));
        Complement complement = new Complement("un gâteau", "un pastel", List.of("manger"));

        Round round = new Round(subject, verb, complement);
        String sentence = builder.buildSentence(round);

        assertTrue(Character.isUpperCase(sentence.charAt(0)));
    }

    @Test
    void buildSentence_endsWithPeriod() {
        Subject subject = new Subject("nous", "nosotros");
        Verb verb = new Verb("manger", "comer", Map.of(
            "nous", new Conjugation("mangeons", "comemos")
        ));
        Complement complement = new Complement("un gâteau", "un pastel", List.of("manger"));

        Round round = new Round(subject, verb, complement);
        String sentence = builder.buildSentence(round);

        assertTrue(sentence.endsWith("."));
    }

    @Test
    void buildSentence_throwsWhenConjugationMissing() {
        Subject subject = new Subject("je", "yo");
        Verb verb = new Verb("manger", "comer", Map.of(
            "tu", new Conjugation("manges", "comes")
        ));
        Complement complement = new Complement("un gâteau", "un pastel", List.of("manger"));

        Round round = new Round(subject, verb, complement);

        assertThrows(IllegalStateException.class, () -> builder.buildSentence(round));
    }

    @Test
    void buildSentence_withJConjugation() {
        Subject subject = new Subject("j'", "yo");
        Verb verb = new Verb("acheter", "comprar", Map.of(
            "j'", new Conjugation("achète", "compro")
        ));
        Complement complement = new Complement("un livre", "un libro", List.of("acheter"));

        Round round = new Round(subject, verb, complement);
        String sentence = builder.buildSentence(round);

        assertEquals("Yo compro un libro.", sentence);
    }
}
