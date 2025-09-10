package com.quizmaster.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a user of the Quiz Master application.
 * Stores user information and quiz session history.
 */
public class User {
    
    private String name;
    private LocalDateTime lastLoginTime;
    private List<QuizSession> quizHistory;
    private int totalQuizzesCompleted;
    private int totalQuestionsAnswered;
    private int totalCorrectAnswers;
    
    /**
     * Default constructor
     */
    public User() {
        this.quizHistory = new ArrayList<>();
        this.totalQuizzesCompleted = 0;
        this.totalQuestionsAnswered = 0;
        this.totalCorrectAnswers = 0;
    }
    
    /**
     * Constructor with user name
     * @param name The user's name
     */
    public User(String name) {
        this();
        this.name = name;
        this.lastLoginTime = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }
    
    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
    
    public List<QuizSession> getQuizHistory() {
        return quizHistory;
    }
    
    public void setQuizHistory(List<QuizSession> quizHistory) {
        this.quizHistory = quizHistory;
    }
    
    public int getTotalQuizzesCompleted() {
        return totalQuizzesCompleted;
    }
    
    public void setTotalQuizzesCompleted(int totalQuizzesCompleted) {
        this.totalQuizzesCompleted = totalQuizzesCompleted;
    }
    
    public int getTotalQuestionsAnswered() {
        return totalQuestionsAnswered;
    }
    
    public void setTotalQuestionsAnswered(int totalQuestionsAnswered) {
        this.totalQuestionsAnswered = totalQuestionsAnswered;
    }
    
    public int getTotalCorrectAnswers() {
        return totalCorrectAnswers;
    }
    
    public void setTotalCorrectAnswers(int totalCorrectAnswers) {
        this.totalCorrectAnswers = totalCorrectAnswers;
    }
    
    /**
     * Adds a completed quiz session to the user's history
     * @param session The completed quiz session
     */
    public void addQuizSession(QuizSession session) {
        if (session != null && session.isCompleted()) {
            quizHistory.add(session);
            totalQuizzesCompleted++;
            totalQuestionsAnswered += session.getTotalQuestions();
            totalCorrectAnswers += session.getCorrectAnswers();
        }
    }
    
    /**
     * Calculates the user's overall accuracy percentage
     * @return Accuracy percentage as a double (0.0 to 100.0)
     */
    public double getOverallAccuracy() {
        if (totalQuestionsAnswered == 0) {
            return 0.0;
        }
        return (double) totalCorrectAnswers / totalQuestionsAnswered * 100.0;
    }
    
    /**
     * Gets the user's best quiz score
     * @return The highest score achieved, or 0.0 if no quizzes completed
     */
    public double getBestScore() {
        return quizHistory.stream()
                .mapToDouble(QuizSession::getScorePercentage)
                .max()
                .orElse(0.0);
    }
    
    /**
     * Gets the user's average quiz score
     * @return The average score across all quizzes, or 0.0 if no quizzes completed
     */
    public double getAverageScore() {
        if (quizHistory.isEmpty()) {
            return 0.0;
        }
        return quizHistory.stream()
                .mapToDouble(QuizSession::getScorePercentage)
                .average()
                .orElse(0.0);
    }
    
    /**
     * Updates the user's last login time to now
     */
    public void updateLastLoginTime() {
        this.lastLoginTime = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", totalQuizzesCompleted=" + totalQuizzesCompleted +
                ", totalQuestionsAnswered=" + totalQuestionsAnswered +
                ", totalCorrectAnswers=" + totalCorrectAnswers +
                ", overallAccuracy=" + String.format("%.1f", getOverallAccuracy()) + "%" +
                '}';
    }
}
