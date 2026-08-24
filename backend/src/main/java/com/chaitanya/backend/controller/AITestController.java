package com.chaitanya.backend.controller;
import org.springframework.web.bind.annotation.*;

import com.chaitanya.backend.service.LLMClient;

import lombok.*;

@RestController
@RequestMapping("/ai-test")
@RequiredArgsConstructor
public class AITestController {

    private final LLMClient llmClient;

    @GetMapping
    public String test() {
        return llmClient.chat("Reply with exactly: AI WORKING");
    }
}