public class Greeting {
    public static void greetingMsg() {
        String init = SigmaBot.HR_LINE_IN
                + "\t" + "Yo I'm SigmaBot\n"
                + "\t" + "What are u up to tdy?\n"
                + SigmaBot.HR_LINE;
        System.out.println(init);
    }

    public static void byeMsg() {
        String msg = SigmaBot.HR_LINE
                + "\t" + "See u!\n"
                + SigmaBot.HR_LINE_OUT
                + SigmaBot.HR_LINE_OUT;
        System.out.println(msg);
    }
}
