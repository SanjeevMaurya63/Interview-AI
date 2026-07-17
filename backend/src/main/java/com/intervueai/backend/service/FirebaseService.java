package com.intervueai.backend.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * FirebaseService - Only handles Firebase Authentication.
 * Data storage (users, interviews, feedback) is now handled by SqlDatabaseService via MySQL.
 */
@Service
public class FirebaseService {

    @Autowired
    private FirebaseAuth firebaseAuth;

    /**
     * Verify a Firebase ID Token and return the UID.
     */
    public String verifySessionToken(String idToken) {
        try {
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
            return decodedToken.getUid();
        } catch (Exception e) {
            System.err.println("Error verifying ID token: " + e.getMessage());
            return null;
        }
    }

    /**
     * Verify a Firebase session cookie and return the UID.
     */
    public String verifySessionCookie(String sessionCookie) {
        try {
            FirebaseToken decodedToken = firebaseAuth.verifySessionCookie(sessionCookie, true);
            return decodedToken.getUid();
        } catch (Exception e) {
            System.err.println("Error verifying session cookie: " + e.getMessage());
            return null;
        }
    }

    /**
     * Create a Firebase session cookie from an ID Token.
     */
    public String createSessionCookie(String idToken, long expiresInMillis) {
        try {
            com.google.firebase.auth.SessionCookieOptions options =
                com.google.firebase.auth.SessionCookieOptions.builder()
                    .setExpiresIn(expiresInMillis)
                    .build();
            return firebaseAuth.createSessionCookie(idToken, options);
        } catch (Exception e) {
            System.err.println("Error creating session cookie: " + e.getMessage());
            return null;
        }
    }
}
