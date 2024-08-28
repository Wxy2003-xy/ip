import exceptions.InvalidInputException;
import java.util.*;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Listing {
    private List<Task> lst = new LinkedList<>();
    private String tag;
    public static boolean inListingEventChain = false;

    public Listing() {}

    public Listing(String tag) {
        this.tag = tag;
    }

    public void addItem(Task task) {
        lst.add(task);
    }

    public String printList() {
        if (lst.isEmpty()) {
            return SigmaBot.HR_LINE + "\tNo items found in current List\n" + SigmaBot.HR_LINE;
        }
        StringBuilder result = new StringBuilder();
        if (tag != null) {
            result.append(tag).append(":\n");
        }
        int index = 1;

        for (Task task : lst) {
            result.append(index++).append(". ").append(task.toString()).append("\n");
        }
        return SigmaBot.HR_LINE + result.toString() + SigmaBot.HR_LINE;
    }

    public static void handleListInput(Scanner scanner, Map<String, Listing> lsts, Writer writer) {
        try {
            if (lsts.isEmpty()) {
                writer.printAndWrite(SigmaBot.HR_LINE + SigmaBot.CYAN + "\tNo existing list found, would you like to start a new list? [y/n/exit]\n" + SigmaBot.RESET + SigmaBot.HR_LINE);
                String response = scanner.nextLine().trim();
                if (response.equalsIgnoreCase("y")) {
                    createNewList(scanner, lsts, writer);
                } else if (response.equalsIgnoreCase("n") || response.equalsIgnoreCase("exit")) {
                    writer.printAndWrite(SigmaBot.HR_LINE + SigmaBot.CYAN + "\tOK. Exiting...\n" + SigmaBot.RESET + SigmaBot.HR_LINE_OUT);
                    inListingEventChain = false;
                } else {
                    writer.printAndWrite(SigmaBot.HR_LINE + SigmaBot.RED + "\tInvalid response. Please enter 'y', 'n', or 'exit'.\n" + SigmaBot.RESET + SigmaBot.HR_LINE_OUT);
                }
            } else {
                writer.printAndWrite(SigmaBot.HR_LINE + SigmaBot.CYAN + "\tCreate new list? [a],\n" +
                        "\tQuery an existing list? [b],\n" +
                        "\tDelete an existing list? [c],\n" +
                        "\tor Sort lists? [d],\n" +
                        "\tExit [exit]\n" + SigmaBot.RESET + SigmaBot.HR_LINE);
                String response = scanner.nextLine().trim();
                switch (response.toLowerCase()) {
                    case "a":
                        createNewList(scanner, lsts, writer);
                        break;
                    case "b":
                        queryExistingList(scanner, lsts, writer);
                        break;
                    case "c":
                        handleRemoveList(scanner, lsts, writer);
                        break;
                    case "d":
                        handleSortLists(scanner, lsts, writer);
                        break;
                    case "exit":
                        inListingEventChain = false;
                        writer.printAndWrite(SigmaBot.HR_LINE + SigmaBot.GREEN + "\tExiting...\n" + SigmaBot.RESET + SigmaBot.HR_LINE_OUT);
                        break;
                    default:
                        writer.printAndWrite(SigmaBot.HR_LINE + SigmaBot.RED + "\tInvalid response. Please enter 'a', 'b', 'c', 'd', or 'exit'.\n" + SigmaBot.RESET + SigmaBot.HR_LINE_OUT);
                        break;
                }
            }
            Greeting.greetingMsg();
        } catch (InvalidInputException e) {
            writer.printAndWrite(SigmaBot.HR_LINE + SigmaBot.RED + "\tError: " + e.getMessage() + "\n" + SigmaBot.RESET + SigmaBot.HR_LINE_OUT);
        } catch (Exception e) {
            writer.printAndWrite(SigmaBot.HR_LINE + SigmaBot.RED + "\tUnexpected error occurred: " + e.getMessage() + "\n" + SigmaBot.RESET + SigmaBot.HR_LINE_OUT);
            e.printStackTrace();
        }
    }

    public static void createNewList(Scanner scanner, Map<String, Listing> lsts, Writer writer) throws InvalidInputException {
        Listing newList = createListEventChain(scanner, lsts, writer);
        lsts.put(newList.getTag(), newList);
        newList.addItemEventChain(scanner, writer);
    }

    public static Listing createListEventChain(Scanner sc, Map<String, Listing> lsts, Writer writer) {
        writer.printAndWrite(SigmaBot.HR_LINE + "\tEnter a tag for the new list (or press Enter to skip):\n" + SigmaBot.HR_LINE);
        String tag = sc.nextLine().trim();
        if (tag.isEmpty()) {
            int defaultTagIndex = 1;
            while (lsts.containsKey("list_" + defaultTagIndex)) {
                defaultTagIndex++;
            }
            tag = "list_" + defaultTagIndex;
            writer.printAndWrite(SigmaBot.HR_LINE + "\tNew list created with default tag: " + tag + "\n" + SigmaBot.HR_LINE);
        } else if (lsts.containsKey(tag)) {
            writer.printAndWrite(SigmaBot.HR_LINE + "\tTag already exists. Please choose a different tag.\n" + SigmaBot.HR_LINE);
            return createListEventChain(sc, lsts, writer);
        } else {
            writer.printAndWrite(SigmaBot.HR_LINE + "\tNew list \"" + tag + "\" created\n" + SigmaBot.HR_LINE);
        }
        return new Listing(tag);
    }

    public void addItemEventChain(Scanner sc, Writer writer) throws InvalidInputException {
        writer.printAndWrite(SigmaBot.HR_LINE + "\tAdd tasks to list: (Stop by entering \"EXIT\")");
        while (true) {
            writer.printAndWrite("\tEnter task type and name (format: [type] name, e.g., 'todo Buy milk') or EXIT to finish:\n" + SigmaBot.HR_LINE_DOUBLE);
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("EXIT")) {
                writer.printAndWrite(SigmaBot.HR_LINE + SigmaBot.GREEN + "\tComplete\n" + SigmaBot.RESET + SigmaBot.HR_LINE_OUT);
                break;
            }

            String[] parts = input.split(" ", 2);
            if (parts.length < 2) {
                writer.printAndWrite(SigmaBot.HR_LINE + SigmaBot.RED +  "\tInvalid format\n" + SigmaBot.RESET + SigmaBot.HR_LINE);
                continue;
            }

            String type = parts[0].toLowerCase();
            String name = parts[1];

            writer.printAndWrite(SigmaBot.HR_LINE + "\tEnter task description:\n" + SigmaBot.HR_LINE);
            String description = sc.nextLine().trim();

            Task task;
            switch (type) {
                case "todo":
                    task = new Todo(name, description);
                    break;
                case "event":
                    writer.printAndWrite(SigmaBot.HR_LINE + "\tEnter event start time:\n" + SigmaBot.HR_LINE);
                    String startTime = sc.nextLine().trim();

                    writer.printAndWrite(SigmaBot.HR_LINE + "\tEnter event end time:\n" + SigmaBot.HR_LINE);
                    String endTime = sc.nextLine().trim();

                    writer.printAndWrite(SigmaBot.HR_LINE + "\tEnter event location:\n" + SigmaBot.HR_LINE);
                    String location = sc.nextLine().trim();

                    task = new Event(name, description, startTime, endTime, location);
                    break;
                case "deadline":
                    writer.printAndWrite(SigmaBot.HR_LINE + "\tEnter deadline time:\n" + SigmaBot.HR_LINE);
                    String byTime = sc.nextLine().trim();

                    task = new Deadline(name, description, byTime);
                    break;
                default:
                    writer.printAndWrite(SigmaBot.HR_LINE_IN + "\tInvalid task type. Please enter 'todo', 'event', or 'deadline'.\n" + SigmaBot.HR_LINE_OUT);
                    continue;
            }

            addItem(task);
            writer.printAndWrite(SigmaBot.HR_LINE + "\tTask added successfully. Total tasks in the list: " + getList().size() + "\n" + SigmaBot.HR_LINE);
        }
    }

    public static void queryExistingList(Scanner scanner, Map<String, Listing> lsts, Writer writer) {
        if (lsts.isEmpty()) {
            writer.printAndWrite(SigmaBot.HR_LINE + SigmaBot.RED + "\tNo lists available.\n" + SigmaBot.RESET + SigmaBot.HR_LINE_OUT);
            return;
        }

        writer.printAndWrite(SigmaBot.HR_LINE + SigmaBot.CYAN + "\tAvailable lists by tag:" + SigmaBot.RESET);
        lsts.keySet().forEach(tag -> writer.printAndWrite("\t\t" + tag));
        writer.printAndWrite(SigmaBot.HR_LINE);
        writer.printAndWrite(SigmaBot.HR_LINE + SigmaBot.CYAN + "\tEnter the tag of the list you want to query or 'exit' to leave:\n" + SigmaBot.RESET + SigmaBot.HR_LINE);
        String tag = scanner.nextLine().trim();

        if (tag.equalsIgnoreCase("exit")) {
            inListingEventChain = false;  // Exiting listing event chain
            return;
        }

        if (lsts.containsKey(tag)) {
            Listing listing = lsts.get(tag);
            writer.printAndWrite(listing.printList());
            writer.printAndWrite(SigmaBot.HR_LINE_IN + "\tWould you like to [a] Mark tasks as done or [b] Delete tasks? (type 'exit' to exit)\n" + SigmaBot.HR_LINE_OUT);
            String action = scanner.nextLine().trim().toLowerCase();
            while (!action.equals("terminate")) {
                if (action.equals("a")) {
                    listing.markDoneEventChain(scanner, writer);
                    action = "terminate";
                } else if (action.equals("b")) {
                    listing.deleteItemEventChain(scanner, writer);
                    action = "terminate";
                } else if (action.equals("exit")) {
                    writer.printAndWrite(SigmaBot.HR_LINE + SigmaBot.GREEN + "\tExiting list management.\n" + SigmaBot.RESET + SigmaBot.HR_LINE_OUT);
                    action = "terminate";
                    inListingEventChain = false;
                    return;
                } else {
                    writer.printAndWrite(SigmaBot.HR_LINE_IN + SigmaBot.RED + "\tInvalid response. Please enter 'a', 'b', or 'exit'.\n" + SigmaBot.RESET + SigmaBot.HR_LINE_OUT);
                    action = scanner.nextLine();
                }
            }
        } else {
            writer.printAndWrite(SigmaBot.HR_LINE_IN + SigmaBot.RED + "\tNo list found with the tag \"" + tag + "\".\n" + SigmaBot.RESET + SigmaBot.HR_LINE_OUT);
        }

        inListingEventChain = false;  // Exiting listing event chain after operation
    }

    public static void handleRemoveList(Scanner scanner, Map<String, Listing> lsts, Writer writer) {
        if (lsts.isEmpty()) {
            writer.printAndWrite(SigmaBot.HR_LINE_IN + SigmaBot.RED + "\tNo lists available to remove.\n" + SigmaBot.RESET + SigmaBot.HR_LINE_OUT);
            return;
        }

        writer.printAndWrite(SigmaBot.HR_LINE + SigmaBot.CYAN + "\tAvailable lists by tag:" + SigmaBot.RESET);
        lsts.keySet().forEach(tag -> writer.printAndWrite("\t\t" + tag));

        writer.printAndWrite(SigmaBot.HR_LINE + SigmaBot.CYAN + "\tEnter the tag of the list you want to remove or 'exit' to leave:\n" + SigmaBot.RESET + SigmaBot.HR_LINE);
        String tag = scanner.nextLine().trim();

        if (tag.equalsIgnoreCase("exit")) {
            inListingEventChain = false;  // Exiting listing event chain
            return;
        }

        if (lsts.containsKey(tag)) {
            lsts.remove(tag);
            writer.printAndWrite(SigmaBot.HR_LINE + SigmaBot.CYAN + "\tList with tag \"" + tag + "\" has been removed.\n" + SigmaBot.RESET + SigmaBot.HR_LINE);
        } else {
            writer.printAndWrite(SigmaBot.HR_LINE + SigmaBot.RED + "\tNo list found with the tag \"" + tag + "\".\n" + SigmaBot.RESET + SigmaBot.HR_LINE_OUT);
        }

        inListingEventChain = false;  // Exiting listing event chain after operation
    }

    public static void handleSortLists(Scanner scanner, Map<String, Listing> lsts, Writer writer) {
        if (lsts.isEmpty()) {
            writer.printAndWrite(SigmaBot.HR_LINE_IN + SigmaBot.RED + "\tNo lists available to sort.\n" + SigmaBot.RESET + SigmaBot.HR_LINE_OUT);
            return;
        }

        writer.printAndWrite(SigmaBot.HR_LINE_IN + SigmaBot.CYAN + "\tSort by [a] Tag or [b] Length? (type 'exit' to exit)\n" + SigmaBot.RESET + SigmaBot.HR_LINE_OUT);
        String sortOption = scanner.nextLine().trim();

        if (sortOption.equalsIgnoreCase("exit")) {
            inListingEventChain = false;  // Exiting listing event chain
            return;
        }

        List<Map.Entry<String, Listing>> sortedList = new ArrayList<>(lsts.entrySet());

        if (sortOption.equalsIgnoreCase("a")) {
            sortedList.sort(Map.Entry.comparingByKey());
        } else if (sortOption.equalsIgnoreCase("b")) {
            sortedList.sort(Comparator.comparingInt(entry -> entry.getValue().getList().size()));
        } else {
            writer.printAndWrite(SigmaBot.HR_LINE_IN + SigmaBot.RED + "\tInvalid sorting option. Please enter 'a', 'b', or 'exit'.\n" + SigmaBot.RESET + SigmaBot.HR_LINE_OUT);
            return;
        }

        writer.printAndWrite(SigmaBot.HR_LINE_IN + SigmaBot.CYAN + "\tSorted lists:\n" + SigmaBot.RESET + SigmaBot.HR_LINE);
        for (Map.Entry<String, Listing> entry : sortedList) {
            writer.printAndWrite(entry.getKey());
            writer.printAndWrite(entry.getValue().printList());
        }
        writer.printAndWrite(SigmaBot.HR_LINE_OUT);

        inListingEventChain = false;  // Exiting listing event chain after operation
    }

    public void markDoneEventChain(Scanner scanner, Writer writer) {
        writer.printAndWrite(SigmaBot.HR_LINE_IN + "\tMark tasks as done in list: \"" + tag + "\". Enter -1 to stop or exit.\n" + SigmaBot.HR_LINE_OUT);
        while (true) {
            try {
                int idx = scanner.nextInt();
                scanner.nextLine();
                if (idx == -1) {
                    return;
                }
                if (idx - 1 < 0 || idx - 1 >= lst.size()) {
                    writer.printAndWrite(SigmaBot.HR_LINE_IN + "\tInvalid response: Index out of bounds\n" + SigmaBot.HR_LINE_OUT);
                    continue;
                } else {
                    this.markDone(idx - 1, writer);
                }
            } catch (InputMismatchException e) {
                writer.printAndWrite(SigmaBot.HR_LINE_IN + "\tPlease enter a valid number.\n" + SigmaBot.HR_LINE_OUT);
                scanner.nextLine();
            }
        }
    }

    public void deleteItemEventChain(Scanner scanner, Writer writer) {
        writer.printAndWrite(SigmaBot.HR_LINE_IN + "\tDelete tasks from list: \"" + tag + "\". Enter -1 to stop or exit.\n" + SigmaBot.HR_LINE_OUT);
        while (true) {
            try {
                int idx = scanner.nextInt();
                scanner.nextLine();
                if (idx == -1) {
                    return;
                }
                if (idx - 1 < 0 || idx - 1 >= lst.size()) {
                    writer.printAndWrite(SigmaBot.HR_LINE_IN + "\tInvalid response: Index out of bounds\n" + SigmaBot.HR_LINE_OUT);
                    continue;
                } else {
                    Task removedTask = lst.remove(idx - 1);
                    writer.printAndWrite(SigmaBot.HR_LINE_IN + "\tTask deleted: " + removedTask.getName() + "\n" + SigmaBot.HR_LINE_OUT);
                }
            } catch (InputMismatchException e) {
                writer.printAndWrite(SigmaBot.HR_LINE_IN + "\tPlease enter a valid number.\n" + SigmaBot.HR_LINE_OUT);
                scanner.nextLine();
            }
        }
    }

    public void markDone(int idx, Writer writer) {
        if (idx < 0 || idx >= lst.size()) {
            writer.printAndWrite(SigmaBot.HR_LINE + "\tInvalid response: Index out of bounds\n" + SigmaBot.HR_LINE_OUT);
            return;
        }

        Task task = lst.get(idx);
        if (!task.isDone()) {
            task.markDone();
            writer.printAndWrite(SigmaBot.HR_LINE_IN + "\tTask marked as done: " + task.getName() + "\n" + SigmaBot.HR_LINE_OUT);
        } else {
            writer.printAndWrite(SigmaBot.HR_LINE_IN + "\tInvalid response: Task already marked as done\n" + SigmaBot.HR_LINE_OUT);
        }
    }

    public static boolean isInListingEventChain() {
        return inListingEventChain;
    }

    public String getTag() {
        return tag;
    }

    public List<Task> getList() {
        return lst;
    }
}
