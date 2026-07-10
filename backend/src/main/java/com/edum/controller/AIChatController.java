package com.edum.controller;

import com.edum.common.Result;
import com.edum.service.RagService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AIChatController {

    @Autowired
    private ChatClient.Builder chatClientBuilder;

    @Autowired
    private RagService ragService;

    @GetMapping("/chat")
    public Result<String> chat(@RequestParam String message) {
        try {
            // Build RAG prompt with knowledge context
            String prompt = ragService.buildPrompt(message);

            ChatClient chatClient = chatClientBuilder.build();
            String response = chatClient.prompt(prompt).call().content();
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("AI service unavailable: " + e.getMessage());
        }
    }
}
