package com.intervueai.backend.dto;

public class GenerateInterviewRequest {
    private String type;
    private String role;
    private String level;
    private String techstack;
    private int amount;
    private String profile;
    private String userid;

    public GenerateInterviewRequest() {
    }

    public GenerateInterviewRequest(String type, String role, String level, String techstack, int amount, String profile, String userid) {
        this.type = type;
        this.role = role;
        this.level = level;
        this.techstack = techstack;
        this.amount = amount;
        this.profile = profile;
        this.userid = userid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTechstack() {
        return techstack;
    }

    public void setTechstack(String techstack) {
        this.techstack = techstack;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
