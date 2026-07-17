package com.intervueai.backend.repository;

import com.intervueai.backend.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, String> {
    Optional<Feedback> findByInterviewIdAndUserId(String interviewId, String userId);
}
