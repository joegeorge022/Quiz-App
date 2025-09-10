package com.quizmaster.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a quiz session containing questions, user progress, and scoring information.
 * Manages the state of an active or completed quiz.
 */
public class QuizSession {
    
    private String topic;
    private List<Question> questions;
    private int currentQuestionIndex;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean completed;
    private long durationInSeconds;
    
    /**
     * Default constructor
     */
    public QuizSession() {
        this.questions = new ArrayList<>();
        this.currentQuestionIndex = 0;
        this.completed = false;
        this.startTime = LocalDateTime.now();
    }
    
    /**
     * Constructor with topic
     * @param topic The quiz topic
     */
    public QuizSession(String topic) {
        this();
        this.topic = topic;
    }
    
    /**
     * Constructor with topic and questions
     * @param topic The quiz topic
     * @param questions List of questions for the quiz
     */
    public QuizSession(String topic, List<Question> questions) {
        this(topic);
        this.questions = new ArrayList<>(questions);
    }
    
    // Getters and Setters
    public String getTopic() {
        return topic;
    }
    
    public void setTopic(String topic) {
        this.topic = topic;
    }
    
    public List<Question> getQuestions() {
        return questions;
    }
    
    public void setQuestions(List<Question> questions) {
        this.questions = questions != null ? new ArrayList<>(questions) : new ArrayList<>();
    }
    
    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }
    
    public void setCurrentQuestionIndex(int currentQuestionIndex) {
        this.currentQuestionIndex = currentQuestionIndex;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    
    public long getDurationInSeconds() {
        return durationInSeconds;
    }
    
    public void setDurationInSeconds(long durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }
    
    /**
     * Gets the current question being answered
     * @return Current question or null if no more questions
     */
    public Question getCurrentQuestion() {
        if (currentQuestionIndex >= 0 && currentQuestionIndex < questions.size()) {
            return questions.get(currentQuestionIndex);
        }
        return null;
    }
    
    /**
     * Moves to the next question
     * @return true if there is a next question, false if quiz is complete
     */
    public boolean nextQuestion() {
        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            return true;
        }
        return false;
    }
    
    /**
     * Moves to the previous question
     * @return true if there is a previous question, false if at the beginning
     */
    public boolean previousQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            return true;
        }
        return false;
    }
    
    /**
     * Checks if there are more questions after the current one
     * @return true if there are more questions
     */
    public boolean hasNextQuestion() {
        return currentQuestionIndex < questions.size() - 1;
    }
    
    /**
     * Checks if there are questions before the current one
     * @return true if there are previous questions
     */
    public boolean hasPreviousQuestion() {
        return currentQuestionIndex > 0;
    }
    
    /**
     * Gets the total number of questions in the quiz
     * @return Total number of questions
     */
    public int getTotalQuestions() {
        return questions.size();
    }
    
    /**
     * Gets the number of questions answered
     * @return Number of answered questions
     */
    public int getAnsweredQuestions() {
        return (int) questions.stream().mapToLong(q -> q.isAnswered() ? 1 : 0).sum();
    }
    
    /**
     * Gets the number of correct answers
     * @return Number of correct answers
     */
    public int getCorrectAnswers() {
        return (int) questions.stream().mapToLong(q -> q.isCorrect() ? 1 : 0).sum();
    }
    
    /**
     * Gets the number of incorrect answers
     * @return Number of incorrect answers
     */
    public int getIncorrectAnswers() {
        return (int) questions.stream().mapToLong(q -> q.isAnswered() && !q.isCorrect() ? 1 : 0).sum();
    }
    
    /**
     * Calculates the score percentage
     * @return Score as a percentage (0.0 to 100.0)
     */
    public double getScorePercentage() {
        if (questions.isEmpty()) {
            return 0.0;
        }
        return (double) getCorrectAnswers() / questions.size() * 100.0;
    }
    
    /**
     * Checks if all questions have been answered
     * @return true if all questions are answered
     */
    public boolean areAllQuestionsAnswered() {
        return questions.stream().allMatch(Question::isAnswered);
    }
    
    /**
     * Completes the quiz session and calculates duration
     */
    public void completeQuiz() {
        if (!completed) {
            this.endTime = LocalDateTime.now();
            this.completed = true;
            if (startTime != null) {
                this.durationInSeconds = java.time.Duration.between(startTime, endTime).getSeconds();
            }
        }
    }
    
    /**
     * Resets the quiz session to start over
     */
    public void resetQuiz() {
        this.currentQuestionIndex = 0;
        this.completed = false;
        this.endTime = null;
        this.durationInSeconds = 0;
        this.startTime = LocalDateTime.now();
        questions.forEach(Question::resetAnswer);
    }
    
    /**
     * Gets a formatted duration string
     * @return Duration in MM:SS format
     */
    public String getFormattedDuration() {
        long minutes = durationInSeconds / 60;
        long seconds = durationInSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    /**
     * Gets a formatted start time string
     * @return Start time in readable format
     */
    public String getFormattedStartTime() {
        if (startTime == null) {
            return "N/A";
        }
        return startTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuizSession that = (QuizSession) o;
        return Objects.equals(topic, that.topic) &&
               Objects.equals(startTime, that.startTime);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(topic, startTime);
    }
    
    @Override
    public String toString() {
        return "QuizSession{" +
                "topic='" + topic + '\'' +
                ", totalQuestions=" + getTotalQuestions() +
                ", answeredQuestions=" + getAnsweredQuestions() +
                ", correctAnswers=" + getCorrectAnswers() +
                ", scorePercentage=" + String.format("%.1f", getScorePercentage()) + "%" +
                ", completed=" + completed +
                '}';
    }
}
