package com.example.lexicron.ui.viewmodel;

import com.example.lexicron.model.Complement;
import com.example.lexicron.model.RoundResult;
import com.example.lexicron.model.Subject;
import com.example.lexicron.model.Verb;
import com.example.lexicron.model.WordCategory;
import com.example.lexicron.model.WordChip;
import com.example.lexicron.service.FrenchFormatter;
import com.example.lexicron.service.GameSession;
import com.example.lexicron.service.WordBankBuilder;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * ViewModel observable de la pantalla del juego.
 * Expone el estado de la ronda actual, coordina la interacción entre la vista
 * y la sesión de juego, y gestiona la visualización del feedback tras validar.
 */
public class GameViewModel {

    private static final String STYLE_CORRECT = "feedback-correct";
    private static final String STYLE_PARTIAL = "feedback-partial";
    private static final String STYLE_WRONG = "feedback-wrong";

    private final GameSession gameSession;
    private final WordBankBuilder wordBankBuilder;
    private final FrenchFormatter frenchFormatter;

    private final IntegerProperty currentRound = new SimpleIntegerProperty(0);
    private final IntegerProperty totalRounds = new SimpleIntegerProperty(5);
    private final IntegerProperty score = new SimpleIntegerProperty(0);
    private final DoubleProperty progress = new SimpleDoubleProperty(0);
    private final StringProperty spanishSentence = new SimpleStringProperty("");

    private final ObservableList<WordChipViewModel> wordBank = FXCollections.observableArrayList();
    private final ObjectProperty<WordChipViewModel> subjectSlot = new SimpleObjectProperty<>();
    private final ObjectProperty<WordChipViewModel> verbSlot = new SimpleObjectProperty<>();
    private final ObjectProperty<WordChipViewModel> complementSlot = new SimpleObjectProperty<>();
    private final BooleanProperty canValidate = new SimpleBooleanProperty(false);

    private final BooleanProperty feedbackVisible = new SimpleBooleanProperty(false);
    private final StringProperty feedbackTitle = new SimpleStringProperty("");
    private final StringProperty feedbackMessage = new SimpleStringProperty("");
    private final StringProperty feedbackStyleClass = new SimpleStringProperty("");
    private final BooleanProperty continueVisible = new SimpleBooleanProperty(false);
    private final StringProperty continueButtonText = new SimpleStringProperty("CONTINUAR →");
    private final BooleanProperty gameOver = new SimpleBooleanProperty(false);

    private final BooleanProperty subjectSlotCorrect = new SimpleBooleanProperty(false);
    private final BooleanProperty verbSlotCorrect = new SimpleBooleanProperty(false);
    private final BooleanProperty complementSlotCorrect = new SimpleBooleanProperty(false);

    /**
     * @param gameSession     sesión de juego con la lógica de rondas
     * @param wordBankBuilder generador del banco de palabras
     * @param frenchFormatter formateador de textos en francés
     */
    public GameViewModel(GameSession gameSession, WordBankBuilder wordBankBuilder, FrenchFormatter frenchFormatter) {
        this.gameSession = gameSession;
        this.wordBankBuilder = wordBankBuilder;
        this.frenchFormatter = frenchFormatter;

        subjectSlot.addListener((obs, oldChip, newChip) -> updateCanValidate());
        verbSlot.addListener((obs, oldChip, newChip) -> updateCanValidate());
        complementSlot.addListener((obs, oldChip, newChip) -> updateCanValidate());
    }

    /**
     * Inicia una nueva partida y carga la primera ronda.
     */
    public void startNewGame() {
        gameSession.startNewGame();
        loadRound();
    }

    /**
     * Coloca un chip en la casilla correspondiente a su categoría.
     * Si la casilla ya contenía otro chip, este último vuelve al banco.
     *
     * @param chip chip seleccionado por el usuario
     */
    public void placeChip(WordChipViewModel chip) {
        ObjectProperty<WordChipViewModel> targetSlot = getSlotFor(chip.getWordChip().category());

        WordChipViewModel previousChip = targetSlot.get();
        if (previousChip != null && previousChip != chip) {
            previousChip.setPlaced(false);
        }

        targetSlot.set(chip);
        chip.setPlaced(true);
    }

