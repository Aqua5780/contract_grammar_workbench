package com.example.contractgrammar;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class GrammarEngine {
    private final Map<String, List<List<String>>> productions = new LinkedHashMap<>();
    private final List<GrammarVersion> versions = new ArrayList<>();
    private int versionNumber;

    public GrammarEngine() {
        replaceGrammar(Map.of(
                "Clause", List.of(List.of("Party", "shall", "Action", "Object")),
                "Party", List.of(List.of("the", "provider"), List.of("the", "client")),
                "Action", List.of(List.of("deliver"), List.of("pay"), List.of("maintain")),
                "Object", List.of(List.of("the", "service"), List.of("the", "fee"),
                        List.of("confidentiality"))
        ), "Initial contract grammar");
    }

    public synchronized ValidationResult validate(String text) {
        List<String> tokens = tokenize(text);
        List<String> expected = new ArrayList<>();
        boolean accepted = match("Clause", tokens, 0, expected) == tokens.size();
        if (accepted) {
            return new ValidationResult(true, "Clause accepted by grammar.", List.of(),
                    versionNumber);
        }
        String found = expected.isEmpty() ? "end of input" : expected.get(0);
        String actual = tokens.isEmpty() ? "empty input"
                : "near token " + Math.min(tokens.size(), expected.size() + 1);
        return new ValidationResult(false,
                "Clause rejected: expected " + found + " (" + actual + ").",
                List.of("This is a grammar diagnostic, not a legal opinion."),
                versionNumber);
    }

    public synchronized List<GrammarVersion> history() {
        return List.copyOf(versions);
    }

    public synchronized GrammarVersion currentVersion() {
        return versions.get(versions.size() - 1);
    }

    public synchronized GrammarVersion update(Map<String, List<List<String>>> grammar, String reason) {
        replaceGrammar(grammar, reason == null || reason.isBlank() ? "Manual update" : reason);
        return currentVersion();
    }

    private void replaceGrammar(Map<String, List<List<String>>> grammar, String reason) {
        productions.clear();
        productions.putAll(grammar);
        versionNumber++;
        versions.add(new GrammarVersion(versionNumber, reason, Map.copyOf(productions)));
    }

    private int match(String symbol, List<String> tokens, int position, List<String> expected) {
        List<List<String>> choices = productions.get(symbol);
        if (choices == null) {
            if (position < tokens.size() && tokens.get(position).equals(symbol)) {
                return position + 1;
            }
            expected.add(symbol);
            return -1;
        }
        for (List<String> choice : choices) {
            int current = position;
            boolean successful = true;
            for (String item : choice) {
                current = match(item, tokens, current, expected);
                if (current < 0) {
                    successful = false;
                    break;
                }
            }
            if (successful) {
                return current;
            }
        }
        return -1;
    }

    private List<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return List.of(text.trim().toLowerCase().split("\\s+"));
    }

    public record ValidationResult(boolean accepted, String message,
                                   List<String> diagnostics, int grammarVersion) {}

    public record GrammarVersion(int version, String reason,
                                 Map<String, List<List<String>>> productions) {}
}
