package com.example.storyapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class StoryController {

    private static final Logger log = LoggerFactory.getLogger(StoryController.class);

    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @GetMapping("/")
    public String index(Model model) {
        // ensure model has keys used by template
        model.addAttribute("story", "");
        model.addAttribute("words", "");
        model.addAttribute("genre", "");
        return "index";
    }

    @PostMapping("/generate")
    public String generate(@RequestParam("words") String words,
                           @RequestParam(value = "genre", required = false) String genre,
                           Model model) {
        try {
            String story = storyService.generate(words, genre);
            model.addAttribute("story", story);
            model.addAttribute("words", words);
            model.addAttribute("genre", genre);
            return "index";
        } catch (Exception e) {
            log.error("Error while generating story", e);
            String shortMsg = "AI generation failed: " + e.getMessage();
            String fix = "Fix: set AI_GEMINI_KEY env var, enable Generative Language API, enable billing.";
            model.addAttribute("error", shortMsg + " " + fix);
            model.addAttribute("story", "");
            model.addAttribute("words", words);
            model.addAttribute("genre", genre);
            return "index";
        }
    }
}
