# Lexicron — AGENTS.md

## Commands

```powershell
mvn test                    # all tests
mvn compile                 # compile only
mvn exec:java -Dexec.mainClass=com.example.lexicron.LexicronApp   # run console game
```

## Project structure

- **Java 24**, Maven single-module, no Lombok
- **src/main/resources/** — JSON data files (`subject.json`, `verb.json`, `complement.json`)
- **`com.example.lexicron.LexicronApp`** — entry point (console game, 5 rounds with scoring)
- **`com.example.lexicron.model`** — records only (`Subject`, `Verb`, `Conjugation`, `Complement`, `Round`, `RoundOptions`, `ValidationResult`)
- **`com.example.lexicron.service`** — `DataLoader` (Jackson), `RoundGenerator`, `SpanishSentenceBuilder`, `RoundValidator`, `ScoreCalculator`
- **`com.example.lexicron.exception`** — `LexicronException` (unchecked)

## Key conventions

- All Javadoc in **Spanish**; only programming terms stay in English
- JSON field names use `@JsonProperty` on record components (snake_case in JSON, camelCase in Java)
- `j'` subject key in JSON for vowel-starting conjugations; value is `null` when inapplicable
- `verb.json` conjugations stored as `Map<String, Conjugation>` with nullable values
- `complement.json` links to verbs via `acceptedVerbs` list; `RoundGenerator` filters by it

## Testing

- JUnit Jupiter 5.11 (no Mockito, no integration tests)
- Tests create model instances directly (no JSON loading in tests)
- Test classes mirror source package: `src/test/java/com/example/lexicron/service/`
- Run: `mvn test` (Surefire auto-detects JUnit 5)

## Data flow

```
DataLoader → List<Subject/Verb/Complement>
  → RoundGenerator.generateRound() → Round (correct answer)
  → RoundGenerator.generateOptions(round) → RoundOptions (4 shuffled options/category)
  → SpanishSentenceBuilder.buildSentence(round) → "Ella mira una película."
  → RoundValidator.validate(round, userChoice) → ValidationResult
  → ScoreCalculator.calculatePoints(validationResult) → int (0, 40, 70, 100)
```
