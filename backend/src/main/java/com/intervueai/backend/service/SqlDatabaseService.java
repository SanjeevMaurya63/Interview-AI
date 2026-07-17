package com.intervueai.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intervueai.backend.model.CategoryScore;
import com.intervueai.backend.model.Feedback;
import com.intervueai.backend.model.Interview;
import com.intervueai.backend.model.User;
import com.intervueai.backend.repository.FeedbackRepository;
import com.intervueai.backend.repository.InterviewRepository;
import com.intervueai.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SqlDatabaseService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ============================================================
    // USER OPERATIONS
    // ============================================================

    public boolean userExists(String uid) {
        return userRepository.existsById(uid);
    }

    public void saveUser(String uid, String name, String email) {
        User user = new User(uid, name, email);
        userRepository.save(user);
    }

    public User getUser(String uid) {
        return userRepository.findById(uid).orElse(null);
    }

    // ============================================================
    // INTERVIEW OPERATIONS
    // ============================================================

    public List<Interview> getInterviewsByUserId(String userId) {
        List<Interview> interviews = interviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
        interviews.forEach(this::deserializeInterview);
        return interviews;
    }

    public List<Interview> getLatestInterviews(String userId) {
        List<Interview> allFinalized = interviewRepository.findByFinalizedTrueOrderByCreatedAtDesc();
        List<Interview> filtered = allFinalized.stream()
            .filter(i -> userId == null || !userId.equals(i.getUserId()))
            .collect(Collectors.toList());
        filtered.forEach(this::deserializeInterview);
        return filtered;
    }

    public Interview getInterviewById(String id) {
        Optional<Interview> opt = interviewRepository.findById(id);
        if (opt.isPresent()) {
            Interview interview = opt.get();
            deserializeInterview(interview);
            return interview;
        }
        return null;
    }

    public String saveInterview(Interview interview) {
        if (interview.getId() == null || interview.getId().isEmpty()) {
            interview.setId(UUID.randomUUID().toString());
        }
        // Serialize lists to JSON strings before saving
        serializeInterview(interview);
        interviewRepository.save(interview);
        return interview.getId();
    }

    // ============================================================
    // FEEDBACK OPERATIONS
    // ============================================================

    public String saveFeedback(Feedback feedback, String feedbackId) {
        if (feedbackId == null || feedbackId.isEmpty()) {
            feedback.setId(UUID.randomUUID().toString());
        } else {
            feedback.setId(feedbackId);
        }
        // Serialize lists to JSON strings before saving
        serializeFeedback(feedback);
        feedbackRepository.save(feedback);
        return feedback.getId();
    }

    public Feedback getFeedbackByInterviewId(String interviewId, String userId) {
        Optional<Feedback> opt = feedbackRepository.findByInterviewIdAndUserId(interviewId, userId);
        if (opt.isPresent()) {
            Feedback feedback = opt.get();
            deserializeFeedback(feedback);
            return feedback;
        }
        return null;
    }

    // ============================================================
    // PRIVATE HELPERS: JSON Serialization / Deserialization
    // ============================================================

    private void serializeInterview(Interview interview) {
        try {
            if (interview.getQuestions() != null) {
                interview.setQuestionsJson(objectMapper.writeValueAsString(interview.getQuestions()));
            }
            if (interview.getTechstack() != null) {
                interview.setTechstackJson(objectMapper.writeValueAsString(interview.getTechstack()));
            }
        } catch (Exception e) {
            System.err.println("Error serializing interview JSON: " + e.getMessage());
        }
    }

    private void deserializeInterview(Interview interview) {
        try {
            if (interview.getQuestionsJson() != null && !interview.getQuestionsJson().isEmpty()) {
                List<String> questions = objectMapper.readValue(
                    interview.getQuestionsJson(), new TypeReference<List<String>>() {});
                interview.setQuestions(questions);
            } else {
                interview.setQuestions(new ArrayList<>());
            }
            if (interview.getTechstackJson() != null && !interview.getTechstackJson().isEmpty()) {
                List<String> techstack = objectMapper.readValue(
                    interview.getTechstackJson(), new TypeReference<List<String>>() {});
                interview.setTechstack(techstack);
            } else {
                interview.setTechstack(new ArrayList<>());
            }
        } catch (Exception e) {
            System.err.println("Error deserializing interview JSON: " + e.getMessage());
            interview.setQuestions(new ArrayList<>());
            interview.setTechstack(new ArrayList<>());
        }
    }

    private void serializeFeedback(Feedback feedback) {
        try {
            if (feedback.getCategoryScores() != null) {
                feedback.setCategoryScoresJson(objectMapper.writeValueAsString(feedback.getCategoryScores()));
            }
            if (feedback.getStrengths() != null) {
                feedback.setStrengthsJson(objectMapper.writeValueAsString(feedback.getStrengths()));
            }
            if (feedback.getAreasForImprovement() != null) {
                feedback.setAreasForImprovementJson(objectMapper.writeValueAsString(feedback.getAreasForImprovement()));
            }
        } catch (Exception e) {
            System.err.println("Error serializing feedback JSON: " + e.getMessage());
        }
    }

    private void deserializeFeedback(Feedback feedback) {
        try {
            if (feedback.getCategoryScoresJson() != null && !feedback.getCategoryScoresJson().isEmpty()) {
                List<CategoryScore> scores = objectMapper.readValue(
                    feedback.getCategoryScoresJson(), new TypeReference<List<CategoryScore>>() {});
                feedback.setCategoryScores(scores);
            } else {
                feedback.setCategoryScores(new ArrayList<>());
            }
            if (feedback.getStrengthsJson() != null && !feedback.getStrengthsJson().isEmpty()) {
                List<String> strengths = objectMapper.readValue(
                    feedback.getStrengthsJson(), new TypeReference<List<String>>() {});
                feedback.setStrengths(strengths);
            } else {
                feedback.setStrengths(new ArrayList<>());
            }
            if (feedback.getAreasForImprovementJson() != null && !feedback.getAreasForImprovementJson().isEmpty()) {
                List<String> areas = objectMapper.readValue(
                    feedback.getAreasForImprovementJson(), new TypeReference<List<String>>() {});
                feedback.setAreasForImprovement(areas);
            } else {
                feedback.setAreasForImprovement(new ArrayList<>());
            }
        } catch (Exception e) {
            System.err.println("Error deserializing feedback JSON: " + e.getMessage());
            feedback.setCategoryScores(new ArrayList<>());
            feedback.setStrengths(new ArrayList<>());
            feedback.setAreasForImprovement(new ArrayList<>());
        }
    }
}
