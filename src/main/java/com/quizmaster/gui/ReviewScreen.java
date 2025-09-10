package com.quizmaster.gui;

import com.quizmaster.model.Question;
import com.quizmaster.model.QuizSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Review screen panel for examining all quiz questions, answers, and explanations.
 * Allows users to review their performance and learn from mistakes.
 */
public class ReviewScreen extends JPanel {
    
    private final QuizGUI parentGUI;
    
    // UI Components
    private JLabel titleLabel;
    private JLabel questionCountLabel;
    private JScrollPane scrollPane;
    private JPanel questionsPanel;
    private JButton backToResultsButton;
    private JButton newQuizButton;
    private JButton homeButton;
    
    /**
     * Constructor for ReviewScreen
     * @param parentGUI Reference to the main GUI controller
     */
    public ReviewScreen(QuizGUI parentGUI) {
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
        titleLabel = new JLabel("Review Your Answers", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(QuizGUI.getPrimaryColor());
        
        // Question count label
        questionCountLabel = new JLabel("", JLabel.CENTER);
        questionCountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        questionCountLabel.setForeground(Color.GRAY);
        
        // Questions panel (will be populated dynamically)
        questionsPanel = new JPanel();
        questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));
        questionsPanel.setBackground(Color.WHITE);
        questionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Scroll pane for questions
        scrollPane = new JScrollPane(questionsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Action buttons
        backToResultsButton = createActionButton("← Back to Results", QuizGUI.getSecondaryColor());
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
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
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
        
        // Action panel
        JPanel actionPanel = createActionPanel();
        
        // Main layout
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
        
        // Add padding
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }
    
    /**
     * Creates the header panel with title and summary
     * @return JPanel containing header elements
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        questionCountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(questionCountLabel);
        
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
        
        panel.add(backToResultsButton);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(newQuizButton);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(homeButton);
        
        return panel;
    }
    
    /**
     * Sets up event handlers for UI components
     */
    private void setupEventHandlers() {
        backToResultsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentGUI.showResultsScreen();
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
     * Updates the display with all questions and answers
     */
    private void updateDisplay() {
        QuizSession session = parentGUI.getQuizManager().getCurrentSession();
        if (session == null) {
            return;
        }
        
        // Update header information
        List<Question> questions = session.getQuestions();
        int correct = session.getCorrectAnswers();
        int total = questions.size();
        
        questionCountLabel.setText(String.format("Review all %d questions • %d correct • %d incorrect", 
                                                 total, correct, total - correct));
        
        // Clear existing questions
        questionsPanel.removeAll();
        
        // Add each question as a review panel
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            JPanel questionPanel = createQuestionReviewPanel(question, i + 1);
            questionsPanel.add(questionPanel);
            
            // Add spacing between questions
            if (i < questions.size() - 1) {
                questionsPanel.add(Box.createVerticalStrut(20));
            }
        }
        
        // Refresh the display
        questionsPanel.revalidate();
        questionsPanel.repaint();
        
        // Scroll to top
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
    }
    
