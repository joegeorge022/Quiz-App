package com.quizmaster.service;

import com.quizmaster.model.Question;
import com.quizmaster.model.QuizSession;
import com.quizmaster.model.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Manages quiz operations including question generation, session management, and user interactions.
 * Coordinates between the API client, question parser, and quiz sessions.
 */
public class QuizManager {
    
    private final ApiClient apiClient;
    private final QuestionParser questionParser;
    private final ExecutorService executorService;
    private QuizSession currentSession;
    private User currentUser;
    
    /**
     * Constructor for QuizManager
     * @param apiKey The Groq API key for authentication
     */
    public QuizManager(String apiKey) {
        this.apiClient = new ApiClient(apiKey);
        this.questionParser = new QuestionParser();
        // Use cached thread pool for better performance
        this.executorService = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "QuizManager-Worker");
            t.setDaemon(true);
            return t;
        });
    }
    
    /**
     * Sets the current user for the quiz manager
     * @param user The current user
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            user.updateLastLoginTime();
        }
    }
    
    /**
     * Gets the current user
     * @return Current user or null if not set
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Gets the current quiz session
     * @return Current quiz session or null if not active
     */
    public QuizSession getCurrentSession() {
        return currentSession;
    }
    
    /**
     * Starts a new quiz session asynchronously with progress callback
     * @param topic The topic for the quiz
     * @param numberOfQuestions Number of questions to generate
     * @param progressCallback Optional callback for progress updates
     * @return CompletableFuture that completes when the quiz is ready
     */
    public CompletableFuture<QuizSession> startNewQuiz(String topic, int numberOfQuestions, Consumer<String> progressCallback) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (progressCallback != null) {
                    progressCallback.accept("Connecting to AI service...");
                }
                
                // Generate questions from API
                String apiResponse = apiClient.generateQuestions(topic, numberOfQuestions);
                
                if (progressCallback != null) {
                    progressCallback.accept("Processing quiz questions...");
                }
                
                // Parse questions
                List<Question> questions = questionParser.parseQuestions(apiResponse);
                
                if (progressCallback != null) {
                    progressCallback.accept("Quiz ready!");
                }
                
                // Create new quiz session
                currentSession = new QuizSession(topic, questions);
                
                return currentSession;
                
            } catch (ApiClient.ApiException e) {
                throw new QuizException("Failed to generate questions from API: " + e.getMessage(), e);
            } catch (QuestionParser.ParseException e) {
                throw new QuizException("Failed to parse questions: " + e.getMessage(), e);
            } catch (Exception e) {
                throw new QuizException("Unexpected error starting quiz: " + e.getMessage(), e);
            }
        }, executorService);
    }
    
    /**
     * Starts a new quiz session asynchronously (backward compatibility)
     * @param topic The topic for the quiz
     * @param numberOfQuestions Number of questions to generate
     * @return CompletableFuture that completes when the quiz is ready
     */
    public CompletableFuture<QuizSession> startNewQuiz(String topic, int numberOfQuestions) {
        return startNewQuiz(topic, numberOfQuestions, null);
    }
    
    /**
     * Submits an answer for the current question
     * @param answer The selected answer
     * @return true if the answer was submitted successfully
     */
    public boolean submitAnswer(String answer) {
        if (currentSession == null) {
            return false;
        }
        
        Question currentQuestion = currentSession.getCurrentQuestion();
        if (currentQuestion == null) {
            return false;
        }
        
        currentQuestion.setUserAnswer(answer);
        return true;
    }
    
    /**
     * Moves to the next question in the current session
     * @return true if there is a next question, false if quiz is complete
     */
    public boolean nextQuestion() {
        if (currentSession == null) {
            return false;
        }
        
        return currentSession.nextQuestion();
    }
    
    /**
     * Moves to the previous question in the current session
     * @return true if there is a previous question, false if at the beginning
     */
    public boolean previousQuestion() {
        if (currentSession == null) {
            return false;
        }
        
        return currentSession.previousQuestion();
    }
    
    /**
     * Completes the current quiz session and updates user statistics
     * @return The completed quiz session or null if no active session
     */
    public QuizSession completeQuiz() {
        if (currentSession == null) {
            return null;
        }
        
        currentSession.completeQuiz();
        
        // Update user statistics if user is set
        if (currentUser != null) {
            currentUser.addQuizSession(currentSession);
        }
        
        return currentSession;
    }
    
    /**
     * Resets the current quiz session to start over
     * @return true if the quiz was reset successfully
     */
    public boolean resetQuiz() {
        if (currentSession == null) {
            return false;
        }
        
        currentSession.resetQuiz();
        return true;
    }
    
    /**
     * Abandons the current quiz session
     */
    public void abandonQuiz() {
        currentSession = null;
    }
    
    /**
     * Checks if there is an active quiz session
     * @return true if there is an active session
     */
    public boolean hasActiveSession() {
        return currentSession != null;
    }
    
    /**
     * Gets the current question from the active session
     * @return Current question or null if no active session
     */
    public Question getCurrentQuestion() {
        if (currentSession == null) {
            return null;
        }
        
        return currentSession.getCurrentQuestion();
    }
    
    /**
     * Gets the progress information for the current session
     * @return Progress string in format "X of Y" or null if no active session
     */
    public String getProgressInfo() {
        if (currentSession == null) {
            return null;
        }
        
        int current = currentSession.getCurrentQuestionIndex() + 1;
        int total = currentSession.getTotalQuestions();
        return String.format("%d of %d", current, total);
    }
    
    /**
     * Gets the score information for the current session
     * @return Score string in format "X/Y (Z%)" or null if no active session
     */
    public String getScoreInfo() {
        if (currentSession == null) {
            return null;
        }
        
        int correct = currentSession.getCorrectAnswers();
        int total = currentSession.getTotalQuestions();
        double percentage = currentSession.getScorePercentage();
        
        return String.format("%d/%d (%.1f%%)", correct, total, percentage);
    }
    
    /**
     * Tests the API connection
     * @return CompletableFuture that completes with connection status
     */
    public CompletableFuture<Boolean> testApiConnection() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return apiClient.testConnection();
            } catch (Exception e) {
                return false;
            }
        }, executorService);
    }
    
    /**
     * Validates a topic string
     * @param topic The topic to validate
     * @return true if the topic is valid
     */
    public boolean isValidTopic(String topic) {
        return topic != null && 
               !topic.trim().isEmpty() && 
               topic.trim().length() >= 2 && 
               topic.trim().length() <= 100;
    }
    
    /**
     * Validates the number of questions
     * @param numberOfQuestions The number to validate
     * @return true if the number is valid
     */
    public boolean isValidQuestionCount(int numberOfQuestions) {
        return numberOfQuestions >= 1 && numberOfQuestions <= 20;
    }
    
    /**
     * Gets quiz statistics for the current user
     * @return Statistics string or null if no user set
     */
    public String getUserStatistics() {
        if (currentUser == null) {
            return null;
        }
        
        return String.format(
            "Quizzes Completed: %d | Questions Answered: %d | Overall Accuracy: %.1f%% | Best Score: %.1f%% | Average Score: %.1f%%",
            currentUser.getTotalQuizzesCompleted(),
            currentUser.getTotalQuestionsAnswered(),
            currentUser.getOverallAccuracy(),
            currentUser.getBestScore(),
            currentUser.getAverageScore()
        );
    }
    
    /**
     * Shuts down the quiz manager and releases resources
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        
        if (apiClient != null) {
            apiClient.close();
        }
    }
    
    /**
     * Custom exception class for quiz-related errors
     */
    public static class QuizException extends RuntimeException {
        
        public QuizException(String message) {
            super(message);
        }
        
        public QuizException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
