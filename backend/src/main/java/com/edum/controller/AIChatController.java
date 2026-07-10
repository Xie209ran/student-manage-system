package com.edum.controller;

import com.edum.common.Result;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AIChatController {

    @Autowired
    private ChatClient.Builder chatClientBuilder;

    @GetMapping("/chat")
    public Result<String> chat(@RequestParam String message) {
        try {
            ChatClient chatClient = chatClientBuilder.build();
            String response = chatClient.prompt(message).call().content();
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("AI服务暂不可用: " + e.getMessage());
        }
    }
}
