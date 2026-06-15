package com.example.lexicron.ui.game;

import com.example.lexicron.service.FrenchFormatter;
import com.example.lexicron.service.GameSession;
import com.example.lexicron.service.WordBankBuilder;
import com.example.lexicron.ui.viewmodel.GameViewModel;
import com.example.lexicron.ui.viewmodel.WordChipViewModel;

import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Controlador de la pantalla del juego.
 * Conecta los elementos visuales con el {@link GameViewModel}.
 */
public class GameController {

    private static final String PLACEHOLDER_TEXT = "—";
    private static final String STYLE_CORRECT = "slot-correct";
    private static final String STYLE_WRONG = "slot-wrong";

    @FXML private ProgressBar progressBar;
    @FXML private Label roundLabel;
    @FXML private Label scoreLabel;
    @FXML private Label spanishSentenceLabel;

    @FXML private Region feedbackSpacer;
    @FXML private VBox feedbackBanner;
    @FXML private Label feedbackTitle;
    @FXML private Label feedbackMessage;

    @FXML private Label wordBankLabel;
    @FXML private FlowPane wordBankPane;

    @FXML private VBox subjectSlotBox;
    @FXML private VBox verbSlotBox;
    @FXML private VBox complementSlotBox;
    @FXML private Label subjectSlotLabel;
    @FXML private Label verbSlotLabel;
    @FXML private Label complementSlotLabel;

    @FXML private Button validateButton;
    @FXML private Button continueButton;

    private GameViewModel viewModel;
    private Runnable onGameOver;

    /**
     * Inyecta la sesión de juego y construye el ViewModel asociado.
     *
     * @param gameSession     sesión de juego
     * @param wordBankBuilder generador del banco de palabras
     * @param frenchFormatter formateador de textos en francés
     * @param onGameOver      callback que se ejecuta cuando termina la partida
     */
    public void initializeGame(GameSession gameSession, WordBankBuilder wordBankBuilder,
                               FrenchFormatter frenchFormatter, Runnable onGameOver) {
        this.viewModel = new GameViewModel(gameSession, wordBankBuilder, frenchFormatter);
        this.onGameOver = onGameOver;
        bindView();
        viewModel.startNewGame();
    }

    private void bindView() {
        progressBar.progressProperty().bind(viewModel.progressProperty());
        roundLabel.textProperty().bind(
            Bindings.format("%d/%d", viewModel.currentRoundProperty(), viewModel.totalRoundsProperty())
        );
        scoreLabel.textProperty().bind(
            Bindings.format("★ %d pts", viewModel.scoreProperty())
        );
        spanishSentenceLabel.textProperty().bind(viewModel.spanishSentenceProperty());

        wordBankLabel.visibleProperty().bind(viewModel.feedbackVisibleProperty().not());
        wordBankLabel.managedProperty().bind(wordBankLabel.visibleProperty());
        wordBankPane.visibleProperty().bind(viewModel.feedbackVisibleProperty().not());
        wordBankPane.managedProperty().bind(wordBankPane.visibleProperty());

        feedbackSpacer.visibleProperty().bind(viewModel.feedbackVisibleProperty());
        feedbackSpacer.managedProperty().bind(feedbackSpacer.visibleProperty());

        feedbackBanner.visibleProperty().bind(viewModel.feedbackVisibleProperty());
        feedbackBanner.managedProperty().bind(viewModel.feedbackVisibleProperty());
        feedbackBanner.getStyleClass().add("feedback-banner");
        viewModel.feedbackStyleClassProperty().addListener((obs, oldStyle, newStyle) -> {
            if (oldStyle != null && !oldStyle.isEmpty()) {
                feedbackBanner.getStyleClass().remove(oldStyle);
            }
            if (newStyle != null && !newStyle.isEmpty()) {
                feedbackBanner.getStyleClass().add(newStyle);
            }
        });
        feedbackTitle.textProperty().bind(viewModel.feedbackTitleProperty());
        feedbackMessage.textProperty().bind(viewModel.feedbackMessageProperty());

        viewModel.subjectSlotProperty().addListener((obs, oldChip, newChip) -> updateSlot(subjectSlotLabel, newChip));
        viewModel.verbSlotProperty().addListener((obs, oldChip, newChip) -> updateSlot(verbSlotLabel, newChip));
        viewModel.complementSlotProperty().addListener((obs, oldChip, newChip) -> updateSlot(complementSlotLabel, newChip));

        viewModel.getWordBank().addListener((ListChangeListener<WordChipViewModel>) change -> {
            while (change.next()) {
                if (change.wasReplaced() || change.wasAdded() || change.wasRemoved()) {
                    rebuildWordBank();
                    return;
                }
            }
        });

        validateButton.visibleProperty().bind(viewModel.feedbackVisibleProperty().not());
        validateButton.managedProperty().bind(validateButton.visibleProperty());
        validateButton.disableProperty().bind(viewModel.canValidateProperty().not());

        continueButton.visibleProperty().bind(viewModel.continueVisibleProperty());
        continueButton.managedProperty().bind(continueButton.visibleProperty());
        continueButton.textProperty().bind(viewModel.continueButtonTextProperty());

        viewModel.feedbackVisibleProperty().addListener((obs, wasVisible, isVisible) -> {
            if (isVisible) {
                applySlotStyles();
            } else {
                clearSlotStyles();
            }
        });
    }

