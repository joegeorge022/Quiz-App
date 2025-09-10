package com.quizmaster.gui;

import com.quizmaster.model.Question;
import com.quizmaster.model.QuizSession;
import com.quizmaster.model.User;
import com.quizmaster.service.QuizManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;

/**
 * Main GUI controller for the AI QuizMaster application.
 * Manages different screens and coordinates user interactions with the quiz system.
 */
public class QuizGUI extends JFrame {
    
    private static final String APP_TITLE = "AI QuizMaster";
    private static final Dimension WINDOW_SIZE = new Dimension(800, 600);
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color ERROR_COLOR = new Color(231, 76, 60);
    
    private final QuizManager quizManager;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    
    // Screen panels
    private HomeScreen homeScreen;
    private QuizScreen quizScreen;
    private ResultsScreen resultsScreen;
    private ReviewScreen reviewScreen;
    
    // Screen identifiers
    private static final String HOME_SCREEN = "HOME";
    private static final String QUIZ_SCREEN = "QUIZ";
    private static final String RESULTS_SCREEN = "RESULTS";
    private static final String REVIEW_SCREEN = "REVIEW";
    
    private JDialog loadingDialog;
    private JLabel loadingLabel;
    
    /**
     * Constructor for QuizGUI
     * @param apiKey The Groq API key for the quiz manager
     */
    public QuizGUI(String apiKey) {
        this.quizManager = new QuizManager(apiKey);
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);
        
        initializeGUI();
        createScreens();
        setupEventHandlers();
        
