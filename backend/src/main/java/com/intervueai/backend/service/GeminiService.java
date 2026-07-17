package com.intervueai.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intervueai.backend.model.CategoryScore;
import com.intervueai.backend.model.Feedback;
import com.intervueai.backend.model.SavedMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Service
public class GeminiService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    // Strip markdown code blocks if the model outputs them
    private String cleanJsonResponse(String response) {
        String clean = response.trim();
        if (clean.contains("```json")) {
            clean = clean.substring(clean.indexOf("```json") + 7);
            if (clean.contains("```")) {
                clean = clean.substring(0, clean.lastIndexOf("```"));
            }
        } else if (clean.contains("```")) {
            clean = clean.substring(clean.indexOf("```") + 3);
            if (clean.contains("```")) {
                clean = clean.substring(0, clean.lastIndexOf("```"));
            }
        }
        return clean.trim();
    }

    // Call Gemini API helper
    private String callGemini(String prompt, boolean expectJson) throws Exception {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("Google Gemini API Key is not configured.");
        }

        // Build the request payload
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);

        Map<String, Object> partsMap = new HashMap<>();
        partsMap.put("parts", List.of(textPart));

        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("contents", List.of(partsMap));

        if (expectJson) {
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("responseMimeType", "application/json");
            contentMap.put("generationConfig", generationConfig);
        }

        String requestBody = objectMapper.writeValueAsString(contentMap);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(GEMINI_API_URL + "?key=" + apiKey))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to call Gemini API: status code " + response.statusCode() + ", body: " + response.body());
        }

        // Parse response to extract the text content
        JsonNode rootNode = objectMapper.readTree(response.body());
        JsonNode candidates = rootNode.path("candidates");
        if (candidates.isArray() && !candidates.isEmpty()) {
            JsonNode firstCandidate = candidates.get(0);
            JsonNode textNode = firstCandidate.path("content").path("parts").get(0).path("text");
            return textNode.asText();
        }

        throw new RuntimeException("Invalid response format from Gemini API: " + response.body());
    }

    // Generate Interview Questions
    public List<String> generateQuestions(String role, String level, String techstack, String type, String profile, int amount) {
        String prompt = String.format(
            "Prepare questions for a job interview.\n" +
            "The job role is: %s.\n" +
            "The job experience level is: %s.\n" +
            "The tech stack used in the job is: %s.\n" +
            "The focus between behavioural and technical questions should lean towards: %s.\n" +
            "The user's cv/resume profile is: %s.\n" +
            "The amount of questions required is: %d.\n" +
            "Please return only a JSON array of strings containing the questions, without any additional text.\n" +
            "The questions are going to be read by a voice assistant, so do not use '/' or '*' or any other special characters which might break the voice assistant.\n" +
            "Return the questions formatted strictly like this:\n" +
            "[\"Question 1\", \"Question 2\", \"Question 3\"]",
            role, level, techstack, type, profile, amount
        );

        try {
            String resultText = callGemini(prompt, true);
            String cleanedJson = cleanJsonResponse(resultText);
            return objectMapper.readValue(cleanedJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            System.err.println("Error generating questions with Gemini: " + e.getMessage());
            // Return fallback questions in case of error
            return List.of(
                "Can you tell me about yourself and your background with " + techstack + "?",
                "What is the most challenging technical project you have worked on as a " + role + "?",
                "How do you handle conflict or differences of opinion in a team environment?"
            );
        }
    }

    // Evaluate Interview Transcript and Generate Structured Feedback
    public Feedback generateFeedback(String interviewId, String userId, List<SavedMessage> transcript) {
        StringBuilder formattedTranscript = new StringBuilder();
        for (SavedMessage msg : transcript) {
            formattedTranscript.append("- ").append(msg.getRole()).append(": ").append(msg.getContent()).append("\n");
        }

        String prompt = String.format(
            "You are an AI interviewer analyzing a mock interview. Your task is to evaluate the candidate based on structured categories. Be thorough and detailed in your analysis. Don't be lenient with the candidate. If there are mistakes or areas for improvement, point them out.\n\n" +
            "Transcript:\n" +
            "%s\n\n" +
            "Please score the candidate from 0 to 100 in the following categories. Do not add categories other than the ones provided. Return only a valid JSON object matching the following structure:\n" +
            "{\n" +
            "  \"totalScore\": number,\n" +
            "  \"categoryScores\": [\n" +
            "    {\n" +
            "      \"name\": \"Communication Skills\",\n" +
            "      \"score\": number,\n" +
            "      \"comment\": \"string\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Technical Knowledge\",\n" +
            "      \"score\": number,\n" +
            "      \"comment\": \"string\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Problem Solving\",\n" +
            "      \"score\": number,\n" +
            "      \"comment\": \"string\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Cultural Fit\",\n" +
            "      \"score\": number,\n" +
            "      \"comment\": \"string\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Confidence and Clarity\",\n" +
            "      \"score\": number,\n" +
            "      \"comment\": \"string\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"strengths\": [\"string\"],\n" +
            "  \"areasForImprovement\": [\"string\"],\n" +
            "  \"finalAssessment\": \"string\"\n" +
            "}",
            formattedTranscript.toString()
        );

        try {
            String resultText = callGemini(prompt, true);
            String cleanedJson = cleanJsonResponse(resultText);

            Feedback feedback = objectMapper.readValue(cleanedJson, Feedback.class);
            feedback.setInterviewId(interviewId);
            feedback.setUserId(userId);
            feedback.setCreatedAt(Instant.now().toString());

            return feedback;
        } catch (Exception e) {
            System.err.println("Error generating feedback with Gemini: " + e.getMessage());
            
            // Fallback mock feedback
            Feedback fallback = new Feedback();
            fallback.setInterviewId(interviewId);
            fallback.setUserId(userId);
            fallback.setTotalScore(70);
            fallback.setCategoryScores(List.of(
                new CategoryScore("Communication Skills", 70, "Good effort, but answers could be more structured."),
                new CategoryScore("Technical Knowledge", 75, "Demonstrated good baseline skills."),
                new CategoryScore("Problem Solving", 65, "Needs to walk through the logic more clearly."),
                new CategoryScore("Cultural Fit", 75, "Aligned with company values."),
                new CategoryScore("Confidence and Clarity", 65, "Try to speak with more conviction.")
            ));
            fallback.setStrengths(List.of("Strong enthusiasm", "Honest responses"));
            fallback.setAreasForImprovement(List.of("Structure answers using STAR method", "Provide deeper technical depth"));
            fallback.setFinalAssessment("Overall a decent attempt. Practice structuring your answers to boost performance.");
            fallback.setCreatedAt(Instant.now().toString());
            return fallback;
        }
    }
}
