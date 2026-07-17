package com.intervueai.backend.controller;

import com.intervueai.backend.dto.SignInRequest;
import com.intervueai.backend.dto.SignUpRequest;
import com.intervueai.backend.model.User;
import com.intervueai.backend.service.FirebaseService;
import com.intervueai.backend.service.SqlDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private FirebaseService firebaseService;

    @Autowired
    private SqlDatabaseService sqlDatabaseService;

    // Register a new user (stores in MySQL)
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signUp(@RequestBody SignUpRequest req) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (sqlDatabaseService.userExists(req.getUid())) {
                response.put("success", false);
                response.put("message", "User already exists. Please sign in instead.");
                return ResponseEntity.ok(response);
            }
            sqlDatabaseService.saveUser(req.getUid(), req.getName(), req.getEmail());
            response.put("success", true);
            response.put("message", "User created successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to create user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Verify Firebase token and return session cookie
    @PostMapping("/signin")
    public ResponseEntity<Map<String, Object>> signIn(@RequestBody SignInRequest req) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Verify Firebase ID token
            String uid = firebaseService.verifySessionToken(req.getIdToken());
            if (uid == null) {
                response.put("success", false);
                response.put("message", "Invalid login token.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Check/save user in MySQL
            User user = sqlDatabaseService.getUser(uid);
            if (user == null) {
                sqlDatabaseService.saveUser(uid, req.getEmail().split("@")[0], req.getEmail());
                user = sqlDatabaseService.getUser(uid);
            }

            // Create Firebase session cookie
            long twoWeeksMillis = 1000L * 60 * 60 * 24 * 14;
            String sessionCookie = firebaseService.createSessionCookie(req.getIdToken(), twoWeeksMillis);

            response.put("success", true);
            response.put("sessionCookie", sessionCookie);
            response.put("user", user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Sign in failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Retrieve current user from MySQL using session cookie (verified by Firebase)
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@RequestParam("sessionCookie") String sessionCookie) {
        try {
            String uid = firebaseService.verifySessionCookie(sessionCookie);
            if (uid == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            User user = sqlDatabaseService.getUser(uid);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
