package com.internship.tool.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.Map;
import java.util.HashMap;

/**
 * AiServiceClient.java
 * Tool-21: Audit Planning and Scheduling
 * AI Developer 3 — Day 6 Task
 *
 * This class handles all communication between the
 * Java Spring Boot backend and the Python Flask AI service.
 * - Uses RestTemplate to make HTTP calls
 * - 10 second timeout on all requests
 * - Returns null gracefully if AI service is unavailable
 */
@Service
public class AiServiceClient {

    // Logger to record errors without crashing the app
    private static final Logger logger = LoggerFactory.getLogger(AiServiceClient.class);

    // AI service base URL from application.yml
    @Value("${ai.service.url:http://localhost:5000}")
    private String aiServiceUrl;

    // ─────────────────────────────────────────────
    // 1. CREATE REST TEMPLATE WITH 10s TIMEOUT
    // ─────────────────────────────────────────────
    private RestTemplate buildRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10_000); // 10 seconds to connect
        factory.setReadTimeout(10_000);    // 10 seconds to read response
        return new RestTemplate(factory);
    }

    // ─────────────────────────────────────────────
    // HELPER — Build JSON headers
    // ─────────────────────────────────────────────
    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    // ─────────────────────────────────────────────
    // HELPER — Make POST request to Flask
    // Returns null if anything goes wrong
    // ─────────────────────────────────────────────
    private Map<String, Object> post(String endpoint, Map<String, Object> payload) {
        try {
            RestTemplate restTemplate = buildRestTemplate();
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, buildHeaders());

            ResponseEntity<Map> response = restTemplate.postForEntity(
                aiServiceUrl + endpoint,
                request,
                Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }

            logger.warn("AI service returned non-200 status: {}", response.getStatusCode());
            return null;

        } catch (Exception e) {
            logger.error("AI service call failed for endpoint {}: {}", endpoint, e.getMessage());
            return null; // Graceful null return — app keeps working!
        }
    }

    // ─────────────────────────────────────────────
    // 2. CALL /describe ENDPOINT
    // Generates a description for an audit item
    // ─────────────────────────────────────────────
    public Map<String, Object> describe(String text) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("text", text);
            return post("/describe", payload);
        } catch (Exception e) {
            logger.error("Error calling /describe: {}", e.getMessage());
            return null;
        }
    }

    // ─────────────────────────────────────────────
    // 3. CALL /recommend ENDPOINT
    // Gets AI recommendations for an audit item
    // ─────────────────────────────────────────────
    public Map<String, Object> recommend(String text) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("text", text);
            return post("/recommend", payload);
        } catch (Exception e) {
            logger.error("Error calling /recommend: {}", e.getMessage());
            return null;
        }
    }

    // ─────────────────────────────────────────────
    // 4. CALL /categorise ENDPOINT
    // Classifies an audit item into a category
    // ─────────────────────────────────────────────
    public Map<String, Object> categorise(String text) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("text", text);
            return post("/categorise", payload);
        } catch (Exception e) {
            logger.error("Error calling /categorise: {}", e.getMessage());
            return null;
        }
    }

    // ─────────────────────────────────────────────
    // 5. CALL /generate-report ENDPOINT
    // Generates a full audit report
    // ─────────────────────────────────────────────
    public Map<String, Object> generateReport(String text) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("text", text);
            return post("/generate-report", payload);
        } catch (Exception e) {
            logger.error("Error calling /generate-report: {}", e.getMessage());
            return null;
        }
    }

    // ─────────────────────────────────────────────
    // 6. CALL /query ENDPOINT
    // RAG query — asks AI a question about documents
    // ─────────────────────────────────────────────
    public Map<String, Object> query(String question) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("question", question);
            return post("/query", payload);
        } catch (Exception e) {
            logger.error("Error calling /query: {}", e.getMessage());
            return null;
        }
    }

    // ─────────────────────────────────────────────
    // 7. CHECK /health ENDPOINT
    // Checks if AI service is running
    // ─────────────────────────────────────────────
    public boolean isHealthy() {
        try {
            RestTemplate restTemplate = buildRestTemplate();
            ResponseEntity<Map> response = restTemplate.getForEntity(
                aiServiceUrl + "/health",
                Map.class
            );
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.error("AI service health check failed: {}", e.getMessage());
            return false; // Return false instead of crashing
        }
    }
}
