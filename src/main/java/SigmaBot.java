import java.util.*;
import java.io.File;

public class SigmaBot {
    public static final String HR_LINE_IN = ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n";
    public static final String HR_LINE_OUT = "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n";
    public static final String HR_LINE = "——————————————————————————————————————————————————————————————————————————————\n";
    public static final String HR_LINE_DOUBLE = "==============================================================================\n";
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String CYAN = "\u001B[36m";

    private Writer writer; // Use Writer class instead of directly handling File

    public SigmaBot(String filePath) {
        this.writer = new Writer(filePath);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        Greeting.greetingMsg();
        Map<String, Listing> lsts = new HashMap<>();

        while (true) {
            String inputString = scanner.nextLine().trim();

            if (inputString.equalsIgnoreCase("bye")) {
                Greeting.byeMsg();
                break;
            }

            if (Listing.inListingEventChain) {
                if (inputString.equalsIgnoreCase("sort")) {
                    Listing.handleSortLists(scanner, lsts, writer);
                } else if (inputString.equalsIgnoreCase("remove")) {
                    Listing.handleRemoveList(scanner, lsts, writer);
                } else if (inputString.equalsIgnoreCase("exit")) {
                    Listing.inListingEventChain = false; // Exiting listing event chain
                    writer.printAndWrite(HR_LINE_OUT + GREEN + "Exiting listing event chain.\n" + RESET + HR_LINE_OUT);
                } else {
                    writer.printAndWrite(HR_LINE + RED + "\tInvalid command while in listing event chain. Please enter 'sort', 'remove', or 'exit'.\n" + RESET + HR_LINE_OUT);
                }
            } else {
                if (inputString.equalsIgnoreCase("list")) {
                    Listing.handleListInput(scanner, lsts, writer);
                    Listing.inListingEventChain = Listing.isInListingEventChain();
                } else if (inputString.equalsIgnoreCase("joke")) {
                    Joke.ligma(scanner);
                } else {
                    Echo.echoMsg(inputString);
                }
            }
        }
        scanner.close();
        writer.close(); // Close writer to ensure all data is saved
    }

    public static void main(String[] args) {
        SigmaBot bot = new SigmaBot("../data/log.txt");
        bot.run();
    }
}
