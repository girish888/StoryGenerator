package com.example.storyapp;

import com.example.storyapp.ai.GeminiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class StoryService {

    private static final Logger log = LoggerFactory.getLogger(StoryService.class);

    private final GeminiClient geminiClient;

    public StoryService(GeminiClient geminiClient) {
        this.geminiClient = geminiClient;
    }

    /**
     * Generate a story synchronously (blocking for up to 10 seconds).
     * If GeminiClient is disabled it will return a friendly message.
     */
    public String generate(String words, String genre) {
        String prompt = buildPrompt(words, genre);
        try {
            Mono<String> mono = geminiClient.generate(prompt, 400);
            // block with timeout so we don't hang the web thread forever
            String result = mono.block(Duration.ofSeconds(10));
            return result != null ? result : "AI returned no content.";
        } catch (Exception e) {
            log.error("Story generation failed", e);
            return "Story generation failed: " + e.getMessage();
        }
    }

    private String buildPrompt(String words, String genre) {
        StringBuilder sb = new StringBuilder();
        sb.append("Write a creative short story using these words: ");
        sb.append(words == null ? "" : words);
        if (genre != null && !genre.isBlank()) {
            sb.append(". Genre: ").append(genre);
        }
        sb.append(". Keep it readable and engaging.");
        return sb.toString();
    }
}
