package com.example.lexicron.service;

import com.example.lexicron.model.Round;
import com.example.lexicron.model.Subject;
import com.example.lexicron.model.Verb;
import com.example.lexicron.model.Complement;
import com.example.lexicron.model.ValidationResult;

/**
 * Valida la respuesta del usuario comparando su selección con la ronda correcta.
 * Cada elemento (sujeto, verbo, complemento) se evalúa por separado.
 */
public class RoundValidator {

    /**
     * Compara los elementos elegidos por el usuario contra los correctos.
     *
     * @param correctRound       la ronda con la combinación correcta
     * @param chosenSubject      el sujeto que eligió el usuario
     * @param chosenVerb         el verbo que eligió el usuario
     * @param chosenComplement   el complemento que eligió el usuario
     * @return un {@link ValidationResult} indicando qué elementos fueron correctos
     */
    public ValidationResult validate(Round correctRound, Subject chosenSubject, Verb chosenVerb, Complement chosenComplement) {
        boolean subjectOk = correctRound.subject().equals(chosenSubject);
        boolean verbOk = correctRound.verb().equals(chosenVerb);
        boolean complementOk = correctRound.complement().equals(chosenComplement);
        return new ValidationResult(subjectOk, verbOk, complementOk);
    }
}
