package com.intervueai.backend.controller;

import com.intervueai.backend.dto.GenerateInterviewRequest;
import com.intervueai.backend.model.Interview;
import com.intervueai.backend.service.GeminiService;
import com.intervueai.backend.service.SqlDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/interviews")
@CrossOrigin(origins = "*")
public class InterviewController {

    @Autowired
    private SqlDatabaseService sqlDatabaseService;

    @Autowired
    private GeminiService geminiService;

    private static final String[] INTERVIEW_COVERS = {
        "/tcs.png", "/accenture.png", "/infosys.jpg", "/adobe.png",
        "/amazon.png", "/facebook.png", "/google.png", "/pinterest.png",
        "/quora.png", "/wipro.png", "/flipkart.png", "/telegram.png",
        "/microsoft.png", "/hcl.jpg"
    };

    private String getRandomInterviewCover() {
        int index = new Random().nextInt(INTERVIEW_COVERS.length);
        return "/covers" + INTERVIEW_COVERS[index];
    }

    // Get interviews by userId (from MySQL)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Interview>> getInterviewsByUserId(@PathVariable String userId) {
        try {
            List<Interview> interviews = sqlDatabaseService.getInterviewsByUserId(userId);
            return ResponseEntity.ok(interviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get latest finalized interviews from other users (from MySQL)
    @GetMapping("/latest")
    public ResponseEntity<List<Interview>> getLatestInterviews(@RequestParam("userId") String userId) {
        try {
            List<Interview> interviews = sqlDatabaseService.getLatestInterviews(userId);
            return ResponseEntity.ok(interviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get interview by ID (from MySQL)
    @GetMapping("/{id}")
    public ResponseEntity<Interview> getInterviewById(@PathVariable String id) {
        try {
            Interview interview = sqlDatabaseService.getInterviewById(id);
            if (interview == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(interview);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Generate questions with Gemini and save interview to MySQL
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateInterview(@RequestBody GenerateInterviewRequest req) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Generate questions using Gemini
            List<String> questions = geminiService.generateQuestions(
                req.getRole(),
                req.getLevel(),
                req.getTechstack(),
                req.getType(),
                req.getProfile(),
                req.getAmount()
            );

            // Build Interview object
            Interview interview = new Interview();
            interview.setRole(req.getRole());
            interview.setType(req.getType());
            interview.setLevel(req.getLevel());
            interview.setUserId(req.getUserid());
            interview.setQuestions(questions);

            // Split techstack by comma
            List<String> techstackList = new ArrayList<>();
            if (req.getTechstack() != null && !req.getTechstack().isEmpty()) {
                for (String tech : req.getTechstack().split(",")) {
                    techstackList.add(tech.trim());
                }
            }
            interview.setTechstack(techstackList);
            interview.setFinalized(true);
            interview.setCoverImage(getRandomInterviewCover());
            interview.setCreatedAt(Instant.now().toString());

            // Save to MySQL
            String interviewId = sqlDatabaseService.saveInterview(interview);

            response.put("success", true);
            response.put("interviewId", interviewId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error generating interview: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
