package com.quizmaster.gui;

import com.quizmaster.model.QuizSession;
import com.quizmaster.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Results screen panel for displaying quiz completion results and statistics.
 * Shows final score, performance metrics, and options for next actions.
 */
public class ResultsScreen extends JPanel {
    
    private final QuizGUI parentGUI;
    
    // UI Components
    private JLabel titleLabel;
    private JLabel scoreLabel;
    private JLabel percentageLabel;
    private JLabel timeLabel;
    private JLabel accuracyLabel;
    private JPanel statsPanel;
    private JButton reviewButton;
    private JButton newQuizButton;
    private JButton homeButton;
    
    /**
     * Constructor for ResultsScreen
     * @param parentGUI Reference to the main GUI controller
     */
    public ResultsScreen(QuizGUI parentGUI) {
        this.parentGUI = parentGUI;
        initializeComponents();
        layoutComponents();
        setupEventHandlers();
    }
    
    /**
     * Initializes all UI components
     */
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Title label
        titleLabel = new JLabel("Quiz Complete!", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(QuizGUI.getSuccessColor());
        
        // Score label
        scoreLabel = new JLabel("", JLabel.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setForeground(QuizGUI.getPrimaryColor());
        
        // Percentage label
        percentageLabel = new JLabel("", JLabel.CENTER);
        percentageLabel.setFont(new Font("Arial", Font.BOLD, 36));
        percentageLabel.setForeground(QuizGUI.getPrimaryColor());
        
        // Time label
        timeLabel = new JLabel("", JLabel.CENTER);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        timeLabel.setForeground(Color.GRAY);
        
        // Accuracy label
        accuracyLabel = new JLabel("", JLabel.CENTER);
        accuracyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        accuracyLabel.setForeground(Color.DARK_GRAY);
        
        // Stats panel
        statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        
        // Action buttons
        reviewButton = createActionButton("Review Answers", QuizGUI.getSecondaryColor());
        newQuizButton = createActionButton("Take Another Quiz", QuizGUI.getPrimaryColor());
        homeButton = createActionButton("Back to Home", Color.GRAY);
    }
    
    /**
     * Creates an action button with consistent styling
     * @param text Button text
     * @param color Button background color
     * @return Styled JButton
     */
    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(color.darker());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    /**
     * Layouts all components on the panel
     */
    private void layoutComponents() {
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        
        // Results panel
        JPanel resultsPanel = createResultsPanel();
        
        // Action panel
        JPanel actionPanel = createActionPanel();
        
        // Main layout
        add(headerPanel, BorderLayout.NORTH);
        add(resultsPanel, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
        
        // Add padding
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
    }
    
    /**
     * Creates the header panel with title and congratulations
     * @return JPanel containing header elements
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Congratulations icon (using text)
        JLabel iconLabel = new JLabel("🎉", JLabel.CENTER);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(iconLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(titleLabel);
        
        return panel;
    }
    
    /**
     * Creates the results panel with score and statistics
     * @return JPanel containing results information
     */
    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Score display panel
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));
        scorePanel.setBackground(Color.WHITE);
        scorePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        percentageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        scorePanel.add(percentageLabel);
        scorePanel.add(Box.createVerticalStrut(10));
        scorePanel.add(scoreLabel);
        scorePanel.add(Box.createVerticalStrut(15));
        scorePanel.add(timeLabel);
        
        // Add stats panel content
        updateStatsPanel();
        
        panel.add(scorePanel, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the action panel with control buttons
     * @return JPanel containing action buttons
     */
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        panel.add(reviewButton);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(newQuizButton);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(homeButton);
        
        return panel;
    }
    
