package com.intervueai.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * FirebaseConfig - Initializes Firebase App for Authentication only.
 * Firestore is no longer used; data is stored in MySQL via Spring Data JPA.
 */
@Configuration
public class FirebaseConfig {

    @Value("${firebase.project.id:}")
    private String projectId;

    @Value("${firebase.client.email:}")
    private String clientEmail;

    @Value("${firebase.private.key:}")
    private String privateKey;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            if (projectId == null || projectId.isEmpty()) {
                throw new IllegalStateException(
                    "FIREBASE_PROJECT_ID is not configured. Please set FIREBASE_PROJECT_ID environment variable."
                );
            }

            // Normalize private key (replace literal '\n' with actual newlines)
            String normalizedKey = privateKey != null ? privateKey.replace("\\n", "\n") : "";

            // Reconstruct service account JSON
            String serviceAccountJson = String.format(
                "{\n" +
                "  \"type\": \"service_account\",\n" +
                "  \"project_id\": \"%s\",\n" +
                "  \"client_email\": \"%s\",\n" +
                "  \"private_key\": \"%s\"\n" +
                "}",
                projectId,
                clientEmail,
                normalizedKey.replace("\"", "\\\"").replace("\n", "\\n")
            );

            ByteArrayInputStream serviceAccountStream = new ByteArrayInputStream(
                serviceAccountJson.getBytes(StandardCharsets.UTF_8)
            );

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                .setProjectId(projectId)
                .build();

            return FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        return FirebaseAuth.getInstance();
    }
}
