package com.quizmaster.service;

import okhttp3.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * HTTP client for making requests to the Groq LLaMA API.
 * Handles authentication, request formatting, and response processing.
 */
public class ApiClient {
    
    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final int TIMEOUT_SECONDS = 15; 
    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");
    
    private final OkHttpClient httpClient;
    private final String apiKey;
    
    /**
     * Constructor for ApiClient
     * @param apiKey The Groq API key for authentication
     */
    public ApiClient(String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API key cannot be null or empty");
        }
        
        this.apiKey = apiKey.trim();
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS) 
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(10, 5, TimeUnit.MINUTES)) 
                .protocols(java.util.Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1)) 
                .retryOnConnectionFailure(true) 
                .build();
    }
    
    /**
     * Sends a request to the Groq LLaMA API to generate quiz questions
     * @param topic The topic for which to generate questions
     * @param numberOfQuestions The number of questions to generate
     * @return The raw JSON response from the API
     * @throws ApiException if the request fails or returns an error
     */
    public String generateQuestions(String topic, int numberOfQuestions) throws ApiException {
        if (topic == null || topic.trim().isEmpty()) {
            throw new IllegalArgumentException("Topic cannot be null or empty");
        }
        
        if (numberOfQuestions <= 0 || numberOfQuestions > 20) {
            throw new IllegalArgumentException("Number of questions must be between 1 and 20");
        }
        
        String requestBody = buildRequestBody(topic.trim(), numberOfQuestions);
        Request request = buildRequest(requestBody);
        
        try (Response response = httpClient.newCall(request).execute()) {
            return handleResponse(response);
        } catch (IOException e) {
            throw new ApiException("Network error occurred while calling Groq API: " + e.getMessage(), e);
        }
    }
    
    /**
     * Builds the JSON request body for the Groq API
     * @param topic The quiz topic
     * @param numberOfQuestions Number of questions to generate
     * @return JSON request body as string
     */
    private String buildRequestBody(String topic, int numberOfQuestions) {
        // Optimized prompt for faster generation
        String prompt = String.format(
            "Generate exactly %d multiple-choice quiz questions about '%s'. " +
            "Return ONLY a valid JSON array. Each question: " +
            "{\"question\": \"text\", \"options\": [\"A\", \"B\", \"C\", \"D\"], " +
            "\"correct_answer\": \"correct option\", \"explanation\": \"brief explanation\"}. " +
            "Make questions factual with exactly 4 options each.",
            numberOfQuestions, topic
        );
        
        return String.format(
            "{\"messages\": [{\"role\": \"user\", \"content\": \"%s\"}], " +
            "\"model\": \"llama-3.3-70b-versatile\", " +
            "\"temperature\": 0.1, " +
            "\"max_tokens\": 1500, " +
            "\"top_p\": 0.9}",
            escapeJsonString(prompt)
        );
    }
    
    /**
     * Builds the HTTP request with proper headers
     * @param requestBody The JSON request body
     * @return Configured Request object
     */
    private Request buildRequest(String requestBody) {
        return new Request.Builder()
                .url(GROQ_API_URL)
                .post(RequestBody.create(requestBody, JSON_MEDIA_TYPE))
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
    }
    
    /**
     * Handles the HTTP response from the API
     * @param response The HTTP response
     * @return The response body as string
     * @throws ApiException if the response indicates an error
     * @throws IOException if reading the response fails
     */
    private String handleResponse(Response response) throws ApiException, IOException {
        if (!response.isSuccessful()) {
            String errorBody = response.body() != null ? response.body().string() : "Unknown error";
            throw new ApiException(
                String.format("API request failed with status %d: %s", response.code(), errorBody)
            );
        }
        
        if (response.body() == null) {
            throw new ApiException("API response body is null");
        }
        
        return response.body().string();
    }
    
    /**
     * Escapes special characters in a string for JSON
     * @param input The input string
     * @return Escaped string safe for JSON
     */
    private String escapeJsonString(String input) {
        if (input == null) {
            return "";
        }
        
        return input.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
    
    /**
     * Tests the API connection with a simple request
     * @return true if the API is accessible and the key is valid
     */
    public boolean testConnection() {
        try {
            generateQuestions("general knowledge", 1);
            return true;
        } catch (ApiException e) {
            return false;
        }
    }
    
    /**
     * Closes the HTTP client and releases resources
     */
    public void close() {
        if (httpClient != null) {
            httpClient.dispatcher().executorService().shutdown();
            httpClient.connectionPool().evictAll();
        }
    }
    
    /**
     * Custom exception class for API-related errors
     */
    public static class ApiException extends Exception {
        
        public ApiException(String message) {
            super(message);
        }
        
        public ApiException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
