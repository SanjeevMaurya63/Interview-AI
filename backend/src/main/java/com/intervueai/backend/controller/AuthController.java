package com.intervueai.backend.controller;

import com.intervueai.backend.dto.SignInRequest;
import com.intervueai.backend.dto.SignUpRequest;
import com.intervueai.backend.model.User;
import com.intervueai.backend.security.JwtService;
import com.intervueai.backend.service.SqlDatabaseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private SqlDatabaseService sqlDatabaseService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register a new user
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signUp(@Valid @RequestBody SignUpRequest req) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (sqlDatabaseService.userExistsByEmail(req.getEmail())) {
                response.put("success", false);
                response.put("message", "User already exists. Please sign in instead.");
                return ResponseEntity.ok(response);
            }

            User user = sqlDatabaseService.saveUser(req.getName(), req.getEmail(), req.getPassword());
            String token = jwtService.generateToken(user.getId());

            response.put("success", true);
            response.put("message", "User created successfully");
            response.put("token", token);
            response.put("user", Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail()
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to create user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Sign in with email & password
    @PostMapping("/signin")
    public ResponseEntity<Map<String, Object>> signIn(@Valid @RequestBody SignInRequest req) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = sqlDatabaseService.getUserByEmail(req.getEmail());
            if (user == null) {
                response.put("success", false);
                response.put("message", "User does not exist. Create an account instead.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
                response.put("success", false);
                response.put("message", "Invalid password.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = jwtService.generateToken(user.getId());

            response.put("success", true);
            response.put("token", token);
            response.put("user", Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail()
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Sign in failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Get current user from token
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String token = authHeader.substring(7);
            String userId = jwtService.extractUserId(token);

            if (!jwtService.isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            User user = sqlDatabaseService.getUser(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            response.put("id", user.getId());
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