    /**
     * Valida la respuesta actual, actualiza el puntaje y muestra el feedback.
     *
     * @return resultado de la ronda validada
     */
    public RoundResult validateRound() {
        Subject subject = (Subject) subjectSlot.get().getWordChip().source();
        Verb verb = (Verb) verbSlot.get().getWordChip().source();
        Complement complement = (Complement) complementSlot.get().getWordChip().source();

        RoundResult result = gameSession.submitAnswer(subject, verb, complement);
        score.set(gameSession.getTotalScore());

        subjectSlotCorrect.set(result.validationResult().subjectCorrect());
        verbSlotCorrect.set(result.validationResult().verbCorrect());
        complementSlotCorrect.set(result.validationResult().complementCorrect());

        String correctSentence = frenchFormatter.buildCorrectSentence(result.round());
        configureFeedback(result, correctSentence);

        boolean isGameOver = gameSession.isGameOver();
        gameOver.set(isGameOver);
        continueButtonText.set(isGameOver ? "VER RESULTADOS" : "CONTINUAR →");
        feedbackVisible.set(true);
        continueVisible.set(true);
        canValidate.set(false);

        if (isGameOver) {
            gameSession.saveFinalScore();
        }

        return result;
    }

    /**
     * Avanza a la siguiente ronda y oculta el feedback.
     */
    public void continueRound() {
        if (gameSession.isGameOver()) {
            return;
        }
        gameSession.startNextRound();
        loadRound();
    }

    private void configureFeedback(RoundResult result, String correctSentence) {
        if (result.validationResult().allCorrect()) {
            feedbackTitle.set("¡Correcto!");
            feedbackMessage.set("Has formado la frase en francés correctamente");
            feedbackStyleClass.set(STYLE_CORRECT);
        } else if (result.points() > 0) {
            feedbackTitle.set("¡Casi!");
            feedbackMessage.set("La frase correcta es: " + correctSentence);
            feedbackStyleClass.set(STYLE_PARTIAL);
        } else {
            feedbackTitle.set("¡Ups!");
            feedbackMessage.set("La frase correcta es: " + correctSentence);
            feedbackStyleClass.set(STYLE_WRONG);
        }
    }

    private void loadRound() {
        currentRound.set(gameSession.getCurrentRoundNumber());
        totalRounds.set(gameSession.getTotalRounds());
        progress.set((double) gameSession.getCurrentRoundNumber() / gameSession.getTotalRounds());
        score.set(gameSession.getTotalScore());
        spanishSentence.set(gameSession.getSpanishSentence());

        wordBank.clear();
        subjectSlot.set(null);
        verbSlot.set(null);
        complementSlot.set(null);

        feedbackVisible.set(false);
        continueVisible.set(false);
        continueButtonText.set("CONTINUAR →");
        gameOver.set(false);
        subjectSlotCorrect.set(false);
        verbSlotCorrect.set(false);
        complementSlotCorrect.set(false);

        for (WordChip chip : wordBankBuilder.buildWordBank(gameSession.getCurrentOptions())) {
            wordBank.add(new WordChipViewModel(chip));
        }
    }

    private ObjectProperty<WordChipViewModel> getSlotFor(WordCategory category) {
        return switch (category) {
            case SUBJECT -> subjectSlot;
            case VERB -> verbSlot;
            case COMPLEMENT -> complementSlot;
        };
    }

    private void updateCanValidate() {
        boolean slotsFilled = subjectSlot.get() != null && verbSlot.get() != null && complementSlot.get() != null;
        canValidate.set(slotsFilled && !feedbackVisible.get());
    }

    public IntegerProperty currentRoundProperty() {
        return currentRound;
    }

    public IntegerProperty totalRoundsProperty() {
        return totalRounds;
    }

    public IntegerProperty scoreProperty() {
        return score;
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    public StringProperty spanishSentenceProperty() {
        return spanishSentence;
    }

    public ObservableList<WordChipViewModel> getWordBank() {
        return wordBank;
    }

    public ObjectProperty<WordChipViewModel> subjectSlotProperty() {
        return subjectSlot;
    }

    public ObjectProperty<WordChipViewModel> verbSlotProperty() {
        return verbSlot;
    }

    public ObjectProperty<WordChipViewModel> complementSlotProperty() {
        return complementSlot;
    }

    public BooleanProperty canValidateProperty() {
        return canValidate;
    }

    public BooleanProperty feedbackVisibleProperty() {
        return feedbackVisible;
    }

    public StringProperty feedbackTitleProperty() {
        return feedbackTitle;
    }

    public StringProperty feedbackMessageProperty() {
        return feedbackMessage;
    }

    public StringProperty feedbackStyleClassProperty() {
        return feedbackStyleClass;
    }

    public BooleanProperty continueVisibleProperty() {
        return continueVisible;
    }

    public StringProperty continueButtonTextProperty() {
        return continueButtonText;
    }

    public BooleanProperty gameOverProperty() {
        return gameOver;
    }

    public BooleanProperty subjectSlotCorrectProperty() {
        return subjectSlotCorrect;
    }

    public BooleanProperty verbSlotCorrectProperty() {
        return verbSlotCorrect;
    }

    public BooleanProperty complementSlotCorrectProperty() {
        return complementSlotCorrect;
    }
}
