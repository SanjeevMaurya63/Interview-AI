package com.intervueai.backend.repository;

import com.intervueai.backend.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, String> {
    List<Interview> findByUserIdOrderByCreatedAtDesc(String userId);
    List<Interview> findByFinalizedTrueOrderByCreatedAtDesc();
}
