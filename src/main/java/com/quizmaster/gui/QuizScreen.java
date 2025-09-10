package com.quizmaster.gui;

import com.quizmaster.model.Question;
import com.quizmaster.model.QuizSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Quiz screen panel for displaying questions and handling user interactions during the quiz.
 * Shows the current question, answer options, and navigation controls.
 */
public class QuizScreen extends JPanel {
    
    private final QuizGUI parentGUI;
    
    // UI Components
    private JLabel questionLabel;
    private ButtonGroup answerButtonGroup;
    private JRadioButton[] answerButtons;
    private JButton previousButton;
    private JButton nextButton;
    private JButton finishButton;
    private JLabel progressLabel;
    private JLabel topicLabel;
    private JPanel answerPanel;
    
    /**
     * Constructor for QuizScreen
     * @param parentGUI Reference to the main GUI controller
     */
    public QuizScreen(QuizGUI parentGUI) {
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
        
        // Topic label
        topicLabel = new JLabel();
        topicLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topicLabel.setForeground(QuizGUI.getPrimaryColor());
        topicLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Progress label
        progressLabel = new JLabel();
        progressLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        progressLabel.setForeground(Color.GRAY);
        progressLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Question label
        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        questionLabel.setVerticalAlignment(JLabel.TOP);
        questionLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Answer panel
        answerPanel = new JPanel();
        answerPanel.setLayout(new BoxLayout(answerPanel, BoxLayout.Y_AXIS));
        answerPanel.setBackground(Color.WHITE);
        answerPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));
        
        // Answer button group
        answerButtonGroup = new ButtonGroup();
        answerButtons = new JRadioButton[4]; // Maximum 4 options
        
        for (int i = 0; i < answerButtons.length; i++) {
            answerButtons[i] = new JRadioButton();
            answerButtons[i].setFont(new Font("Arial", Font.PLAIN, 14));
            answerButtons[i].setBackground(Color.WHITE);
            answerButtons[i].setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            answerButtons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            answerButtonGroup.add(answerButtons[i]);
            answerPanel.add(answerButtons[i]);
            answerPanel.add(Box.createVerticalStrut(5));
        }
        
        // Navigation buttons
        previousButton = createNavigationButton("← Previous");
        nextButton = createNavigationButton("Next →");
        finishButton = createNavigationButton("Finish Quiz");
        
        finishButton.setBackground(QuizGUI.getSuccessColor());
        finishButton.setVisible(false);
    }
    
    /**
     * Creates a navigation button with consistent styling
     * @param text Button text
     * @return Styled JButton
     */
    private JButton createNavigationButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(QuizGUI.getPrimaryColor());
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(QuizGUI.getSecondaryColor());
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (button == finishButton) {
                    button.setBackground(QuizGUI.getSuccessColor());
                } else {
                    button.setBackground(QuizGUI.getPrimaryColor());
                }
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
        
        // Question panel
        JPanel questionPanel = createQuestionPanel();
        
        // Navigation panel
        JPanel navigationPanel = createNavigationPanel();
        
        // Main layout
        add(headerPanel, BorderLayout.NORTH);
        add(questionPanel, BorderLayout.CENTER);
        add(navigationPanel, BorderLayout.SOUTH);
        
        // Add padding
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }
    
    /**
     * Creates the header panel with topic and progress information
     * @return JPanel containing header elements
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        panel.add(topicLabel, BorderLayout.CENTER);
        panel.add(progressLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Creates the question panel with question text and answer options
     * @return JPanel containing question elements
     */
    private JPanel createQuestionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        
        // Question text in a scroll pane
        JScrollPane questionScrollPane = new JScrollPane(questionLabel);
        questionScrollPane.setBorder(null);
        questionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        questionScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        questionScrollPane.setPreferredSize(new Dimension(0, 120));
        
        panel.add(questionScrollPane, BorderLayout.NORTH);
        panel.add(answerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the navigation panel with control buttons
     * @return JPanel containing navigation buttons
     */
    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        panel.add(previousButton);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(nextButton);
        panel.add(finishButton);
        
        return panel;
    }
    
    /**
     * Sets up event handlers for UI components
     */
    private void setupEventHandlers() {
        previousButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handlePreviousQuestion();
            }
        });
        
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleNextQuestion();
            }
        });
        
        finishButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleFinishQuiz();
            }
        });
        
        // Add action listeners to answer buttons
        for (int i = 0; i < answerButtons.length; i++) {
            final int index = i;
            answerButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleAnswerSelection(index);
                }
            });
        }
    }
    
    /**
     * Handles previous question navigation
     */
    private void handlePreviousQuestion() {
        saveCurrentAnswer();
        parentGUI.previousQuestion();
    }
    
    /**
     * Handles next question navigation
     */
    private void handleNextQuestion() {
        saveCurrentAnswer();
        parentGUI.nextQuestion();
    }
    
    /**
     * Handles finish quiz action
     */
    private void handleFinishQuiz() {
        saveCurrentAnswer();
        
        QuizSession session = parentGUI.getQuizManager().getCurrentSession();
        if (session != null && !session.areAllQuestionsAnswered()) {
            boolean confirmed = parentGUI.showConfirmDialog("Finish Quiz", 
                "You haven't answered all questions. Are you sure you want to finish?");
            if (!confirmed) {
                return;
            }
        }
        
        parentGUI.completeQuiz();
    }
    
    /**
     * Handles answer selection
     * @param optionIndex Index of the selected option
     */
    private void handleAnswerSelection(int optionIndex) {
        Question currentQuestion = parentGUI.getQuizManager().getCurrentQuestion();
        if (currentQuestion != null && optionIndex < currentQuestion.getOptions().size()) {
            String selectedAnswer = currentQuestion.getOptions().get(optionIndex);
            parentGUI.submitAnswer(selectedAnswer);
        }
    }
    
    /**
     * Saves the current answer selection
     */
    private void saveCurrentAnswer() {
        for (int i = 0; i < answerButtons.length; i++) {
            if (answerButtons[i].isSelected()) {
                handleAnswerSelection(i);
                break;
            }
        }
    }
    
    /**
     * Updates the display with current question information
     */
    public void updateDisplay() {
        QuizSession session = parentGUI.getQuizManager().getCurrentSession();
        if (session == null) {
            return;
        }
        
        Question currentQuestion = session.getCurrentQuestion();
        if (currentQuestion == null) {
            return;
        }
        
        // Update topic and progress
        topicLabel.setText("Quiz Topic: " + session.getTopic());
        progressLabel.setText("Question " + (session.getCurrentQuestionIndex() + 1) + 
                             " of " + session.getTotalQuestions());
        
        // Update question text
        questionLabel.setText("<html><div style='width: 700px;'>" + 
                             currentQuestion.getQuestionText() + "</div></html>");
        
        // Update answer options
        List<String> options = currentQuestion.getOptions();
        for (int i = 0; i < answerButtons.length; i++) {
            if (i < options.size()) {
                answerButtons[i].setText(options.get(i));
                answerButtons[i].setVisible(true);
                
                // Check if this option was previously selected
                if (currentQuestion.isAnswered() && 
                    options.get(i).equals(currentQuestion.getUserAnswer())) {
                    answerButtons[i].setSelected(true);
                }
            } else {
                answerButtons[i].setVisible(false);
            }
        }
        
        // Clear selection if question not answered
        if (!currentQuestion.isAnswered()) {
            answerButtonGroup.clearSelection();
        }
        
        // Update navigation buttons
        previousButton.setEnabled(session.hasPreviousQuestion());
        
        if (session.hasNextQuestion()) {
            nextButton.setVisible(true);
            finishButton.setVisible(false);
        } else {
            nextButton.setVisible(false);
            finishButton.setVisible(true);
        }
    }
    
    /**
     * Refreshes the screen (called when navigating to this screen)
     */
    public void refreshScreen() {
        updateDisplay();
    }
}
