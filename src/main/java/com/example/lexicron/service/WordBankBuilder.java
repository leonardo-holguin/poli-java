package com.example.lexicron.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.example.lexicron.model.Complement;
import com.example.lexicron.model.RoundOptions;
import com.example.lexicron.model.Subject;
import com.example.lexicron.model.VerbOption;
import com.example.lexicron.model.WordCategory;
import com.example.lexicron.model.WordChip;

/**
 * Construye el banco de palabras a partir de las opciones de una ronda.
 * Genera 12 chips (4 sujetos + 4 verbos + 4 complementos) mezclados en orden aleatorio.
 */
public class WordBankBuilder {

    private final FrenchFormatter frenchFormatter;
    private final Random random;

    /**
     * @param frenchFormatter formateador de textos en francés
     */
    public WordBankBuilder(FrenchFormatter frenchFormatter) {
        this(frenchFormatter, new Random());
    }

    /**
     * @param frenchFormatter formateador de textos en francés
     * @param random          generador de números aleatorios para mezclar
     */
    public WordBankBuilder(FrenchFormatter frenchFormatter, Random random) {
        this.frenchFormatter = frenchFormatter;
        this.random = random;
    }

    /**
     * Genera una lista mezclada de chips a partir de las opciones de la ronda.
     *
     * @param options opciones generadas por {@link RoundGenerator}
     * @return lista de 12 chips en orden aleatorio
     */
    public List<WordChip> buildWordBank(RoundOptions options) {
        List<WordChip> chips = new ArrayList<>();

        for (Subject subject : options.subjectOptions()) {
            chips.add(new WordChip(subject.fr(), WordCategory.SUBJECT, subject));
        }

        for (VerbOption verbOption : options.verbOptions()) {
            String text = frenchFormatter.displayVerbOption(verbOption);
            chips.add(new WordChip(text, WordCategory.VERB, verbOption.verb()));
        }

        for (Complement complement : options.complementOptions()) {
            chips.add(new WordChip(complement.fr(), WordCategory.COMPLEMENT, complement));
        }

        Collections.shuffle(chips, random);
        return chips;
    }
}
