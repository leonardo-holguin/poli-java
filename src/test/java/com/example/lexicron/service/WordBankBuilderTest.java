package com.example.lexicron.service;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.example.lexicron.model.Complement;
import com.example.lexicron.model.Conjugation;
import com.example.lexicron.model.Round;
import com.example.lexicron.model.RoundOptions;
import com.example.lexicron.model.Subject;
import com.example.lexicron.model.Verb;
import com.example.lexicron.model.WordCategory;
import com.example.lexicron.model.WordChip;

class WordBankBuilderTest {

    private final List<Subject> subjects = List.of(
        new Subject("je", "yo"),
        new Subject("tu", "tú"),
        new Subject("elle", "ella"),
        new Subject("ils", "ellos")
    );

    private final List<Verb> verbs = List.of(
        new Verb("regarder", "mirar", Map.of(
            "je", new Conjugation("regarde", "miro"),
            "tu", new Conjugation("regardes", "miras"),
            "elle", new Conjugation("regarde", "mira"),
            "ils", new Conjugation("regardent", "miran")
        )),
        new Verb("manger", "comer", Map.of(
            "je", new Conjugation("mange", "como"),
            "tu", new Conjugation("manges", "comes"),
            "elle", new Conjugation("mange", "come"),
            "ils", new Conjugation("mangent", "comen")
        )),
        new Verb("lire", "leer", Map.of(
            "je", new Conjugation("lis", "leo"),
            "tu", new Conjugation("lis", "lees"),
            "elle", new Conjugation("lit", "lee"),
            "ils", new Conjugation("lisent", "leen")
        )),
        new Verb("écouter", "escuchar", Map.of(
            "je", new Conjugation("écoute", "escucho"),
            "tu", new Conjugation("écoutes", "escuchas"),
            "elle", new Conjugation("écoute", "escucha"),
            "ils", new Conjugation("écoutent", "escuchan")
        ))
    );

    private final List<Complement> complements = List.of(
        new Complement("un film", "una película", List.of("regarder", "voir")),
        new Complement("un livre", "un libro", List.of("lire", "regarder")),
        new Complement("une pomme", "una manzana", List.of("manger")),
        new Complement("de la musique", "música", List.of("écouter"))
    );

    private final FrenchFormatter formatter = new FrenchFormatter();

    @Test
    void buildWordBank_returnsTwelveChips() {
        RoundGenerator generator = new RoundGenerator(subjects, verbs, complements);
        Round round = generator.generateRound();
        RoundOptions options = generator.generateOptions(round);

        WordBankBuilder builder = new WordBankBuilder(formatter, new Random(1));
        List<WordChip> chips = builder.buildWordBank(options);

        assertEquals(12, chips.size());
    }

    @Test
    void buildWordBank_containsFourChipsPerCategory() {
        RoundGenerator generator = new RoundGenerator(subjects, verbs, complements);
        Round round = generator.generateRound();
        RoundOptions options = generator.generateOptions(round);

        WordBankBuilder builder = new WordBankBuilder(formatter, new Random(1));
        List<WordChip> chips = builder.buildWordBank(options);

        long subjectsCount = chips.stream().filter(c -> c.category() == WordCategory.SUBJECT).count();
        long verbsCount = chips.stream().filter(c -> c.category() == WordCategory.VERB).count();
        long complementsCount = chips.stream().filter(c -> c.category() == WordCategory.COMPLEMENT).count();

        assertEquals(4, subjectsCount);
        assertEquals(4, verbsCount);
        assertEquals(4, complementsCount);
    }

    @Test
    void buildWordBank_includesCorrectAnswerSources() {
        RoundGenerator generator = new RoundGenerator(subjects, verbs, complements);
        Round round = generator.generateRound();
        RoundOptions options = generator.generateOptions(round);

        WordBankBuilder builder = new WordBankBuilder(formatter, new Random(1));
        List<WordChip> chips = builder.buildWordBank(options);

        boolean hasCorrectSubject = chips.stream()
            .filter(c -> c.category() == WordCategory.SUBJECT)
            .anyMatch(c -> c.source().equals(round.subject()));

        boolean hasCorrectVerb = chips.stream()
            .filter(c -> c.category() == WordCategory.VERB)
            .anyMatch(c -> c.source().equals(round.verb()));

        boolean hasCorrectComplement = chips.stream()
            .filter(c -> c.category() == WordCategory.COMPLEMENT)
            .anyMatch(c -> c.source().equals(round.complement()));

        assertTrue(hasCorrectSubject);
        assertTrue(hasCorrectVerb);
        assertTrue(hasCorrectComplement);
    }
}