    /**
     * Creates a review panel for a single question
     * @param question The question to review
     * @param questionNumber The question number (1-based)
     * @return JPanel containing the question review
     */
    private JPanel createQuestionReviewPanel(Question question, int questionNumber) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(question.isCorrect() ? QuizGUI.getSuccessColor() : QuizGUI.getErrorColor(), 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Question header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        JLabel questionNumberLabel = new JLabel("Question " + questionNumber);
        questionNumberLabel.setFont(new Font("Arial", Font.BOLD, 16));
        questionNumberLabel.setForeground(QuizGUI.getPrimaryColor());
        
        JLabel statusLabel = new JLabel(question.isCorrect() ? "✓ Correct" : "✗ Incorrect");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(question.isCorrect() ? QuizGUI.getSuccessColor() : QuizGUI.getErrorColor());
        
        headerPanel.add(questionNumberLabel, BorderLayout.WEST);
        headerPanel.add(statusLabel, BorderLayout.EAST);
        
        panel.add(headerPanel);
        panel.add(Box.createVerticalStrut(15));
        
        // Question text
        JLabel questionTextLabel = new JLabel("<html><div style='width: 650px;'>" + question.getQuestionText() + "</div></html>");
        questionTextLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        questionTextLabel.setForeground(Color.BLACK);
        panel.add(questionTextLabel);
        panel.add(Box.createVerticalStrut(15));
        
        // Answer options
        List<String> options = question.getOptions();
        for (String option : options) {
            JLabel optionLabel = createOptionLabel(option, question);
            panel.add(optionLabel);
            panel.add(Box.createVerticalStrut(5));
        }
        
        panel.add(Box.createVerticalStrut(10));
        
        // Answer summary
        JPanel summaryPanel = createAnswerSummaryPanel(question);
        panel.add(summaryPanel);
        
        // Explanation
        if (question.getExplanation() != null && !question.getExplanation().trim().isEmpty()) {
            panel.add(Box.createVerticalStrut(15));
            JPanel explanationPanel = createExplanationPanel(question.getExplanation());
            panel.add(explanationPanel);
        }
        
        return panel;
    }
    
    /**
     * Creates a label for an answer option with appropriate styling
     * @param option The option text
     * @param question The question containing this option
     * @return Styled JLabel for the option
     */
    private JLabel createOptionLabel(String option, Question question) {
        boolean isCorrect = option.equals(question.getCorrectAnswer());
        boolean isUserAnswer = option.equals(question.getUserAnswer());
        
        String prefix = "○ ";
        Color textColor = Color.DARK_GRAY;
        Font font = new Font("Arial", Font.PLAIN, 14);
        
        if (isCorrect) {
            prefix = "✓ ";
            textColor = QuizGUI.getSuccessColor();
            font = new Font("Arial", Font.BOLD, 14);
        } else if (isUserAnswer) {
            prefix = "✗ ";
            textColor = QuizGUI.getErrorColor();
            font = new Font("Arial", Font.BOLD, 14);
        }
        
        JLabel label = new JLabel(prefix + option);
        label.setFont(font);
        label.setForeground(textColor);
        label.setBorder(BorderFactory.createEmptyBorder(3, 20, 3, 0));
        
        return label;
    }
    
    /**
     * Creates a summary panel showing the user's answer vs correct answer
     * @param question The question to summarize
     * @return JPanel containing the answer summary
     */
    private JPanel createAnswerSummaryPanel(Question question) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Your answer
        JLabel yourAnswerLabel = new JLabel("Your Answer: " + (question.getUserAnswer() != null ? question.getUserAnswer() : "Not answered"));
        yourAnswerLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        yourAnswerLabel.setForeground(question.isCorrect() ? QuizGUI.getSuccessColor() : QuizGUI.getErrorColor());
        
        // Correct answer
        JLabel correctAnswerLabel = new JLabel("Correct Answer: " + question.getCorrectAnswer());
        correctAnswerLabel.setFont(new Font("Arial", Font.BOLD, 13));
        correctAnswerLabel.setForeground(QuizGUI.getSuccessColor());
        
        panel.add(yourAnswerLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(correctAnswerLabel);
        
        return panel;
    }
    
    /**
     * Creates an explanation panel for the question
     * @param explanation The explanation text
     * @return JPanel containing the explanation
     */
    private JPanel createExplanationPanel(String explanation) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(QuizGUI.getPrimaryColor(), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("💡 Explanation");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(QuizGUI.getPrimaryColor());
        
        JLabel explanationLabel = new JLabel("<html><div style='width: 600px;'>" + explanation + "</div></html>");
        explanationLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        explanationLabel.setForeground(Color.DARK_GRAY);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(8), BorderLayout.CENTER);
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(new Color(240, 248, 255));
        textPanel.add(explanationLabel, BorderLayout.NORTH);
        panel.add(textPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Refreshes the screen (called when navigating to this screen)
     */
    public void refreshScreen() {
        updateDisplay();
    }
}
