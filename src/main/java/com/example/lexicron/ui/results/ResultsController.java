package com.example.lexicron.ui.results;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controlador de la pantalla de resultados finales.
 * Muestra el puntaje obtenido, un mensaje de agradecimiento, los créditos
 * y un botón para reiniciar la partida.
 */
public class ResultsController {

    @FXML private Label scoreValueLabel;
    @FXML private Label percentageLabel;

    private Runnable onRestart;

    /**
     * Registra la acción a ejecutar cuando el usuario pulsa "Jugar de nuevo".
     *
     * @param onRestart callback que reinicia el juego
     */
    public void setOnRestart(Runnable onRestart) {
        this.onRestart = onRestart;
    }

    /**
     * Establece el puntaje final a mostrar.
     *
     * @param score    puntaje obtenido
     * @param maxScore puntaje máximo posible
     */
    public void setScore(int score, int maxScore) {
        scoreValueLabel.setText(score + "/" + maxScore);
        double percentage = maxScore > 0 ? (double) score / maxScore * 100 : 0;
        percentageLabel.setText(String.format("%.1f%%", percentage));
    }

    /**
     * Reinicia el juego cuando el usuario pulsa el botón correspondiente.
     */
    @FXML
    private void handleRestart() {
        if (onRestart != null) {
            onRestart.run();
        }
    }
}
