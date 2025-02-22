public class Main {
    private static String welcomeString = """
            ################################
            ###### Welcome to Hangman ######
            ################################

            """;

    
    public static void main(String[] args) {
        System.out.println(welcomeString);

        Display display = new Display();
        display.printOnScreen("Welcome to Hangman", "Hangman, the Game");
        
        while (true) {
            Hangman hangman = new Hangman();
            hangman.play();
        }
    }
}
