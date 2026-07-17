package com.intervueai.backend.model;

public class CategoryScore {
    private String name;
    private int score;
    private String comment;

    public CategoryScore() {
    }

    public CategoryScore(String name, int score, String comment) {
        this.name = name;
        this.score = score;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
