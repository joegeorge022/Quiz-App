package com.quizmaster.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.quizmaster.model.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses JSON responses from the Groq LLaMA API into Question objects.
 * Handles various response formats and provides robust error handling.
 */
public class QuestionParser {
    
    private final ObjectMapper objectMapper;
    
    /**
     * Constructor initializes the Jackson ObjectMapper
     */
    public QuestionParser() {
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Parses the API response and extracts questions
     * @param apiResponse The raw JSON response from the Groq API
     * @return List of Question objects
     * @throws ParseException if parsing fails
     */
    public List<Question> parseQuestions(String apiResponse) throws ParseException {
        if (apiResponse == null || apiResponse.trim().isEmpty()) {
            throw new ParseException("API response is null or empty");
        }
        
        try {
            // Debug logging
            System.out.println("=== DEBUG: Raw API Response ===");
            System.out.println(apiResponse);
            System.out.println("=== END DEBUG ===");
            
            // First, parse the outer API response structure
            JsonNode rootNode = objectMapper.readTree(apiResponse);
            
            // Extract the content from the API response
            String questionsJson = extractQuestionsJson(rootNode);
            
            // Debug logging for extracted content
            System.out.println("=== DEBUG: Extracted Questions JSON ===");
            System.out.println(questionsJson);
            System.out.println("=== END DEBUG ===");
            
            // Parse the questions JSON array
            return parseQuestionsArray(questionsJson);
            
        } catch (JsonProcessingException e) {
            throw new ParseException("Failed to parse JSON response: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ParseException("Unexpected error during parsing: " + e.getMessage(), e);
        }
    }
    
    /**
     * Extracts the questions JSON from the API response structure
     * @param rootNode The root JSON node of the API response
     * @return The questions JSON string
     * @throws ParseException if the expected structure is not found
     */
    private String extractQuestionsJson(JsonNode rootNode) throws ParseException {
        // Handle direct array format (if API returns questions directly)
        if (rootNode.isArray()) {
            return rootNode.toString();
        }
        
        // Handle Groq API response format
        JsonNode choicesNode = rootNode.get("choices");
        if (choicesNode == null || !choicesNode.isArray() || choicesNode.size() == 0) {
            throw new ParseException("Invalid API response structure: missing 'choices' array");
        }
        
        JsonNode firstChoice = choicesNode.get(0);
        if (firstChoice == null) {
            throw new ParseException("Invalid API response structure: empty choices array");
        }
        
        JsonNode messageNode = firstChoice.get("message");
        if (messageNode == null) {
            throw new ParseException("Invalid API response structure: missing 'message' in choice");
        }
        
        JsonNode contentNode = messageNode.get("content");
        if (contentNode == null) {
            throw new ParseException("Invalid API response structure: missing 'content' in message");
        }
        
        String content = contentNode.asText();
        if (content == null || content.trim().isEmpty()) {
            throw new ParseException("API response content is empty");
        }
        
        // Clean up the content - remove any markdown formatting or extra text
        return cleanJsonContent(content);
    }
    
    /**
     * Cleans the JSON content by removing markdown formatting and extracting the JSON array
     * @param content The raw content string
     * @return Clean JSON string
     */
    private String cleanJsonContent(String content) {
        // Remove markdown code blocks if present
        content = content.replaceAll("```json\\s*", "").replaceAll("```\\s*$", "");
        
        // Find the JSON array boundaries
        int startIndex = content.indexOf('[');
        int endIndex = content.lastIndexOf(']');
        
        if (startIndex == -1 || endIndex == -1 || startIndex >= endIndex) {
            // If no array brackets found, return the content as-is and let Jackson handle it
            return content.trim();
        }
        
        return content.substring(startIndex, endIndex + 1).trim();
    }
    
    /**
     * Parses a JSON array string into a list of Question objects
     * @param questionsJson The JSON array string containing questions
     * @return List of Question objects
     * @throws ParseException if parsing fails
     */
    private List<Question> parseQuestionsArray(String questionsJson) throws ParseException {
        try {
            // Parse as array of Question objects
            List<Question> questions = objectMapper.readValue(
                questionsJson, 
                new TypeReference<List<Question>>() {}
            );
            
            if (questions == null) {
                throw new ParseException("Parsed questions list is null");
            }
            
            // Validate each question
            List<Question> validQuestions = new ArrayList<>();
            for (int i = 0; i < questions.size(); i++) {
                Question question = questions.get(i);
                try {
                    validateQuestion(question, i);
                    validQuestions.add(question);
                } catch (ParseException e) {
                    // Log the invalid question but continue with others
                    System.err.println("Skipping invalid question at index " + i + ": " + e.getMessage());
                }
            }
            
            if (validQuestions.isEmpty()) {
                throw new ParseException("No valid questions found in the response");
            }
            
            return validQuestions;
            
        } catch (JsonProcessingException e) {
            throw new ParseException("Failed to parse questions array: " + e.getMessage(), e);
        }
    }
    
    /**
     * Validates a single Question object
     * @param question The question to validate
     * @param index The index of the question for error reporting
     * @throws ParseException if the question is invalid
     */
    private void validateQuestion(Question question, int index) throws ParseException {
        if (question == null) {
            throw new ParseException("Question at index " + index + " is null");
        }
        
        if (question.getQuestionText() == null || question.getQuestionText().trim().isEmpty()) {
            throw new ParseException("Question at index " + index + " has empty question text");
        }
        
        if (question.getOptions() == null || question.getOptions().isEmpty()) {
            throw new ParseException("Question at index " + index + " has no options");
        }
        
        if (question.getOptions().size() < 2) {
            throw new ParseException("Question at index " + index + " must have at least 2 options");
        }
        
        if (question.getCorrectAnswer() == null || question.getCorrectAnswer().trim().isEmpty()) {
            throw new ParseException("Question at index " + index + " has no correct answer");
        }
        
        // If correct answer is a letter (A, B, C, D), validate it's within range
        if (question.getCorrectAnswer().length() == 1) {
            char letter = question.getCorrectAnswer().toUpperCase().charAt(0);
            int optionIndex = letter - 'A';
            if (optionIndex < 0 || optionIndex >= question.getOptions().size()) {
                throw new ParseException("Question at index " + index + 
                    " has invalid correct answer '" + question.getCorrectAnswer() + "'. " +
                    "Must be a letter between A and " + (char)('A' + question.getOptions().size() - 1));
            }
        } 
        // If correct answer is text, verify it matches one of the options
        else if (!question.getOptions().contains(question.getCorrectAnswer())) {
            throw new ParseException("Question at index " + index + 
                " has correct answer '" + question.getCorrectAnswer() + "' " +
                "which is not in the options list");
        }
        
        // Set default explanation if missing
        if (question.getExplanation() == null || question.getExplanation().trim().isEmpty()) {
            question.setExplanation("No explanation provided.");
        }
    }
    
    /**
     * Parses a single question from JSON string
     * @param questionJson JSON string representing a single question
     * @return Question object
     * @throws ParseException if parsing fails
     */
    public Question parseSingleQuestion(String questionJson) throws ParseException {
        if (questionJson == null || questionJson.trim().isEmpty()) {
            throw new ParseException("Question JSON is null or empty");
        }
        
        try {
            Question question = objectMapper.readValue(questionJson, Question.class);
            validateQuestion(question, 0);
            return question;
        } catch (JsonProcessingException e) {
            throw new ParseException("Failed to parse single question: " + e.getMessage(), e);
        }
    }
    
    /**
     * Custom exception class for parsing errors
     */
    public static class ParseException extends Exception {
        
        public ParseException(String message) {
            super(message);
        }
        
        public ParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
