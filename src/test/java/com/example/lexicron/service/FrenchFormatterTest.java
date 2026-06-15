package com.example.lexicron.service;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.example.lexicron.model.Complement;
import com.example.lexicron.model.Conjugation;
import com.example.lexicron.model.Round;
import com.example.lexicron.model.Subject;
import com.example.lexicron.model.Verb;
import com.example.lexicron.model.VerbOption;

class FrenchFormatterTest {

    private final FrenchFormatter formatter = new FrenchFormatter();

    private final Subject je = new Subject("je", "yo");
    private final Subject elle = new Subject("elle", "ella");

    private final Verb regarder = new Verb("regarder", "mirar", Map.of(
        "je", new Conjugation("regarde", "miro"),
        "elle", new Conjugation("regarde", "mira")
    ));

    private final Verb ecrire = new Verb("écrire", "escribir", Map.of(
        "j'", new Conjugation("écris", "escribo"),
        "elle", new Conjugation("écrit", "escribe")
    ));

    @Test
    void getFrenchConjugation_returnsConjugatedForm() {
        assertEquals("regarde", formatter.getFrenchConjugation(regarder, je));
        assertEquals("regarde", formatter.getFrenchConjugation(regarder, elle));
    }

    @Test
    void getFrenchConjugation_usesJeApostropheWhenApplicable() {
        assertEquals("écris", formatter.getFrenchConjugation(ecrire, je));
    }

    @Test
    void getFrenchConjugation_fallsBackToInfinitive() {
        Verb incomplete = new Verb("parler", "hablar", Map.of());
        assertEquals("parler", formatter.getFrenchConjugation(incomplete, je));
    }

    @Test
    void displayVerbOption_returnsConjugationForAssociatedSubject() {
        VerbOption option = new VerbOption(regarder, elle);
        assertEquals("regarde", formatter.displayVerbOption(option));
    }

    @Test
    void buildCorrectSentence_joinsSubjectVerbAndComplement() {
        Complement complement = new Complement("un film", "una película", List.of("regarder"));
        Round round = new Round(elle, regarder, complement);

        assertEquals("elle regarde un film", formatter.buildCorrectSentence(round));
    }

    @Test
    void buildCorrectSentence_usesJeApostropheWhenApplicable() {
        Complement complement = new Complement("une lettre", "una carta", List.of("écrire"));
        Round round = new Round(je, ecrire, complement);

        assertEquals("j' écris une lettre", formatter.buildCorrectSentence(round));
    }
}
