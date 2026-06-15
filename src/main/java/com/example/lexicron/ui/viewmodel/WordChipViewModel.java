package com.example.lexicron.ui.viewmodel;

import com.example.lexicron.model.WordChip;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * ViewModel observable para un chip del banco de palabras.
 */
public class WordChipViewModel {

    private final WordChip wordChip;
    private final ReadOnlyStringWrapper text;
    private final BooleanProperty placed = new SimpleBooleanProperty(false);

    /**
     * @param wordChip chip del modelo
     */
    public WordChipViewModel(WordChip wordChip) {
        this.wordChip = wordChip;
        this.text = new ReadOnlyStringWrapper(wordChip.text());
    }

    /**
     * @return el chip del modelo
     */
    public WordChip getWordChip() {
        return wordChip;
    }

    /**
     * @return texto mostrado en el chip
     */
    public String getText() {
        return text.get();
    }

    /**
     * @return propiedad de solo lectura del texto
     */
    public ReadOnlyStringProperty textProperty() {
        return text.getReadOnlyProperty();
    }

    /**
     * @return true si el chip ya fue colocado en una casilla
     */
    public boolean isPlaced() {
        return placed.get();
    }

    /**
     * @param placed true si el chip ya fue colocado en una casilla
     */
    public void setPlaced(boolean placed) {
        this.placed.set(placed);
    }

    /**
     * @return propiedad observable de colocación
     */
    public BooleanProperty placedProperty() {
        return placed;
    }
}
