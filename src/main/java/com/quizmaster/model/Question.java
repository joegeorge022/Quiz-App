package com.quizmaster.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;

/**
 * Represents a multiple-choice quiz question with options and correct answer.
 * This class is used for JSON deserialization from the Groq LLaMA API response.
 */
public class Question {
    
    @JsonProperty("question")
    private String questionText;
    
    @JsonProperty("options")
    private List<String> options;
    
    @JsonProperty("correct_answer")
    private String correctAnswer;
    
    @JsonProperty("explanation")
    private String explanation;
    
    private String userAnswer;
    private boolean answered;
    
    /**
     * Default constructor for Jackson deserialization
     */
    public Question() {}
    
    /**
     * Constructor for creating a Question object
     * @param questionText The question text
     * @param options List of answer options
     * @param correctAnswer The correct answer
     * @param explanation Explanation for the correct answer
     */
    public Question(String questionText, List<String> options, String correctAnswer, String explanation) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
        this.answered = false;
    }
    
    // Getters and Setters
    public String getQuestionText() {
        return questionText;
    }
    
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
    
    public List<String> getOptions() {
        return options;
    }
    
    public void setOptions(List<String> options) {
        this.options = options;
    }
    
    public String getCorrectAnswer() {
        return correctAnswer;
    }
    
    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
    
    public String getExplanation() {
        return explanation;
    }
    
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
    
    public String getUserAnswer() {
        return userAnswer;
    }
    
    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
        this.answered = true;
    }
    
    public boolean isAnswered() {
        return answered;
    }
    
    public void setAnswered(boolean answered) {
        this.answered = answered;
    }
    
    /**
     * Checks if the user's answer is correct
     * @return true if the user's answer matches the correct answer
     */
    public boolean isCorrect() {
        if (!answered) {
            return false;
        }
        
        // If correct answer is a single letter (A, B, C, D), check against options list
        if (correctAnswer != null && correctAnswer.length() == 1) {
            char letter = correctAnswer.toUpperCase().charAt(0);
            int optionIndex = letter - 'A'; // Convert A->0, B->1, etc.
            if (optionIndex >= 0 && optionIndex < options.size()) {
                return options.get(optionIndex).equals(userAnswer);
            }
        }
        
        // Fall back to direct comparison
        return Objects.equals(userAnswer, correctAnswer);
    }
    
    /**
     * Gets the correct answer text (resolves letter to text if needed)
     * @return The full text of the correct answer
     */
    public String getCorrectAnswerText() {
        if (correctAnswer == null) {
            return "";
        }
        
        // If correct answer is a single letter (A, B, C, D), return the corresponding option
        if (correctAnswer.length() == 1) {
            char letter = correctAnswer.toUpperCase().charAt(0);
            int optionIndex = letter - 'A'; // Convert A->0, B->1, etc.
            if (optionIndex >= 0 && optionIndex < options.size()) {
                return options.get(optionIndex);
            }
        }
        
        return correctAnswer;
    }
    
    /**
     * Resets the question to unanswered state
     */
    public void resetAnswer() {
        this.userAnswer = null;
        this.answered = false;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return Objects.equals(questionText, question.questionText) &&
               Objects.equals(options, question.options) &&
               Objects.equals(correctAnswer, question.correctAnswer);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(questionText, options, correctAnswer);
    }
    
    @Override
    public String toString() {
        return "Question{" +
                "questionText='" + questionText + '\'' +
                ", options=" + options +
                ", correctAnswer='" + correctAnswer + '\'' +
                ", userAnswer='" + userAnswer + '\'' +
                ", answered=" + answered +
                '}';
    }
}
