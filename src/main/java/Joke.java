import java.util.Scanner;

public class Joke {
    public static void ligma(Scanner sc) {
        String init = SigmaBot.HR_LINE
                + "\t" + "Ligma.\n"
                + SigmaBot.HR_LINE;
        System.out.println(init);
        String response = sc.nextLine();
        while (!response.contains("?")) {
            String re = SigmaBot.HR_LINE
                    + "\t" + "Ligma.\n"
                    + SigmaBot.HR_LINE;
            System.out.println(re);
            response = sc.nextLine();
        }
        String end = SigmaBot.HR_LINE
                + "\t" + "Ligma ball.\n"
                + SigmaBot.HR_LINE_OUT;
        System.out.println(end);
    }
}
