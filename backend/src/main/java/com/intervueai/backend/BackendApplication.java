package com.intervueai.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@SpringBootApplication
public class BackendApplication {
    public static void main(String[] args) {
        loadDotEnv();
        SpringApplication.run(BackendApplication.class, args);
    }

    private static void loadDotEnv() {
        File envFile = findEnvFile();
        if (envFile != null && envFile.exists()) {
            System.out.println("Loading environment variables from: " + envFile.getAbsolutePath());
            try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        int eqIdx = line.indexOf('=');
                        if (eqIdx > 0) {
                            String key = line.substring(0, eqIdx).trim();
                            String value = line.substring(eqIdx + 1).trim();
                            // Strip quotes
                            if (value.startsWith("\"") && value.endsWith("\"")) {
                                value = value.substring(1, value.length() - 1);
                            } else if (value.startsWith("'") && value.endsWith("'")) {
                                value = value.substring(1, value.length() - 1);
                            }
                            
                            // Set property with raw key
                            System.setProperty(key, value);
                            
                            // Also map environment naming convention (e.g. FIREBASE_PROJECT_ID)
                            // to spring naming convention (e.g. firebase.project.id)
                            String springKey = key.toLowerCase().replace('_', '.');
                            System.setProperty(springKey, value);
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading .env.local: " + e.getMessage());
            }
        } else {
            System.out.println(".env.local file not found. Relying on default system environment variables.");
        }
    }

    private static File findEnvFile() {
        String[] paths = {
            ".env.local",
            "../.env.local",
            "../../.env.local",
            "backend/.env.local"
        };
        for (String p : paths) {
            File file = new File(p);
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }
}

