package com.quizmaster.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Home screen panel for the AI QuizMaster application.
 * Allows users to enter their name, select a topic, and configure quiz settings.
 */
public class HomeScreen extends JPanel {
    
    private final QuizGUI parentGUI;
    
    // UI Components
    private JTextField nameField;
    private JTextField topicField;
    private JSpinner questionCountSpinner;
    private JButton startQuizButton;
    private JLabel statusLabel;
    
    /**
     * Constructor for HomeScreen
     * @param parentGUI Reference to the main GUI controller
     */
    public HomeScreen(QuizGUI parentGUI) {
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
        
        // Name field
        nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Topic field
        topicField = new JTextField(20);
        topicField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Question count spinner
        questionCountSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 20, 1));
        questionCountSpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Start quiz button
        startQuizButton = new JButton("Start Quiz");
        startQuizButton.setFont(new Font("Arial", Font.BOLD, 16));
        startQuizButton.setBackground(QuizGUI.getPrimaryColor());
        startQuizButton.setForeground(Color.WHITE);
        startQuizButton.setFocusPainted(false);
        startQuizButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        startQuizButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setForeground(QuizGUI.getErrorColor());
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
    }
    
    /**
     * Layouts all components on the panel
     */
    private void layoutComponents() {
        // Title panel
        JPanel titlePanel = createTitlePanel();
        
        // Form panel
        JPanel formPanel = createFormPanel();
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        
        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout());
        statusPanel.setBackground(Color.WHITE);
        statusPanel.add(statusLabel);
        
        // Main layout
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add some padding
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
    }
    
    /**
     * Creates the title panel
     * @return JPanel containing the title and description
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Main title
        JLabel titleLabel = new JLabel("AI QuizMaster");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(QuizGUI.getPrimaryColor());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Generate personalized quizzes with AI");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Description
        JLabel descLabel = new JLabel("<html><center>Enter a topic of your choice and let AI generate engaging quiz questions for you.<br>Perfect for learning, testing knowledge, or just having fun!</center></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descLabel.setForeground(Color.DARK_GRAY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(subtitleLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(descLabel);
        
        return panel;
    }
    
    /**
     * Creates the form panel with input fields
     * @return JPanel containing the form
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Name field
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Your Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(nameLabel, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(nameField, gbc);
        
        // Topic field
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        JLabel topicLabel = new JLabel("Quiz Topic:");
        topicLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(topicLabel, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(topicField, gbc);
        
        // Add topic examples
        gbc.gridx = 1; gbc.gridy = 2;
        JLabel examplesLabel = new JLabel("<html><i>Examples: Java Programming, World History, Biology, Mathematics, etc.</i></html>");
        examplesLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        examplesLabel.setForeground(Color.GRAY);
        panel.add(examplesLabel, gbc);
        
        // Question count
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        JLabel countLabel = new JLabel("Number of Questions:");
        countLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(countLabel, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(questionCountSpinner, gbc);
        
        return panel;
    }
    
    /**
     * Creates the button panel
     * @return JPanel containing action buttons
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        panel.add(startQuizButton);
        panel.add(statusLabel);
        
        return panel;
    }
    
    /**
     * Sets up event handlers for UI components
     */
    private void setupEventHandlers() {
        startQuizButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleStartQuiz();
            }
        });
        
        // Enter key support for text fields
        ActionListener enterKeyListener = e -> handleStartQuiz();
        nameField.addActionListener(enterKeyListener);
        topicField.addActionListener(enterKeyListener);
        
        // Add hover effects to button
        startQuizButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                startQuizButton.setBackground(QuizGUI.getSecondaryColor());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                startQuizButton.setBackground(QuizGUI.getPrimaryColor());
            }
        });
    }
    
    /**
     * Handles the start quiz button click
     */
    private void handleStartQuiz() {
        // Clear previous status
        statusLabel.setText(" ");
        
        // Get input values
        String name = nameField.getText().trim();
        String topic = topicField.getText().trim();
        int questionCount = (Integer) questionCountSpinner.getValue();
        
        // Validate inputs
        if (!validateInputs(name, topic, questionCount)) {
            return;
        }
        
        // Start the quiz
        parentGUI.startNewQuiz(topic, questionCount, name);
    }
    
    /**
     * Validates user inputs
     * @param name User's name
     * @param topic Quiz topic
     * @param questionCount Number of questions
     * @return true if all inputs are valid
     */
    private boolean validateInputs(String name, String topic, int questionCount) {
        if (name.isEmpty()) {
            showError("Please enter your name.");
            nameField.requestFocus();
            return false;
        }
        
        if (name.length() < 2) {
            showError("Name must be at least 2 characters long.");
            nameField.requestFocus();
            return false;
        }
        
        if (topic.isEmpty()) {
            showError("Please enter a quiz topic.");
            topicField.requestFocus();
            return false;
        }
        
        if (!parentGUI.getQuizManager().isValidTopic(topic)) {
            showError("Topic must be between 2 and 100 characters long.");
            topicField.requestFocus();
            return false;
        }
        
        if (!parentGUI.getQuizManager().isValidQuestionCount(questionCount)) {
            showError("Number of questions must be between 1 and 20.");
            questionCountSpinner.requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * Shows an error message in the status label
     * @param message Error message to display
     */
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(QuizGUI.getErrorColor());
    }
    
    /**
     * Refreshes the screen (called when navigating to this screen)
     */
    public void refreshScreen() {
        // Clear any previous error messages
        statusLabel.setText(" ");
        
        // Focus on the name field if it's empty
        if (nameField.getText().trim().isEmpty()) {
            nameField.requestFocus();
        } else if (topicField.getText().trim().isEmpty()) {
            topicField.requestFocus();
        }
    }
    
    /**
     * Clears all input fields
     */
    public void clearFields() {
        nameField.setText("");
        topicField.setText("");
        questionCountSpinner.setValue(5);
        statusLabel.setText(" ");
    }
}
