package src;
import java.util.Scanner;

public class Main {
    private static String welcomeString = """
            ################################
            ###### Welcome to Hangman ######
            ################################

            """;

    
    public static void main(String[] args) {
        System.out.println(welcomeString);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Please choose a game mode [terminal, ui, exit]: ");
            
            String userChoiceString = scanner.nextLine().trim().toLowerCase();

            if (userChoiceString.equals("exit"))
                System.exit(0);
            
            if (userChoiceString.equals("ui")) {
                Display.main(args);
            }
            else {
                HangmanCLI hangmanCLI = new HangmanCLI();
                hangmanCLI.play(scanner);
            }
        }
    }
}
