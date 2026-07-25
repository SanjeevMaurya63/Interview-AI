package com.intervueai.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intervueai.backend.model.CategoryScore;
import com.intervueai.backend.model.Feedback;
import com.intervueai.backend.model.SavedMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class GeminiService {

    @Autowired
    private ChatModel chatModel;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Generate Interview Questions
    public List<String> generateQuestions(String role, String level, String techstack, String type, String profile, int amount) {
        String promptTemplate = """
            Prepare questions for a job interview.
            The job role is: {role}.
            The job experience level is: {level}.
            The tech stack used in the job is: {techstack}.
            The focus between behavioural and technical questions should lean towards: {type}.
            The user's cv/resume profile is: {profile}.
            The amount of questions required is: {amount}.
            Please return only a JSON array of strings containing the questions, without any additional text.
            The questions are going to be read by a voice assistant, so do not use '/' or '*' or any other special characters which might break the voice assistant.
            Return the questions formatted strictly like this:
            ["Question 1", "Question 2", "Question 3"]
            """;

        try {
            PromptTemplate template = new PromptTemplate(promptTemplate);
            Prompt prompt = template.create(Map.of(
                "role", role,
                "level", level,
                "techstack", techstack,
                "type", type,
                "profile", profile,
                "amount", String.valueOf(amount)
            ));

            String response = chatModel.call(prompt).getResult().getOutput().getText();
            String cleanedJson = cleanJsonResponse(response);
            return objectMapper.readValue(cleanedJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            System.err.println("Error generating questions with Spring AI: " + e.getMessage());
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

        String promptTemplate = """
            You are an AI interviewer analyzing a mock interview. Your task is to evaluate the candidate based on structured categories. Be thorough and detailed in your analysis. Don't be lenient with the candidate. If there are mistakes or areas for improvement, point them out.
            
            Transcript:
            {transcript}
            
            Please score the candidate from 0 to 100 in the following categories. Do not add categories other than the ones provided. Return only a valid JSON object matching the following structure:
            {{
              "totalScore": number,
              "categoryScores": [
                {{
                  "name": "Communication Skills",
                  "score": number,
                  "comment": "string"
                }},
                {{
                  "name": "Technical Knowledge",
                  "score": number,
                  "comment": "string"
                }},
                {{
                  "name": "Problem Solving",
                  "score": number,
                  "comment": "string"
                }},
                {{
                  "name": "Cultural Fit",
                  "score": number,
                  "comment": "string"
                }},
                {{
                  "name": "Confidence and Clarity",
                  "score": number,
                  "comment": "string"
                }}
              ],
              "strengths": ["string"],
              "areasForImprovement": ["string"],
              "finalAssessment": "string"
            }}
            """;

        try {
            PromptTemplate template = new PromptTemplate(promptTemplate);
            Prompt prompt = template.create(Map.of("transcript", formattedTranscript.toString()));

            String response = chatModel.call(prompt).getResult().getOutput().getText();
            String cleanedJson = cleanJsonResponse(response);

            Feedback feedback = objectMapper.readValue(cleanedJson, Feedback.class);
            feedback.setInterviewId(interviewId);
            feedback.setUserId(userId);
            feedback.setCreatedAt(Instant.now().toString());

            return feedback;
        } catch (Exception e) {
            System.err.println("Error generating feedback with Spring AI: " + e.getMessage());
            
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
}

