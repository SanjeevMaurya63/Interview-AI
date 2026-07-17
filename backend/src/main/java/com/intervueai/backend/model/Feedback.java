package com.intervueai.backend.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "interview_id", length = 36)
    private String interviewId;

    @Column(name = "user_id", length = 128)
    private String userId;

    @Column(name = "total_score")
    private int totalScore;

    // Stored as JSON string in TEXT column
    @Column(name = "category_scores", columnDefinition = "TEXT")
    private String categoryScoresJson;

    @Column(name = "strengths", columnDefinition = "TEXT")
    private String strengthsJson;

    @Column(name = "areas_for_improvement", columnDefinition = "TEXT")
    private String areasForImprovementJson;

    @Column(name = "final_assessment", columnDefinition = "TEXT")
    private String finalAssessment;

    @Column(name = "created_at", length = 50)
    private String createdAt;

    // Transient fields for Java use
    @Transient
    private List<CategoryScore> categoryScores;

    @Transient
    private List<String> strengths;

    @Transient
    private List<String> areasForImprovement;

    public Feedback() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getInterviewId() { return interviewId; }
    public void setInterviewId(String interviewId) { this.interviewId = interviewId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }

    public String getCategoryScoresJson() { return categoryScoresJson; }
    public void setCategoryScoresJson(String categoryScoresJson) { this.categoryScoresJson = categoryScoresJson; }

    public String getStrengthsJson() { return strengthsJson; }
    public void setStrengthsJson(String strengthsJson) { this.strengthsJson = strengthsJson; }

    public String getAreasForImprovementJson() { return areasForImprovementJson; }
    public void setAreasForImprovementJson(String areasForImprovementJson) { this.areasForImprovementJson = areasForImprovementJson; }

    public String getFinalAssessment() { return finalAssessment; }
    public void setFinalAssessment(String finalAssessment) { this.finalAssessment = finalAssessment; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public List<CategoryScore> getCategoryScores() { return categoryScores; }
    public void setCategoryScores(List<CategoryScore> categoryScores) { this.categoryScores = categoryScores; }

    public List<String> getStrengths() { return strengths; }
    public void setStrengths(List<String> strengths) { this.strengths = strengths; }

    public List<String> getAreasForImprovement() { return areasForImprovement; }
    public void setAreasForImprovement(List<String> areasForImprovement) { this.areasForImprovement = areasForImprovement; }
}
