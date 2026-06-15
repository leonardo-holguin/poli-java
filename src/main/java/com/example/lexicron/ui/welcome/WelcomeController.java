package com.example.lexicron.ui.welcome;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

/**
 * Controlador de la pantalla de bienvenida.
 * Maneja la navegación hacia el juego y la apertura del tutorial en video.
 */
public class WelcomeController {

    private static final String TUTORIAL_PATH = "videos/tutorial.mp4";

    private Runnable onStartGame;
    private HostServices hostServices;

    /**
     * Registra la acción a ejecutar cuando el usuario pulsa "Empezar a jugar".
     *
     * @param onStartGame callback que inicia el juego
     */
    public void setOnStartGame(Runnable onStartGame) {
        this.onStartGame = onStartGame;
    }

    /**
     * Inyecta los servicios del host para abrir archivos externos.
     *
     * @param hostServices servicios del host de JavaFX
     */
    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    /**
     * Inicia el juego cuando el usuario pulsa el botón principal.
     */
    @FXML
    private void handleStartGame() {
        if (onStartGame != null) {
            onStartGame.run();
        }
    }

    /**
     * Abre el video tutorial con el reproductor predeterminado del sistema.
     */
    @FXML
    private void handleOpenTutorial() {
        Path tutorialPath = Paths.get(TUTORIAL_PATH);
        File tutorialFile = tutorialPath.toFile();

        if (!tutorialFile.exists()) {
            showError("No se encontró el tutorial", "El archivo " + TUTORIAL_PATH + " no existe.");
            return;
        }

        try {
            String uri = tutorialFile.toURI().toString();
            if (hostServices != null) {
                hostServices.showDocument(uri);
            } else {
                java.awt.Desktop.getDesktop().open(tutorialFile);
            }
        } catch (Exception e) {
            showError("No se pudo abrir el tutorial", e.getMessage());
        }
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
