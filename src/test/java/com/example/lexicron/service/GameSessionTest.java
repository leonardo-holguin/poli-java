package com.example.lexicron.service;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.example.lexicron.model.Complement;
import com.example.lexicron.model.Conjugation;
import com.example.lexicron.model.Round;
import com.example.lexicron.model.RoundOptions;
import com.example.lexicron.model.RoundResult;
import com.example.lexicron.model.Subject;
import com.example.lexicron.model.Verb;

class GameSessionTest {

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
        ))
    );

    private final List<Complement> complements = List.of(
        new Complement("un film", "una película", List.of("regarder", "voir")),
        new Complement("un livre", "un libro", List.of("lire", "regarder")),
        new Complement("une pomme", "una manzana", List.of("manger")),
        new Complement("de la musique", "música", List.of("écouter"))
    );

    private GameSession createSession() {
        RoundGenerator generator = new RoundGenerator(subjects, verbs, complements);
        SpanishSentenceBuilder sentenceBuilder = new SpanishSentenceBuilder();
        RoundValidator validator = new RoundValidator();
        ScoreCalculator scoreCalculator = new ScoreCalculator();
        ScoreSaver scoreSaver = new ScoreSaver(Path.of(System.getProperty("java.io.tmpdir"), "lexicron-test-scores.txt"));

        return new GameSession(generator, sentenceBuilder, validator, scoreCalculator, scoreSaver);
    }

    @Test
    void startNewGame_initializesFirstRound() {
        GameSession session = createSession();
        session.startNewGame();

        assertEquals(1, session.getCurrentRoundNumber());
        assertEquals(0, session.getTotalScore());
        assertFalse(session.isGameOver());
        assertNotNull(session.getSpanishSentence());
        assertNotNull(session.getCurrentOptions());
    }

    @Test
    void submitAnswer_withCorrectChoice_awardsFullPoints() {
        GameSession session = createSession();
        session.startNewGame();

        Round round = session.getCurrentOptions().correctRound();
        RoundResult result = session.submitAnswer(round.subject(), round.verb(), round.complement());

        assertTrue(result.validationResult().allCorrect());
        assertEquals(100, result.points());
        assertEquals(100, session.getTotalScore());
    }

    @Test
    void submitAnswer_withPartialChoice_awardsPartialPoints() {
        GameSession session = createSession();
        session.startNewGame();

        Round round = session.getCurrentOptions().correctRound();
        Subject wrongSubject = subjects.stream()
            .filter(s -> !s.equals(round.subject()))
            .findFirst()
            .orElseThrow();

        RoundResult result = session.submitAnswer(wrongSubject, round.verb(), round.complement());

        assertFalse(result.validationResult().allCorrect());
        assertEquals(70, result.points());
        assertEquals(70, session.getTotalScore());
    }

    @Test
    void startNextRound_advancesRoundCounter() {
        GameSession session = createSession();
        session.startNewGame();

        Round round1 = session.getCurrentOptions().correctRound();
        session.submitAnswer(round1.subject(), round1.verb(), round1.complement());

        assertTrue(session.startNextRound());
        assertEquals(2, session.getCurrentRoundNumber());
    }

    @Test
    void isGameOver_returnsTrueAfterFiveRounds() {
        GameSession session = createSession();
        session.startNewGame();

        for (int i = 0; i < 5; i++) {
            Round round = session.getCurrentOptions().correctRound();
            session.submitAnswer(round.subject(), round.verb(), round.complement());
            if (i < 4) {
                session.startNextRound();
            }
        }

        assertTrue(session.isGameOver());
        assertEquals(500, session.getTotalScore());
    }
}
