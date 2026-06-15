package com.example.lexicron.model;

/**
 * Representa una palabra mostrada como chip en el banco de palabras del juego.
 *
 * @param text     texto mostrado en el chip
 * @param category categoría gramatical (sujeto, verbo o complemento)
 * @param source   objeto del modelo subyacente ({@link Subject}, {@link Verb} o {@link Complement})
 */
public record WordChip(
    String text,
    WordCategory category,
    Object source
) {}
