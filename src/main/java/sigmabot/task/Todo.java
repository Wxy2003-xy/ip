package sigmabot.task;

import java.util.Map;
import java.util.Scanner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
/**
 * The {@code Todo} class represents a simple task with a name and description.
 * It can be marked as done or not done. This class extends the {@link Task} class.
 * The {@code Todo} class provides constructors to create a new task and a static method to create a task through user input.
 */
public class Todo extends Task {

    /**
     * Constructs a new {@code Todo} object with the specified name and description.
     * By default, the task is not marked as done.
     *
     * @param name The name of the task.
     * @param description The description of the task.
     */
    public Todo(String name, String description) {
        super(name, description);
    }

    /**
     * Constructs a new {@code Todo} object with the specified name, description, and completion status.
     *
     * @param name The name of the task.
     * @param description The description of the task.
     * @param isDone The completion status of the task. If {@code true}, the task is marked as done; otherwise, it is not done.
     */
    public Todo(String name, String description, boolean isDone) {
        super(name, description);
        this.isDone = isDone;
    }

    /**
     * Prompts the user to enter the details for a new {@code Todo} task and creates a new {@code Todo} object.
     * (This method was for the CLI version of the method).
     *
     * @param sc The {@link Scanner} object used to read user input.
     * @return A new {@code Todo} object created based on user input.
     */
    public static Todo createTodo(Scanner sc) {
        System.out.println("Enter name: ");
        String name = sc.nextLine().trim();
        System.out.println("Enter description: ");
        String description = sc.nextLine().trim();
        return new Todo(name, description);
    }

    /**
     * Resets the input field handler to the main command handler.
     */
    private static void resetInputHandler(TextArea displayArea, TextField inputField, Map<String, Task> taskMap) {
        displayArea.appendText("Enter task type to add (todo, deadline, event) or '/exit' to finish:\n");
        inputField.setOnAction(event -> {
            String input = inputField.getText().trim();
            inputField.clear();

            // Main input handling logic
            if (input.equalsIgnoreCase("/exit")) {
                displayArea.appendText("Finished adding tasks.\n");
                return;
            }

            switch (input.toLowerCase()) {
            case "todo":
                createTodoGUI(displayArea, inputField, taskMap);
                break;
            case "deadline":
                Deadline.createDeadlineGUI(displayArea, inputField, taskMap);
                break;
            case "event":
                Event.createEventGUI(displayArea, inputField, taskMap);
                break;
            default:
                displayArea.appendText("Invalid task type. Please enter 'todo', 'deadline', or 'event'.\n");
            }
        });
    }

    /**
     * Creates a new {@code Todo} object using GUI input components and stores it in the provided map.
     *
     * @param displayArea The {@code TextArea} object for displaying output.
     * @param inputField  The {@code TextField} object for reading user input.
     * @param taskMap     The map to store the created {@code Todo} object.
     */
    public static void createTodoGUI(TextArea displayArea, TextField inputField, Map<String, Task> taskMap) {
        displayArea.appendText("Enter name for Todo:\n");

        // Handle name input
        inputField.setOnAction(event -> {
            String name = inputField.getText().trim();
            inputField.clear();
            if (name.equalsIgnoreCase("/exit")) {
                displayArea.appendText("Todo creation canceled.\n");
                resetInputHandler(displayArea, inputField, taskMap);
                return;
            }
            displayArea.appendText("Enter description for Todo:\n");

            // Handle description input
            inputField.setOnAction(eventDesc -> {
                String description = inputField.getText().trim();
                inputField.clear();
                // Check if the user wants to exit
                if (description.equalsIgnoreCase("/exit")) {
                    displayArea.appendText("Todo creation canceled.\n");
                    resetInputHandler(displayArea, inputField, taskMap);
                    return;
                }
                Todo newTodo = new Todo(name, description);
                taskMap.put(name, newTodo);  // Store the new Todo in the map
                displayArea.appendText("Todo created: " + newTodo.toString() + "\n");
                resetInputHandler(displayArea, inputField, taskMap);
            });
        });
    }


    /**
     * Returns a string representation of the {@code Todo} task.
     * The format includes the type of task (Todo) and the information about its completion status, name, and description.
     *
     * @return A string representation of the {@code Todo} task.
     */
    @Override
    public String toString() {
        return "[T] " + super.toString();
    }
}