        // Show home screen initially
        showHomeScreen();
    }
    
    /**
     * Initializes the main GUI components and settings
     */
    private void initializeGUI() {
        setTitle(APP_TITLE);
        setSize(WINDOW_SIZE);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Set application icon (if available)
        try {
            setIconImage(createAppIcon());
        } catch (Exception e) {
            // Icon not critical, continue without it
        }
        
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel
        }
        
        // Add main panel
        add(mainPanel, BorderLayout.CENTER);
        
        // Create menu bar
        setJMenuBar(createMenuBar());
    }
    
    /**
     * Creates all screen panels
     */
    private void createScreens() {
        homeScreen = new HomeScreen(this);
        quizScreen = new QuizScreen(this);
        resultsScreen = new ResultsScreen(this);
        reviewScreen = new ReviewScreen(this);
        
        mainPanel.add(homeScreen, HOME_SCREEN);
        mainPanel.add(quizScreen, QUIZ_SCREEN);
        mainPanel.add(resultsScreen, RESULTS_SCREEN);
        mainPanel.add(reviewScreen, REVIEW_SCREEN);
    }
    
    /**
     * Sets up event handlers for the main window
     */
    private void setupEventHandlers() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleApplicationExit();
            }
        });
    }
    
    /**
     * Creates the application menu bar
     * @return JMenuBar with application menus
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        
        JMenuItem newQuizItem = new JMenuItem("New Quiz");
        newQuizItem.setMnemonic('N');
        newQuizItem.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
        newQuizItem.addActionListener(e -> showHomeScreen());
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic('X');
        exitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
        exitItem.addActionListener(e -> handleApplicationExit());
        
        fileMenu.add(newQuizItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        
        return menuBar;
    }
    
    /**
     * Creates a simple application icon
     * @return Application icon image
     */
    private Image createAppIcon() {
        // Create a simple colored square as icon
        int size = 32;
        BufferedImage icon = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = icon.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(PRIMARY_COLOR);
        g2d.fillRoundRect(0, 0, size, size, 8, 8);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "Q";
        int x = (size - fm.stringWidth(text)) / 2;
        int y = (size + fm.getAscent() - fm.getDescent()) / 2;
        g2d.drawString(text, x, y);
        g2d.dispose();
        return icon;
    }
    
    // Screen navigation methods
    
    /**
     * Shows the home screen
     */
    public void showHomeScreen() {
        homeScreen.refreshScreen();
        cardLayout.show(mainPanel, HOME_SCREEN);
        setTitle(APP_TITLE + " - Home");
    }
    
    /**
     * Shows the quiz screen
     */
    public void showQuizScreen() {
        quizScreen.refreshScreen();
        cardLayout.show(mainPanel, QUIZ_SCREEN);
        setTitle(APP_TITLE + " - Quiz");
    }
    
    /**
     * Shows the results screen
     */
    public void showResultsScreen() {
        resultsScreen.refreshScreen();
        cardLayout.show(mainPanel, RESULTS_SCREEN);
        setTitle(APP_TITLE + " - Results");
    }
    
    /**
     * Shows the review screen
     */
    public void showReviewScreen() {
        reviewScreen.refreshScreen();
        cardLayout.show(mainPanel, REVIEW_SCREEN);
        setTitle(APP_TITLE + " - Review");
    }
    
    // Quiz management methods
    
    /**
     * Starts a new quiz with the specified parameters
     * @param topic The quiz topic
     * @param numberOfQuestions Number of questions to generate
     * @param userName The user's name
     */
    public void startNewQuiz(String topic, int numberOfQuestions, String userName) {
        // Create user and set in quiz manager
        User user = new User(userName);
        quizManager.setCurrentUser(user);
        
        // Show loading dialog with progress updates
        showLoadingDialog("Generating quiz questions...");
        
        // Start quiz generation with progress callback
        quizManager.startNewQuiz(topic, numberOfQuestions, this::updateLoadingMessage)
            .thenAccept(session -> {
                SwingUtilities.invokeLater(() -> {
                    hideLoadingDialog();
                    showQuizScreen();
                });
            })
            .exceptionally(throwable -> {
                SwingUtilities.invokeLater(() -> {
                    hideLoadingDialog();
                    showErrorDialog("Failed to generate quiz", throwable.getMessage());
                });
                return null;
            });
    }
    
    /**
     * Updates the loading message
     */
    private void updateLoadingMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            if (loadingDialog != null && loadingDialog.isVisible()) {
                loadingLabel.setText(message);
            }
        });
    }
    
    /**
     * Submits an answer for the current question
     * @param answer The selected answer
     */
    public void submitAnswer(String answer) {
        if (quizManager.submitAnswer(answer)) {
            quizScreen.updateDisplay();
        }
    }
    
    /**
     * Moves to the next question
     */
    public void nextQuestion() {
        if (!quizManager.nextQuestion()) {
            // Quiz is complete
            completeQuiz();
        } else {
            quizScreen.updateDisplay();
        }
    }
    
    /**
     * Moves to the previous question
     */
    public void previousQuestion() {
        if (quizManager.previousQuestion()) {
            quizScreen.updateDisplay();
        }
    }
    
    /**
     * Completes the current quiz
     */
    public void completeQuiz() {
        quizManager.completeQuiz();
        showResultsScreen();
    }
    
    /**
     * Gets the quiz manager instance
     * @return QuizManager instance
     */
    public QuizManager getQuizManager() {
        return quizManager;
    }
    
    // Utility methods
    
    /**
     * Creates a loading dialog
     * @return JDialog for showing loading state
     */
    private void showLoadingDialog(String message) {
        loadingDialog = new JDialog(this, "Loading", false); // Changed from true to false (non-modal)
        loadingDialog.setSize(300, 100);
        loadingDialog.setLocationRelativeTo(this);
        loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        loadingLabel = new JLabel(message, JLabel.CENTER);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        
        panel.add(loadingLabel, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);
        
        loadingDialog.add(panel);
        
        // Disable the main window while loading to prevent user interaction
        this.setEnabled(false);
        
        loadingDialog.setVisible(true);
    }
    
    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isVisible()) {
            loadingDialog.dispose();
            loadingDialog = null;
        }
        
        // Re-enable the main window
        this.setEnabled(true);
        this.requestFocus();
    }
    
    /**
     * Shows an error dialog
     * @param title Dialog title
     * @param message Error message
     */
    public void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Shows an information dialog
     * @param title Dialog title
     * @param message Information message
     */
    public void showInfoDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Shows a confirmation dialog
     * @param title Dialog title
     * @param message Confirmation message
     * @return true if user confirmed, false otherwise
     */
    public boolean showConfirmDialog(String title, String message) {
        int result = JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }
    
    /**
     * Shows the about dialog
     */
    private void showAboutDialog() {
        String message = "AI QuizMaster v1.0\n\n" +
                        "A Java desktop application that generates\n" +
                        "quiz questions using the Groq LLaMA API.\n\n" +
                        "Built with Java Swing and Maven.";
        
        JOptionPane.showMessageDialog(this, message, "About AI QuizMaster", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Handles application exit
     */
    private void handleApplicationExit() {
        if (quizManager.hasActiveSession()) {
            boolean confirmed = showConfirmDialog("Exit Application", 
                "You have an active quiz session. Are you sure you want to exit?");
            if (!confirmed) {
                return;
            }
        }
        
        // Shutdown quiz manager
        quizManager.shutdown();
        
        // Exit application
        System.exit(0);
    }
    
    // Color getters for child components
    public static Color getPrimaryColor() { return PRIMARY_COLOR; }
    public static Color getSecondaryColor() { return SECONDARY_COLOR; }
    public static Color getSuccessColor() { return SUCCESS_COLOR; }
    public static Color getErrorColor() { return ERROR_COLOR; }
}
