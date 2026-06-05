package com.example.lexicron.model;

/**
 * Asocia un verbo con el sujeto con el que debe mostrarse conjugado
 * en las opciones del juego.
 *
 * @param verb          el verbo en infinitivo
 * @param conjugatedFor el sujeto para el cual se conjuga el verbo
 */
public record VerbOption(Verb verb, Subject conjugatedFor) {}
