package com.example.lexicron;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.example.lexicron.model.Complement;
import com.example.lexicron.model.GameResult;
import com.example.lexicron.model.Round;
import com.example.lexicron.model.RoundOptions;
import com.example.lexicron.model.RoundResult;
import com.example.lexicron.model.Subject;
import com.example.lexicron.model.ValidationResult;
import com.example.lexicron.model.Verb;
import com.example.lexicron.model.VerbOption;
import com.example.lexicron.service.DataLoader;
import com.example.lexicron.service.FrenchFormatter;
import com.example.lexicron.service.RoundGenerator;
import com.example.lexicron.service.RoundValidator;
import com.example.lexicron.service.ScoreCalculator;
import com.example.lexicron.service.ScoreSaver;
import com.example.lexicron.service.SpanishSentenceBuilder;

/**
 * Punto de entrada del juego Lexicron.
 * Presenta 5 rondas donde el usuario debe traducir frases del español al francés
 * seleccionando sujeto, verbo y complemento. Al final muestra el puntaje total
 * y lo guarda en un archivo.
 *
 * @author Alexander David Gualdron Chaparro
 * @author Daniel Grande Gordillo
 * @author Laura Sofia Gutiérrez Rico
 * @author Leonardo Holguin Arias
 * @author Sandra Milena Gutiérrez Madrigal
 */
public class LexicronApp {

    private static final int TOTAL_ROUNDS = 5;

    public static void main(String[] args) {
        DataLoader loader = new DataLoader();

        List<Subject> subjects = loader.loadSubjects();
        List<Verb> verbs = loader.loadVerbs();
        List<Complement> complements = loader.loadComplements();

        RoundGenerator generator = new RoundGenerator(subjects, verbs, complements);
        SpanishSentenceBuilder sentenceBuilder = new SpanishSentenceBuilder();
        RoundValidator validator = new RoundValidator();
        ScoreCalculator scoreCalculator = new ScoreCalculator();
        FrenchFormatter frenchFormatter = new FrenchFormatter();
        ScoreSaver scoreSaver = new ScoreSaver();

        List<RoundResult> roundResults = new ArrayList<>();

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("=== LEXICRON ===");
            System.out.println("Arma la frase en francés a partir de la traducción al español.");
            System.out.println("Tienes " + TOTAL_ROUNDS + " rondas. ¡Suerte!\n");

            for (int roundNumber = 1; roundNumber <= TOTAL_ROUNDS; roundNumber++) {
                System.out.println("--- Ronda " + roundNumber + " de " + TOTAL_ROUNDS + " ---\n");

                Round round = generator.generateRound();
                RoundOptions options = generator.generateOptions(round);

                String spanishSentence = sentenceBuilder.buildSentence(round);
                System.out.println("Traduce al francés: " + spanishSentence + "\n");

                System.out.println("Sujetos:");
                printNumbered(options.subjectOptions());
                int subjectChoice = readChoice(scanner, 1, options.subjectOptions().size());
                Subject chosenSubject = options.subjectOptions().get(subjectChoice - 1);

                System.out.println("\nVerbos:");
                List<String> verbDisplay = options.verbOptions().stream()
                    .map(frenchFormatter::displayVerbOption)
                    .toList();
                printNumbered(verbDisplay);
                int verbChoice = readChoice(scanner, 1, options.verbOptions().size());
                VerbOption chosenVerbOption = options.verbOptions().get(verbChoice - 1);
                Verb chosenVerb = chosenVerbOption.verb();

                System.out.println("\nComplementos:");
                printNumbered(options.complementOptions());
                int complementChoice = readChoice(scanner, 1, options.complementOptions().size());
                Complement chosenComplement = options.complementOptions().get(complementChoice - 1);

                ValidationResult result = validator.validate(round, chosenSubject, chosenVerb, chosenComplement);
                int points = scoreCalculator.calculatePoints(result);

                roundResults.add(new RoundResult(round, result, points));

                System.out.println("\n--- Resultado ronda " + roundNumber + " ---");
                System.out.println("Sujeto: " + (result.subjectCorrect() ? "✓" : "✗") + " (correcto: " + round.subject().fr() + ")");
                System.out.println("Verbo:  " + (result.verbCorrect() ? "✓" : "✗") + " (correcto: " + frenchFormatter.getFrenchConjugation(round.verb(), round.subject()) + ")");
                System.out.println("Comp.:  " + (result.complementCorrect() ? "✓" : "✗") + " (correcto: " + round.complement().fr() + ")");
                System.out.println("Puntos: " + points);

                if (!result.allCorrect()) {
                    System.out.println("Frase correcta: " + frenchFormatter.buildCorrectSentence(round));
                }

                System.out.println();
            }

            int totalScore = roundResults.stream().mapToInt(RoundResult::points).sum();
            GameResult gameResult = new GameResult(roundResults, totalScore);

            System.out.println("=== FIN DE LA PARTIDA ===");
            System.out.println("Puntaje final: " + totalScore + " / " + (TOTAL_ROUNDS * 100));
            int perfectScore = TOTAL_ROUNDS * 100;
            double percentage = (double) totalScore / perfectScore * 100;
            System.out.printf("Porcentaje: %.1f%%\n", percentage);
            System.out.println("\nDesglose por ronda:");
            for (int i = 0; i < gameResult.rounds().size(); i++) {
                RoundResult rr = gameResult.rounds().get(i);
                int correctos = 0;
                if (rr.validationResult().subjectCorrect()) correctos++;
                if (rr.validationResult().verbCorrect()) correctos++;
                if (rr.validationResult().complementCorrect()) correctos++;
                System.out.println("  Ronda " + (i + 1) + ": " + correctos + "/3 aciertos — " + rr.points() + " pts");
            }

            scoreSaver.saveScore(gameResult);
            System.out.println("Puntaje guardado en: scores.txt");

            System.out.println("\n¡Gracias por jugar Lexicron!");
        }
    }

    /**
     * Muestra una lista numerada de elementos para que el usuario elija.
     *
     * @param <T>   tipo de los elementos
     * @param items lista de elementos a mostrar
     */
    private static <T> void printNumbered(List<T> items) {
        for (int i = 0; i < items.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + items.get(i));
        }
    }

    /**
     * Lee una opción numérica del usuario validando que esté dentro del rango.
     *
     * @param scanner el Scanner para leer la entrada
     * @param min     valor mínimo aceptado
     * @param max     valor máximo aceptado
     * @return la opción elegida dentro del rango [min, max]
     */
    private static int readChoice(Scanner scanner, int min, int max) {
        while (true) {
            System.out.print("Elige (1-" + max + "): ");
            String input = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(input);
                if (choice >= min && choice <= max) {
                    return choice;
                }
            } catch (NumberFormatException e) {
                // entrada no numérica, se ignora y se reintenta
            }
            System.out.println("Opción inválida. Intenta de nuevo.");
        }
    }
}
