package com.example.storyapp.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;

@Component
public class GeminiRestClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String generateUrl;

    public GeminiRestClient(@Value("${ai.gemini.model:gemini-1.5-flash}") String model,
                            @Value("${ai.gemini.key}") String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalStateException("AI_GEMINI_KEY is not set.");
        }
        // HARDCODED URL HERE
        this.generateUrl = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + key;
    }

    public String generate(String prompt, int maxTokens) {
        var body = java.util.Map.of(
                "prompt", java.util.Map.of("text", prompt),
                "maxOutputTokens", maxTokens
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<String> resp = restTemplate.postForEntity(generateUrl, entity, String.class);
            return resp.getBody();
        } catch (ResourceAccessException ex) {
            // often wraps UnknownHostException
            return "{\"error\":\"Upstream AI host not found\"}";
        } catch (Exception ex) {
            return "{\"error\":\"Upstream AI error\"}";
        }
    }
}
