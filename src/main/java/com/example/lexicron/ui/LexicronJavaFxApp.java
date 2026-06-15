package com.example.lexicron.ui;

import java.io.IOException;
import java.util.List;

import com.example.lexicron.exception.LexicronException;
import com.example.lexicron.model.Complement;
import com.example.lexicron.model.Subject;
import com.example.lexicron.model.Verb;
import com.example.lexicron.service.DataLoader;
import com.example.lexicron.service.FrenchFormatter;
import com.example.lexicron.service.GameSession;
import com.example.lexicron.service.RoundGenerator;
import com.example.lexicron.service.RoundValidator;
import com.example.lexicron.service.ScoreCalculator;
import com.example.lexicron.service.ScoreSaver;
import com.example.lexicron.service.SpanishSentenceBuilder;
import com.example.lexicron.service.WordBankBuilder;
import com.example.lexicron.ui.game.GameController;
import com.example.lexicron.ui.results.ResultsController;
import com.example.lexicron.ui.welcome.WelcomeController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Punto de entrada gráfico de Lexicron.
 * Gestiona la navegación entre las pantallas de bienvenida, juego y resultados.
 */
public class LexicronJavaFxApp extends Application {

    private static final String WELCOME_VIEW = "/com/example/lexicron/ui/welcome/WelcomeView.fxml";
    private static final String GAME_VIEW = "/com/example/lexicron/ui/game/GameView.fxml";
    private static final String RESULTS_VIEW = "/com/example/lexicron/ui/results/ResultsView.fxml";

    private static final String WELCOME_STYLESHEET = "/com/example/lexicron/ui/welcome/welcome.css";
    private static final String GAME_STYLESHEET = "/com/example/lexicron/ui/game/game.css";
    private static final String RESULTS_STYLESHEET = "/com/example/lexicron/ui/results/results.css";

    private final DataLoader dataLoader = new DataLoader();
    private final FrenchFormatter frenchFormatter = new FrenchFormatter();
    private final SpanishSentenceBuilder sentenceBuilder = new SpanishSentenceBuilder();
    private final RoundValidator validator = new RoundValidator();
    private final ScoreCalculator scoreCalculator = new ScoreCalculator();
    private final ScoreSaver scoreSaver = new ScoreSaver();

    private Stage primaryStage;
    private GameSession gameSession;
    private WordBankBuilder wordBankBuilder;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Lexicron");
        primaryStage.setMinWidth(380);
        primaryStage.setMinHeight(640);
        primaryStage.setWidth(420);
        primaryStage.setHeight(800);

        initializeGameDependencies();
        showWelcomeScene();
        primaryStage.show();
    }

    private void initializeGameDependencies() {
        List<Subject> subjects = dataLoader.loadSubjects();
        List<Verb> verbs = dataLoader.loadVerbs();
        List<Complement> complements = dataLoader.loadComplements();

        RoundGenerator generator = new RoundGenerator(subjects, verbs, complements);
        this.gameSession = new GameSession(generator, sentenceBuilder, validator, scoreCalculator, scoreSaver);
        this.wordBankBuilder = new WordBankBuilder(frenchFormatter);
    }

    /**
     * Muestra la escena de bienvenida.
     */
    private void showWelcomeScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(WELCOME_VIEW));
            Parent root = loader.load();

            WelcomeController controller = loader.getController();
            controller.setOnStartGame(this::showGameScene);
            controller.setHostServices(getHostServices());

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource(WELCOME_STYLESHEET).toExternalForm());

            primaryStage.setScene(scene);
        } catch (IOException e) {
            throw new LexicronException("No se pudo cargar la pantalla de bienvenida", e);
        }
    }

    /**
     * Muestra la escena del juego.
     */
    private void showGameScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(GAME_VIEW));
            Parent root = loader.load();

            GameController controller = loader.getController();
            controller.initializeGame(gameSession, wordBankBuilder, frenchFormatter, this::showResultsScene);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource(GAME_STYLESHEET).toExternalForm());

            primaryStage.setScene(scene);
        } catch (IOException e) {
            throw new LexicronException("No se pudo cargar la pantalla del juego", e);
        }
    }

    /**
     * Muestra la escena de resultados finales.
     */
    private void showResultsScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RESULTS_VIEW));
            Parent root = loader.load();

            ResultsController controller = loader.getController();
            controller.setScore(gameSession.getTotalScore(), gameSession.getTotalRounds() * 100);
            controller.setOnRestart(this::showGameScene);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource(RESULTS_STYLESHEET).toExternalForm());

            primaryStage.setScene(scene);
        } catch (IOException e) {
            throw new LexicronException("No se pudo cargar la pantalla de resultados", e);
        }
    }

    /**
     * Lanza la aplicación JavaFX.
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        launch(args);
    }
}
