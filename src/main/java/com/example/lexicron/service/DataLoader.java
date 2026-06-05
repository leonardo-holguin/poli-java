package com.example.lexicron.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.example.lexicron.model.Subject;
import com.example.lexicron.model.Verb;
import com.example.lexicron.model.Complement;
import com.example.lexicron.exception.LexicronException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Carga los datos del juego desde archivos JSON ubicados en el classpath.
 * Utiliza Jackson ObjectMapper para deserializar los archivos en listas de
 * objetos del modelo.
 */
public class DataLoader {

    private final ObjectMapper mapper;

    public DataLoader() {
        this.mapper = new ObjectMapper();
    }

    /**
     * @return lista de sujetos cargada desde subject.json
     */
    public List<Subject> loadSubjects() {
        return loadList("subject.json", new TypeReference<List<Subject>>() {});
    }

    /**
     * @return lista de verbos cargada desde verb.json
     */
    public List<Verb> loadVerbs() {
        return loadList("verb.json", new TypeReference<List<Verb>>() {});
    }

    /**
     * @return lista de complementos cargada desde complement.json
     */
    public List<Complement> loadComplements() {
        return loadList("complement.json", new TypeReference<List<Complement>>() {});
    }

    private <T> List<T> loadList(String resourceName, TypeReference<List<T>> typeRef) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName);
        if (is == null) {
            throw new LexicronException("Resource not found: " + resourceName);
        }
        try {
            return mapper.readValue(is, typeRef);
        } catch (IOException e) {
            throw new LexicronException("Failed to load " + resourceName, e);
        }
    }
}
