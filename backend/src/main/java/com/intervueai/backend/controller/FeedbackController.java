package com.intervueai.backend.controller;

import com.intervueai.backend.dto.CreateFeedbackRequest;
import com.intervueai.backend.model.Feedback;
import com.intervueai.backend.service.GeminiService;
import com.intervueai.backend.service.SqlDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "*")
public class FeedbackController {

    @Autowired
    private SqlDatabaseService sqlDatabaseService;

    @Autowired
    private GeminiService geminiService;

    // Generate AI feedback and save to MySQL
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createFeedback(@RequestBody CreateFeedbackRequest req) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Generate structured feedback using Gemini
            Feedback feedback = geminiService.generateFeedback(
                req.getInterviewId(),
                req.getUserId(),
                req.getTranscript()
            );

            // Save feedback to MySQL
            String feedbackId = sqlDatabaseService.saveFeedback(feedback, req.getFeedbackId());

            response.put("success", true);
            response.put("feedbackId", feedbackId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error evaluating transcript: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Get feedback for an interview (from MySQL)
    @GetMapping("/interview/{interviewId}")
    public ResponseEntity<Feedback> getFeedback(
            @PathVariable String interviewId,
            @RequestParam("userId") String userId) {
        try {
            Feedback feedback = sqlDatabaseService.getFeedbackByInterviewId(interviewId, userId);
            if (feedback == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(feedback);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
