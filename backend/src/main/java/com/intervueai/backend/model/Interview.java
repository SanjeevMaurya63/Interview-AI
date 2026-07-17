package com.intervueai.backend.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "interviews")
public class Interview {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "role")
    private String role;

    @Column(name = "level", length = 100)
    private String level;

    // Store as JSON string in a TEXT column
    @Column(name = "questions", columnDefinition = "TEXT")
    private String questionsJson;

    @Column(name = "techstack", columnDefinition = "TEXT")
    private String techstackJson;

    @Column(name = "created_at", length = 50)
    private String createdAt;

    @Column(name = "user_id", length = 128)
    private String userId;

    @Column(name = "type", length = 100)
    private String type;

    @Column(name = "finalized")
    private boolean finalized;

    @Column(name = "cover_image")
    private String coverImage;

    // Transient fields for Java use (not stored directly)
    @Transient
    private List<String> questions;

    @Transient
    private List<String> techstack;

    public Interview() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isFinalized() { return finalized; }
    public void setFinalized(boolean finalized) { this.finalized = finalized; }

    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }

    public String getQuestionsJson() { return questionsJson; }
    public void setQuestionsJson(String questionsJson) { this.questionsJson = questionsJson; }

    public String getTechstackJson() { return techstackJson; }
    public void setTechstackJson(String techstackJson) { this.techstackJson = techstackJson; }

    public List<String> getQuestions() { return questions; }
    public void setQuestions(List<String> questions) { this.questions = questions; }

    public List<String> getTechstack() { return techstack; }
    public void setTechstack(List<String> techstack) { this.techstack = techstack; }
}
