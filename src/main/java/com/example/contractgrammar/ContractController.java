package com.example.contractgrammar;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ContractController {
    private final GrammarEngine engine;

    public ContractController(GrammarEngine engine) {
        this.engine = engine;
    }

    @PostMapping("/validate")
    public GrammarEngine.ValidationResult validate(@RequestBody ValidationRequest request) {
        if (request == null || request.text() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "text is required");
        }
        return engine.validate(request.text());
    }

    @GetMapping("/grammar/versions")
    public List<GrammarEngine.GrammarVersion> versions() {
        return engine.history();
    }

    @PutMapping("/grammar")
    public GrammarEngine.GrammarVersion update(@RequestBody GrammarUpdate request) {
        if (request == null || request.productions() == null || request.productions().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productions are required");
        }
        return engine.update(request.productions(), request.reason());
    }

    public record ValidationRequest(String text) {}
    public record GrammarUpdate(Map<String, List<List<String>>> productions, String reason) {}
}