    private void rebuildWordBank() {
        wordBankPane.getChildren().clear();
        for (WordChipViewModel chip : viewModel.getWordBank()) {
            Button chipButton = createChipButton(chip);
            wordBankPane.getChildren().add(chipButton);
        }
    }

    private Button createChipButton(WordChipViewModel chip) {
        Button button = new Button(chip.getText());
        button.getStyleClass().add("word-chip");
        button.setOnAction(event -> {
            if (!viewModel.feedbackVisibleProperty().get()) {
                viewModel.placeChip(chip);
            }
        });

        chip.placedProperty().addListener((obs, wasPlaced, isPlaced) -> {
            if (isPlaced) {
                button.getStyleClass().add("word-chip-placed");
            } else {
                button.getStyleClass().remove("word-chip-placed");
            }
        });

        if (chip.isPlaced()) {
            button.getStyleClass().add("word-chip-placed");
        }

        return button;
    }

    private void updateSlot(Label slotLabel, WordChipViewModel chip) {
        slotLabel.setText(chip == null ? PLACEHOLDER_TEXT : chip.getText());
    }

    private void applySlotStyles() {
        setSlotStyle(subjectSlotBox, viewModel.subjectSlotCorrectProperty().get());
        setSlotStyle(verbSlotBox, viewModel.verbSlotCorrectProperty().get());
        setSlotStyle(complementSlotBox, viewModel.complementSlotCorrectProperty().get());
    }

    private void clearSlotStyles() {
        subjectSlotBox.getStyleClass().removeAll(STYLE_CORRECT, STYLE_WRONG);
        verbSlotBox.getStyleClass().removeAll(STYLE_CORRECT, STYLE_WRONG);
        complementSlotBox.getStyleClass().removeAll(STYLE_CORRECT, STYLE_WRONG);
    }

    private void setSlotStyle(VBox slotBox, boolean correct) {
        slotBox.getStyleClass().removeAll(STYLE_CORRECT, STYLE_WRONG);
        slotBox.getStyleClass().add(correct ? STYLE_CORRECT : STYLE_WRONG);
    }

    /**
     * Valida la respuesta actual cuando el usuario pulsa el botón VALIDAR.
     */
    @FXML
    private void handleValidate() {
        viewModel.validateRound();
    }

    /**
     * Avanza a la siguiente ronda o navega a resultados cuando el usuario pulsa CONTINUAR.
     */
    @FXML
    private void handleContinue() {
        if (viewModel.gameOverProperty().get()) {
            if (onGameOver != null) {
                onGameOver.run();
            }
        } else {
            viewModel.continueRound();
        }
    }
}
