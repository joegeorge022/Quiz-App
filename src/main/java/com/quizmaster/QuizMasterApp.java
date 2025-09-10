package com.quizmaster;

import com.quizmaster.gui.QuizGUI;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Main application class for AI QuizMaster.
 * Handles application initialization, configuration, and startup.
 */
public class QuizMasterApp {
    
    private static final String APP_NAME = "AI QuizMaster";
    private static final String VERSION = "1.0.0";
    private static final String CONFIG_FILE = "config.properties";
    private static final String API_KEY_PROPERTY = "groq.api.key";
    private static final String API_KEY_ENV_VAR = "GROQ_API_KEY";
    
    /**
     * Main method - application entry point
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Set system properties for better GUI experience
        setupSystemProperties();
        
        // Initialize the application
        SwingUtilities.invokeLater(() -> {
            try {
                initializeApplication();
            } catch (Exception e) {
                handleStartupError(e);
            }
        });
    }
    
    /**
     * Sets up system properties for optimal GUI performance and appearance
     */
    private static void setupSystemProperties() {
        // Enable anti-aliasing for text
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        // Use system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fall back to default look and feel
            System.err.println("Warning: Could not set system look and feel: " + e.getMessage());
        }
        
        // Set application name for macOS
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", APP_NAME);
        
        // Improve font rendering - remove problematic line that could cause compilation issues
        try {
            System.setProperty("swing.defaultlaf", UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignore if system look and feel is not available
        }
    }
    
    /**
     * Initializes and starts the application
     */
    private static void initializeApplication() {
        // Show splash screen
        JWindow splashScreen = createSplashScreen();
        splashScreen.setVisible(true);
        
        try {
            // Get API key
            String apiKey = getApiKey();
            
            if (apiKey == null || apiKey.trim().isEmpty()) {
                splashScreen.dispose();
                showApiKeyDialog();
                return;
            }
            
            // Create and show main application window
            QuizGUI mainWindow = new QuizGUI(apiKey);
            
            // Hide splash screen and show main window
            splashScreen.dispose();
            mainWindow.setVisible(true);
            
        } catch (Exception e) {
            splashScreen.dispose();
            throw e;
        }
    }
    
    /**
     * Creates a splash screen for application startup
     * @return JWindow containing the splash screen
     */
    private static JWindow createSplashScreen() {
        JWindow splash = new JWindow();
        splash.setSize(400, 250);
        splash.setLocationRelativeTo(null);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(41, 128, 185));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        // App name
        JLabel nameLabel = new JLabel(APP_NAME);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 28));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Version
        JLabel versionLabel = new JLabel("Version " + VERSION);
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        versionLabel.setForeground(Color.WHITE);
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Loading message
        JLabel loadingLabel = new JLabel("Loading...");
        loadingLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        loadingLabel.setForeground(Color.WHITE);
        loadingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Progress bar
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setMaximumSize(new Dimension(300, 20));
        
        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(versionLabel);
        panel.add(Box.createVerticalStrut(30));
        panel.add(loadingLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(progressBar);
        
        splash.add(panel);
        return splash;
    }
    
    /**
     * Attempts to get the API key from various sources
     * @return API key string or null if not found
     */
    private static String getApiKey() {
        // Try environment variable first
        String apiKey = System.getenv(API_KEY_ENV_VAR);
        if (apiKey != null && !apiKey.trim().isEmpty()) {
            return apiKey.trim();
        }
        
        // Try system property
        apiKey = System.getProperty(API_KEY_PROPERTY);
        if (apiKey != null && !apiKey.trim().isEmpty()) {
            return apiKey.trim();
        }
        
        // Try config file
        apiKey = getApiKeyFromConfig();
        if (apiKey != null && !apiKey.trim().isEmpty()) {
            return apiKey.trim();
        }
        
        return null;
    }
    
    /**
     * Attempts to read API key from configuration file
     * @return API key from config file or null if not found
     */
    private static String getApiKeyFromConfig() {
        try (InputStream input = QuizMasterApp.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                Properties properties = new Properties();
                properties.load(input);
                return properties.getProperty(API_KEY_PROPERTY);
            }
        } catch (IOException e) {
            // Config file not found or couldn't be read - this is not critical
            System.out.println("Config file not found or couldn't be read: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Shows a dialog for API key configuration
     */
    private static void showApiKeyDialog() {
        String message = "Groq API Key Required\n\n" +
                        "To use AI QuizMaster, you need a Groq API key.\n\n" +
                        "You can set it in one of these ways:\n" +
                        "1. Environment variable: " + API_KEY_ENV_VAR + "\n" +
                        "2. System property: -D" + API_KEY_PROPERTY + "=your_key\n" +
                        "3. Config file: src/main/resources/" + CONFIG_FILE + "\n\n" +
                        "Get your free API key at: https://console.groq.com/keys\n\n" +
                        "Would you like to enter your API key now?";
        
        int choice = JOptionPane.showConfirmDialog(
            null, 
            message, 
            "API Key Required", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            String apiKey = JOptionPane.showInputDialog(
                null,
                "Enter your Groq API Key:",
                "API Key Input",
                JOptionPane.PLAIN_MESSAGE
            );
            
            if (apiKey != null && !apiKey.trim().isEmpty()) {
                // Set as system property and restart
                System.setProperty(API_KEY_PROPERTY, apiKey.trim());
                initializeApplication();
            } else {
                showExitDialog();
            }
        } else {
            showExitDialog();
        }
    }
    
    /**
     * Shows exit confirmation dialog
     */
    private static void showExitDialog() {
        JOptionPane.showMessageDialog(
            null,
            "AI QuizMaster requires a valid Groq API key to function.\n" +
            "The application will now exit.",
            "Application Exit",
            JOptionPane.INFORMATION_MESSAGE
        );
        System.exit(0);
    }
    
    /**
     * Handles startup errors
     * @param e The exception that occurred during startup
     */
    private static void handleStartupError(Exception e) {
        String errorMessage = "Failed to start AI QuizMaster:\n\n" + e.getMessage();
        
        // Log the full stack trace to console
        System.err.println("Startup error:");
        e.printStackTrace();
        
        // Show user-friendly error dialog
        JOptionPane.showMessageDialog(
            null,
            errorMessage,
            "Startup Error",
            JOptionPane.ERROR_MESSAGE
        );
        
        System.exit(1);
    }
    
    /**
     * Gets application information
     * @return String containing app name and version
     */
    public static String getApplicationInfo() {
        return APP_NAME + " " + VERSION;
    }
    
    /**
     * Gets the application version
     * @return Version string
     */
    public static String getVersion() {
        return VERSION;
    }
}
