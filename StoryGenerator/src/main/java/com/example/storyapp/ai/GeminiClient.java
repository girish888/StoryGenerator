package com.example.storyapp.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
public class GeminiClient {

    private static final Logger log = LoggerFactory.getLogger(GeminiClient.class);

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String endpointBase;
    private final String apiKey;              // may be empty if not configured
    private final boolean enabled;            // true only if key present

    public GeminiClient(WebClient.Builder webClientBuilder,
                        ObjectMapper objectMapper,
                        @Value("${ai.gemini.key:}") String key,
                        @Value("${ai.gemini.model:gemini-2.0-flash}") String model) {

        this.objectMapper = objectMapper;
        this.webClient = webClientBuilder.build();

        boolean hasKey = (key != null && !key.isBlank() && !key.trim().startsWith("${"));
        this.enabled = hasKey;
        this.apiKey = hasKey ? key.trim() : "";

        this.endpointBase = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent";

        if (this.enabled) {
            log.info("GeminiClient enabled for model={} endpoint={}", model, endpointBase);
        } else {
            log.warn("GeminiClient disabled - no API key configured. AI calls will return friendly errors.");
        }
    }

    /**
     * Generate text from Gemini (or return friendly message when disabled).
     *
     * @param prompt    user prompt
     * @param maxTokens max tokens to request
     * @return Mono with generated text or friendly error description
     */
    public Mono<String> generate(String prompt, int maxTokens) {
        if (!enabled) {
            String msg = """
                    AI key is not configured. To enable AI features:
                    1) Create an API key in Google AI Studio: https://aistudio.google.com/app/apikey
                    2) Enable the Generative Language API in your GCP Console
                    3) Ensure billing is enabled on the project
                    4) Set environment variable AI_GEMINI_KEY or system property ai.gemini.key at runtime
                    After setting the key, restart the application.
                    """;
            return Mono.just(msg);
        }

        var part = Map.<String, Object>of("text", prompt);
        var content = Map.<String, Object>of("parts", List.of(part));
        var config = Map.<String, Object>of("maxOutputTokens", maxTokens);
        var payload = Map.<String, Object>of(
                "contents", List.of(content),
                "generationConfig", config
        );

        String urlWithKey = endpointBase + "?key=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8);

        return webClient.post()
                .uri(urlWithKey)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::extractTextFromGeminiResponse)
                .onErrorResume(throwable -> {
                    if (throwable instanceof WebClientResponseException wex) {
                        String body = safeResponseBody(wex);
                        log.warn("AI returned error: status={}, body={}", wex.getStatusCode(), body);
                        return Mono.just("AI Error " + wex.getStatusCode() + ": " + body);
                    }
                    Throwable cause = throwable.getCause() != null ? throwable.getCause() : throwable;
                    if (cause instanceof UnknownHostException) {
                        log.warn("Network/DNS problem when calling Gemini", cause);
                        return Mono.just("Error: No internet connection or DNS failure.");
                    }
                    log.error("Unexpected error calling Gemini", throwable);
                    return Mono.just("Error: " + throwable.getMessage());
                });
    }

    private String safeResponseBody(WebClientResponseException ex) {
        try {
            return ex.getResponseBodyAsString();
        } catch (Exception e) {
            return "(unable to read response body)";
        }
    }

    private String extractTextFromGeminiResponse(String jsonBody) {
        try {
            JsonNode root = objectMapper.readTree(jsonBody);

            // common response shapes
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode firstCandidate = candidates.get(0);
                JsonNode parts = firstCandidate.path("content").path("parts");
                if (parts.isArray() && parts.size() > 0) {
                    JsonNode firstPart = parts.get(0);
                    if (firstPart.has("text")) {
                        return firstPart.path("text").asText();
                    } else {
                        return firstPart.toString();
                    }
                }
            }

            if (root.has("output")) {
                return root.path("output").toString();
            }
            if (root.has("outputs")) {
                return root.path("outputs").toString();
            }

            log.debug("Unexpected Gemini response shape: {}", jsonBody);
            return "AI returned no text. Raw response: " + jsonBody;
        } catch (Exception e) {
            log.error("Failed parsing Gemini response", e);
            return "Error parsing story: " + e.getMessage();
        }
    }
}
