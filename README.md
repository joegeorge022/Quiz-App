# AI QuizMaster

A Java desktop application that dynamically generates quiz questions using the Groq LLaMA API. Built with Swing GUI and following MVC architecture patterns.

## Features

- **AI-Powered Question Generation**: Uses Groq LLaMA API to generate contextual quiz questions on any topic
- **Modern Swing GUI**: Clean, intuitive interface with multiple screens (Home, Quiz, Results, Review)
- **Comprehensive Quiz Management**: Track progress, scores, and performance statistics
- **Answer Review System**: Detailed review of all questions with explanations
- **User Statistics**: Personal performance tracking across multiple quiz sessions
- **Robust Error Handling**: Network failure recovery and malformed JSON handling
- **Cross-Platform**: Runs on Windows, macOS, and Linux

## Architecture

The application follows a clean MVC (Model-View-Controller) pattern with clear separation of concerns:

### Model Classes
- **`Question`**: Represents quiz questions with options, correct answers, and explanations
- **`User`**: Manages user information and quiz history
- **`QuizSession`**: Handles quiz state, scoring, and progress tracking

### Service Layer
- **`ApiClient`**: HTTP client for Groq LLaMA API integration with OkHttp
- **`QuestionParser`**: JSON parsing using Jackson with robust error handling
- **`QuizManager`**: Business logic coordinator managing quiz operations

### GUI Components
- **`QuizGUI`**: Main application controller managing screen navigation
- **`HomeScreen`**: User input and quiz configuration
- **`QuizScreen`**: Question display and answer selection
- **`ResultsScreen`**: Score display and performance metrics
- **`ReviewScreen`**: Detailed answer review with explanations

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Groq API key (free at [console.groq.com](https://console.groq.com/keys))

## Installation

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd Quiz-App
   ```

2. **Set up your Groq API key** (choose one method):

   **Option A: Environment Variable**
   ```bash
   export GROQ_API_KEY="your_api_key_here"
   ```

   **Option B: System Property**
   ```bash
   mvn exec:java -Dgroq.api.key="your_api_key_here"
   ```

   **Option C: Configuration File**
   Create `src/main/resources/config.properties`:
   ```properties
   groq.api.key=your_api_key_here
   ```

3. **Build the application**:
   ```bash
   mvn clean compile
   ```

## Usage

### Running the Application

**Development Mode**:
```bash
mvn exec:java -Dexec.mainClass="com.quizmaster.QuizMasterApp"
```

**Production JAR**:
```bash
mvn clean package
java -jar target/ai-quiz-master-1.0.0.jar
```

### Using the Application

1. **Home Screen**: Enter your name, choose a quiz topic, and select the number of questions (1-20)
2. **Quiz Screen**: Answer multiple-choice questions with navigation between questions
3. **Results Screen**: View your final score, time taken, and performance statistics
4. **Review Screen**: Examine all questions with correct answers and explanations

### Example Topics

- Java Programming
- World History
- Biology
- Mathematics
- Physics
- Literature
- Geography
- Computer Science
- Art History
- Current Events

## Dependencies

- **Jackson** (2.15.2): JSON parsing and serialization
- **OkHttp** (4.11.0): HTTP client for API requests
- **JUnit Jupiter** (5.9.3): Unit testing framework

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/quizmaster/
│   │       ├── QuizMasterApp.java          # Main application entry point
│   │       ├── model/                      # Data models
│   │       │   ├── Question.java
│   │       │   ├── User.java
│   │       │   └── QuizSession.java
│   │       ├── service/                    # Business logic
│   │       │   ├── ApiClient.java
│   │       │   ├── QuestionParser.java
│   │       │   └── QuizManager.java
│   │       └── gui/                        # User interface
│   │           ├── QuizGUI.java
│   │           ├── HomeScreen.java
│   │           ├── QuizScreen.java
│   │           ├── ResultsScreen.java
│   │           └── ReviewScreen.java
│   └── resources/
│       └── config.properties              # Optional configuration
└── test/
    └── java/                              # Unit tests
```

## API Integration

The application integrates with the Groq LLaMA API to generate quiz questions:

- **Endpoint**: `https://api.groq.com/v1/chat/completions`
- **Model**: `llama3-8b-8192`
- **Authentication**: Bearer token via API key
- **Response Format**: Structured JSON with questions, options, correct answers, and explanations

### Example API Request
```json
{
  "messages": [
    {
      "role": "user",
      "content": "Generate 5 multiple-choice questions about Java Programming..."
    }
  ],
  "model": "llama3-8b-8192",
  "temperature": 0.3,
  "max_tokens": 2048
}
```

## Error Handling

The application includes comprehensive error handling for:

- **Network Issues**: Connection timeouts, API unavailability
- **JSON Parsing**: Malformed responses, missing fields
- **API Errors**: Invalid keys, rate limiting, service errors
- **User Input**: Invalid topics, connection failures

## Building for Distribution

**Create executable JAR**:
```bash
mvn clean package
```

The shaded JAR will be created at `target/ai-quiz-master-1.0.0.jar` with all dependencies included.

**Run the JAR**:
```bash
java -jar target/ai-quiz-master-1.0.0.jar
```

## Configuration Options

### System Properties
- `groq.api.key`: Groq API key
- `awt.useSystemAAFontSettings`: Font anti-aliasing
- `swing.aatext`: Text anti-aliasing

### Environment Variables
- `GROQ_API_KEY`: Groq API key

## Troubleshooting

### Common Issues

**"API Key Required" Dialog**:
- Ensure your Groq API key is set via environment variable, system property, or config file
- Verify the API key is valid at [console.groq.com](https://console.groq.com)

**Network Connection Errors**:
- Check internet connectivity
- Verify firewall settings allow HTTPS connections
- Ensure the Groq API service is available

**JSON Parsing Errors**:
- Usually indicates API response format changes
- Check application logs for detailed error messages

**Build Failures**:
- Ensure Java 11+ and Maven 3.6+ are installed
- Run `mvn clean` before building
- Check for dependency conflicts

## Performance Considerations

- Questions are generated asynchronously to prevent UI freezing
- HTTP client uses connection pooling for efficiency
- JSON parsing includes validation and error recovery
- GUI updates are performed on the Event Dispatch Thread

## Security Notes

- API keys are handled securely and not logged
- Network requests use HTTPS encryption
- Input validation prevents injection attacks
- No sensitive data is stored locally

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes with appropriate tests
4. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues and questions:
1. Check the troubleshooting section above
2. Review the application logs for error details
3. Ensure your Groq API key is valid and has sufficient credits
4. Verify your internet connection and firewall settings

## Version History

- **v1.0.0**: Initial release with core quiz functionality, Groq API integration, and complete Swing GUI