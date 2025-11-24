package com.example.storyapp.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "story_request")
public class StoryRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // the words/seed the user provided
    @Column(nullable = false, columnDefinition = "TEXT")
    private String words;

    // optional genre
    private String genre;

    // generated story
    @Column(columnDefinition = "TEXT")
    private String story;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private Instant createdAt;
    private Instant updatedAt;

    public StoryRequest() {
        this.createdAt = Instant.now();
    }

    // ----- getters & setters -----
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Enum
    public enum RequestStatus {
        PENDING,
        DONE,
        ERROR
    }
}