    /**
     * Updates the statistics panel with current quiz and user data
     */
    private void updateStatsPanel() {
        statsPanel.removeAll();
        
        QuizSession session = parentGUI.getQuizManager().getCurrentSession();
        User user = parentGUI.getQuizManager().getCurrentUser();
        
        if (session == null) {
            return;
        }
        
        // Quiz statistics
        addStatistic("Topic", session.getTopic());
        addStatistic("Questions Answered", session.getAnsweredQuestions() + " of " + session.getTotalQuestions());
        addStatistic("Correct Answers", String.valueOf(session.getCorrectAnswers()));
        addStatistic("Incorrect Answers", String.valueOf(session.getIncorrectAnswers()));
        addStatistic("Quiz Duration", session.getFormattedDuration());
        
        // Add separator
        statsPanel.add(Box.createVerticalStrut(20));
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        statsPanel.add(separator);
        statsPanel.add(Box.createVerticalStrut(20));
        
        // User overall statistics (if available)
        if (user != null) {
            JLabel userStatsLabel = new JLabel("Your Overall Statistics");
            userStatsLabel.setFont(new Font("Arial", Font.BOLD, 16));
            userStatsLabel.setForeground(QuizGUI.getPrimaryColor());
            userStatsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            statsPanel.add(userStatsLabel);
            statsPanel.add(Box.createVerticalStrut(15));
            
            addStatistic("Total Quizzes Completed", String.valueOf(user.getTotalQuizzesCompleted()));
            addStatistic("Total Questions Answered", String.valueOf(user.getTotalQuestionsAnswered()));
            addStatistic("Overall Accuracy", String.format("%.1f%%", user.getOverallAccuracy()));
            addStatistic("Best Score", String.format("%.1f%%", user.getBestScore()));
            addStatistic("Average Score", String.format("%.1f%%", user.getAverageScore()));
        }
        
        statsPanel.revalidate();
        statsPanel.repaint();
    }
    
    /**
     * Adds a statistic row to the stats panel
     * @param label Statistic label
     * @param value Statistic value
     */
    private void addStatistic(String label, String value) {
        JPanel statPanel = new JPanel(new BorderLayout());
        statPanel.setBackground(Color.WHITE);
        statPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        
        JLabel labelComponent = new JLabel(label + ":");
        labelComponent.setFont(new Font("Arial", Font.PLAIN, 14));
        labelComponent.setForeground(Color.DARK_GRAY);
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.BOLD, 14));
        valueComponent.setForeground(QuizGUI.getPrimaryColor());
        
        statPanel.add(labelComponent, BorderLayout.WEST);
        statPanel.add(valueComponent, BorderLayout.EAST);
        
        statsPanel.add(statPanel);
        statsPanel.add(Box.createVerticalStrut(8));
    }
    
    /**
     * Sets up event handlers for UI components
     */
    private void setupEventHandlers() {
        reviewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentGUI.showReviewScreen();
            }
        });
        
        newQuizButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentGUI.showHomeScreen();
            }
        });
        
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentGUI.showHomeScreen();
            }
        });
    }
    
    /**
     * Updates the display with current quiz results
     */
    private void updateDisplay() {
        QuizSession session = parentGUI.getQuizManager().getCurrentSession();
        if (session == null) {
            return;
        }
        
        // Update score information
        int correct = session.getCorrectAnswers();
        int total = session.getTotalQuestions();
        double percentage = session.getScorePercentage();
        
        percentageLabel.setText(String.format("%.1f%%", percentage));
        scoreLabel.setText(String.format("You scored %d out of %d", correct, total));
        timeLabel.setText("Completed in " + session.getFormattedDuration());
        
        // Set color based on performance
        Color scoreColor;
        if (percentage >= 80) {
            scoreColor = QuizGUI.getSuccessColor();
            titleLabel.setText("Excellent Work! 🌟");
        } else if (percentage >= 60) {
            scoreColor = QuizGUI.getPrimaryColor();
            titleLabel.setText("Good Job! 👍");
        } else {
            scoreColor = QuizGUI.getErrorColor();
            titleLabel.setText("Keep Practicing! 📚");
        }
        
        percentageLabel.setForeground(scoreColor);
        scoreLabel.setForeground(scoreColor);
        
        // Update statistics
        updateStatsPanel();
    }
    
    /**
     * Refreshes the screen (called when navigating to this screen)
     */
    public void refreshScreen() {
        updateDisplay();
    }
}
