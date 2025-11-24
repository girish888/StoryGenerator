package com.example.storyapp.repository;

import com.example.storyapp.model.StoryRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryRequestRepository extends JpaRepository<StoryRequest, Long> {
    // This interface allows the Service to communicate with the Database
}