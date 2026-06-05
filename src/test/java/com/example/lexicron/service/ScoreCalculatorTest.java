package com.example.lexicron.service;

import org.junit.jupiter.api.Test;

import com.example.lexicron.model.ValidationResult;

import static org.junit.jupiter.api.Assertions.*;

class ScoreCalculatorTest {

    private final ScoreCalculator calculator = new ScoreCalculator();

    @Test
    void calculatePoints_threeCorrect_returns100() {
        ValidationResult result = new ValidationResult(true, true, true);
        assertEquals(100, calculator.calculatePoints(result));
    }

    @Test
    void calculatePoints_twoCorrect_returns70() {
        assertEquals(70, calculator.calculatePoints(new ValidationResult(true, true, false)));
        assertEquals(70, calculator.calculatePoints(new ValidationResult(true, false, true)));
        assertEquals(70, calculator.calculatePoints(new ValidationResult(false, true, true)));
    }

    @Test
    void calculatePoints_oneCorrect_returns40() {
        assertEquals(40, calculator.calculatePoints(new ValidationResult(true, false, false)));
        assertEquals(40, calculator.calculatePoints(new ValidationResult(false, true, false)));
        assertEquals(40, calculator.calculatePoints(new ValidationResult(false, false, true)));
    }

    @Test
    void calculatePoints_noneCorrect_returns0() {
        ValidationResult result = new ValidationResult(false, false, false);
        assertEquals(0, calculator.calculatePoints(result));
    }
}
