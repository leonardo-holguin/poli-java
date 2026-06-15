package com.example.lexicron.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.example.lexicron.model.GameResult;

/**
 * Guarda el resultado final de una partida en un archivo de puntuaciones.
 */
public class ScoreSaver {

    private static final Path DEFAULT_SCORES_FILE = Paths.get("scores.txt");
    private static final int TOTAL_ROUNDS = 5;

    private final Path scoresFile;

    /**
     * Crea un guardador de puntajes que escribe en {@code scores.txt}.
     */
    public ScoreSaver() {
        this(DEFAULT_SCORES_FILE);
    }

    /**
     * @param scoresFile ruta del archivo donde se guardarán los puntajes
     */
    public ScoreSaver(Path scoresFile) {
        this.scoresFile = scoresFile;
    }

    /**
     * Guarda el resultado de la partida en el archivo de puntuaciones.
     *
     * @param gameResult resultado de la partida
     */
    public void saveScore(GameResult gameResult) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String line = timestamp + " | Puntaje: " + gameResult.totalScore() + "/" + (TOTAL_ROUNDS * 100) + "\n";
            Files.writeString(scoresFile, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("No se pudo guardar el puntaje: " + e.getMessage());
        }
    }
}
