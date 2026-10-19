package com.example.contractgrammar;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GrammarEngineTest {
    @Test
    void acceptsAClauseCoveredByTheGrammar() {
        GrammarEngine engine = new GrammarEngine();
        assertTrue(engine.validate("the provider shall deliver the service").accepted());
    }

    @Test
    void rejectsAClauseOutsideTheGrammar() {
        GrammarEngine engine = new GrammarEngine();
        assertFalse(engine.validate("the provider may deliver the service").accepted());
    }

    @Test
    void recordsGrammarUpdatesAsVersions() {
        GrammarEngine engine = new GrammarEngine();
        int original = engine.currentVersion().version();
        engine.update(java.util.Map.of("Clause", java.util.List.of(
                java.util.List.of("the", "provider", "shall", "deliver", "the", "service"))),
                "Updated local rule");
        assertEquals(original + 1, engine.currentVersion().version());
        assertEquals(2, engine.history().size());
    }
}
