package com.example.lexicron.service;

import java.util.ArrayList;
import java.util.List;

import com.example.lexicron.model.Complement;
import com.example.lexicron.model.GameResult;
import com.example.lexicron.model.Round;
import com.example.lexicron.model.RoundOptions;
import com.example.lexicron.model.RoundResult;
import com.example.lexicron.model.Subject;
import com.example.lexicron.model.ValidationResult;
import com.example.lexicron.model.Verb;

/**
 * Orquesta una partida completa de Lexicron.
 * Mantiene el estado de la partida (ronda actual, resultados y puntaje total)
 * y coordina la generación de rondas, la validación de respuestas y el guardado final.
 */
public class GameSession {

    private static final int TOTAL_ROUNDS = 5;

    private final RoundGenerator generator;
    private final SpanishSentenceBuilder sentenceBuilder;
    private final RoundValidator validator;
    private final ScoreCalculator scoreCalculator;
    private final ScoreSaver scoreSaver;

    private final List<RoundResult> roundResults;
    private int currentRoundNumber;
    private Round currentRound;
    private RoundOptions currentOptions;
    private int totalScore;

    /**
     * @param generator         generador de rondas
     * @param sentenceBuilder   constructor de frases en español
     * @param validator         validador de respuestas
     * @param scoreCalculator   calculador de puntajes
     * @param scoreSaver        guardador de puntajes finales
     */
    public GameSession(RoundGenerator generator, SpanishSentenceBuilder sentenceBuilder,
                       RoundValidator validator, ScoreCalculator scoreCalculator,
                       ScoreSaver scoreSaver) {
        this.generator = generator;
        this.sentenceBuilder = sentenceBuilder;
        this.validator = validator;
        this.scoreCalculator = scoreCalculator;
        this.scoreSaver = scoreSaver;
        this.roundResults = new ArrayList<>();
        this.currentRoundNumber = 0;
        this.totalScore = 0;
    }

    /**
     * Reinicia la partida y genera la primera ronda.
     */
    public void startNewGame() {
        roundResults.clear();
        totalScore = 0;
        currentRoundNumber = 0;
        startNextRound();
    }

    /**
     * Avanza a la siguiente ronda generando una nueva combinación y opciones.
     *
     * @return {@code false} si ya se jugaron todas las rondas
     */
    public boolean startNextRound() {
        if (currentRoundNumber >= TOTAL_ROUNDS) {
            return false;
        }
        currentRound = generator.generateRound();
        currentOptions = generator.generateOptions(currentRound);
        currentRoundNumber++;
        return true;
    }

    /**
     * Valida la respuesta del usuario para la ronda actual, calcula los puntos
     * y almacena el resultado.
     *
     * @param chosenSubject    sujeto elegido
     * @param chosenVerb       verbo elegido
     * @param chosenComplement complemento elegido
     * @return el resultado de la ronda con validación y puntos
     */
    public RoundResult submitAnswer(Subject chosenSubject, Verb chosenVerb, Complement chosenComplement) {
        ValidationResult result = validator.validate(currentRound, chosenSubject, chosenVerb, chosenComplement);
        int points = scoreCalculator.calculatePoints(result);
        RoundResult roundResult = new RoundResult(currentRound, result, points);
        roundResults.add(roundResult);
        totalScore += points;
        return roundResult;
    }

    /**
     * Guarda el puntaje final si la partida ha terminado.
     */
    public void saveFinalScore() {
        if (isGameOver()) {
            scoreSaver.saveScore(getGameResult());
        }
    }

    /**
     * @return frase en español de la ronda actual
     */
    public String getSpanishSentence() {
        return sentenceBuilder.buildSentence(currentRound);
    }

    /**
     * @return opciones de respuesta de la ronda actual
     */
    public RoundOptions getCurrentOptions() {
        return currentOptions;
    }

    /**
     * @return número de ronda actual (1-based)
     */
    public int getCurrentRoundNumber() {
        return currentRoundNumber;
    }

    /**
     * @return cantidad total de rondas de la partida
     */
    public int getTotalRounds() {
        return TOTAL_ROUNDS;
    }

    /**
     * @return puntaje acumulado hasta el momento
     */
    public int getTotalScore() {
        return totalScore;
    }

    /**
     * @return {@code true} si ya se completaron todas las rondas
     */
    public boolean isGameOver() {
        return currentRoundNumber >= TOTAL_ROUNDS && roundResults.size() >= TOTAL_ROUNDS;
    }

    /**
     * @return resultado completo de la partida
     */
    public GameResult getGameResult() {
        return new GameResult(new ArrayList<>(roundResults), totalScore);
    }
}
