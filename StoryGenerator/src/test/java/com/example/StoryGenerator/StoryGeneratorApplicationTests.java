package com.example.StoryGenerator; // <-- use your real package where StoryGeneratorApplication lives

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = com.example.storyapp.StoryGeneratorApplication.class)
class StoryGeneratorApplicationTests {

    @Test
    void contextLoads() {
        // simple smoke test
    }
}
