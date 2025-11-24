package com.example.storyapp.service;

import com.example.storyapp.model.StoryRequest;
import com.example.storyapp.repository.StoryRequestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class storyGenerationWorker {

    private final StoryRequestRepository repository;
    private final ObjectMapper objectMapper;
    private final OkHttpClient httpClient;

    public storyGenerationWorker(StoryRequestRepository repository,
                                 ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.httpClient = new OkHttpClient.Builder()
                .callTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    /**
     * This method runs asynchronously and updates the DB when finished.
     */
    @Async
    public void processStoryGenerationAsync(Long requestId) {
        Optional<StoryRequest> opt = repository.findById(requestId);
        if (opt.isEmpty()) {
            return;
        }

        StoryRequest req = opt.get();
        try {
            // perform external call (replace with your real API)
            String story = callExternalApiAndComposeStory(req.getWords(), req.getGenre());

            req.setStory(story);
            req.setStatus(StoryRequest.RequestStatus.DONE);
            req.setErrorMessage(null);
            req.setUpdatedAt(Instant.now());
        } catch (Exception ex) {
            req.setStatus(StoryRequest.RequestStatus.ERROR);
            req.setErrorMessage(ex.getMessage());
            req.setUpdatedAt(Instant.now());
        } finally {
            repository.save(req);
        }
    }

    /**
     * Simple wrapper for the external AI HTTP call — adapt to your API.
     */
    private String callExternalApiAndComposeStory(String words, String genre) throws IOException {
        // TODO: build the real request (headers, method, body) for your AI provider
        String url = "https://example-ai-api/generate?words=" + urlEncode(words) + "&genre=" + urlEncode(genre);
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("AI API error: " + response.code() + " - " + response.message());
            }
            if (response.body() == null) {
                throw new IOException("AI API returned empty body");
            }
            String body = response.body().string();

            // try to parse JSON { "story": "..." } else return raw body
            try {
                var node = objectMapper.readTree(body);
                if (node.has("story")) {
                    return node.get("story").asText();
                } else {
                    return body;
                }
            } catch (Exception e) {
                return body;
            }
        }
    }

    private static String urlEncode(String s) {
        if (s == null) return "";
        return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8);
    }
}
