package sigmabot;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import sigmabot.command.ChatCommand;
import sigmabot.command.GreetingCommand;
import sigmabot.command.ListOperation;
import sigmabot.command.TerminateCommand;

import java.util.HashMap;
import java.util.Map;

/**
 * The Dialogue class manages the interaction between the user and the chatbot.
 * It processes user commands and controls the flow of conversation until the user decides to exit.
 */
public class Dialogue {
    private TextArea displayArea;
    private TextField inputField;
    private Map<String, ChatCommand> commands;

    /**
     * Private constructor to prevent direct instantiation.
     * Use the defaultDialogue() method to create a new Dialogue instance.
     */
    private Dialogue() {
        this.commands = new HashMap<>();
        this.commands.put("/list", new ListOperation());
        this.commands.put("/exit", new TerminateCommand());
    }

    /**
     * Creates a new instance of the Dialogue class.
     *
     * @return A new Dialogue object.
     */
    public static Dialogue defaultDialogue() {
        return new Dialogue();
    }

    /**
     * Sets up the UI components for the dialogue session.
     *
     * @param root The root container where UI components will be added.
     */
    public void setupUI(VBox root) {
        // Initialize UI components
        displayArea = new TextArea();
        displayArea.setEditable(false);
        inputField = new TextField();
        inputField.setPromptText("Type your command here...");
        Button sendButton = new Button("Send");
        // Set up button action
        sendButton.setOnAction(event -> handleInput());
        // Set up text field action
        inputField.setOnAction(event -> handleInput());
        // Add components to the layout
        root.getChildren().addAll(displayArea, inputField, sendButton);
        // Initial greeting
        GreetingCommand.greet(displayArea);
    }

    /**
     * Handles user input from the text field.
     */
    private void handleInput() {
        String input = inputField.getText().trim();
        if (!input.isEmpty()) {
            displayArea.appendText("User: " + input + "\n");
            ChatCommand command = commands.getOrDefault(input, null);
            if (command != null) {
                disableInputHandler();  // Temporarily disable the main input handler
                command.execute(displayArea, inputField);
                enableInputHandler();  // Re-enable the main input handler after execution
            } else {
                displayArea.appendText("Unknown command. Please enter '/list' or '/exit'.\n");
            }
            inputField.clear();
        }
    }

    /**
     * Disables the input field's main action handler.
     */
    private void disableInputHandler() {
        inputField.setOnAction(null);  // Disable the current input handler
    }

    /**
     * Re-enables the input field's main action handler.
     */
    private void enableInputHandler() {
        inputField.setOnAction(event -> handleInput());
    }
}
