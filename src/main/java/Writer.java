import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Writer {
    private File cacheFile;
    private PrintWriter printWriter;

    public Writer(String filePath) {
        this.cacheFile = new File(filePath);
        try {
            this.printWriter = new PrintWriter(new FileWriter(this.cacheFile, true));
        } catch (IOException e) {
            System.out.println("Error initializing writer: " + e.getMessage());
        }
    }

    // New method that prints to console and writes to file
    public void printAndWrite(String text) {
        System.out.println(text); // Print to console
        write(text);              // Write to log file
    }

    // Method to write only to the log file
    public void write(String text) {
        if (printWriter != null) {
            printWriter.println(text);
            printWriter.flush(); // Ensure that data is written to file
        } else {
            System.out.println("PrintWriter is not initialized.");
        }
    }

    public void close() {
        if (printWriter != null) {
            printWriter.close();
        }
    }
}
