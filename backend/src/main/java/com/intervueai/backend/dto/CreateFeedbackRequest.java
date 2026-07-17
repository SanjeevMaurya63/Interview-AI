package com.intervueai.backend.dto;

import com.intervueai.backend.model.SavedMessage;
import java.util.List;

public class CreateFeedbackRequest {
    private String interviewId;
    private String userId;
    private List<SavedMessage> transcript;
    private String feedbackId;

    public CreateFeedbackRequest() {
    }

    public CreateFeedbackRequest(String interviewId, String userId, List<SavedMessage> transcript, String feedbackId) {
        this.interviewId = interviewId;
        this.userId = userId;
        this.transcript = transcript;
        this.feedbackId = feedbackId;
    }

    public String getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(String interviewId) {
        this.interviewId = interviewId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<SavedMessage> getTranscript() {
        return transcript;
    }

    public void setTranscript(List<SavedMessage> transcript) {
        this.transcript = transcript;
    }

    public String getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(String feedbackId) {
        this.feedbackId = feedbackId;
    }
}
