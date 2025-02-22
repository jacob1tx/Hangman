package src;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class HangmanCLI {
                  
    private ScratchWork scratchWork;
    private String targetWord;

    public HangmanCLI() {
        try {
            targetWord = generateRandomWord();
        } catch (IOException e) {
            e.printStackTrace();
        }
        scratchWork = new ScratchWork(targetWord.length());
        
        System.out.printf("Your word has %d letters.\n", targetWord.length());
    }

    public void play(Scanner scanner) {
        while(true) {
            prettyPrint(scratchWork);
            System.out.print("Try a letter, or guess the word: ");
            String guessString = scanner.nextLine().trim().toLowerCase();

            // exit string
            if (guessString.equals("exit"))
                System.exit(0);

            if (guessString.equals("concede")) {
                System.out.printf("The word was %s.\n", targetWord);
                return;
            }

            if (!containsOnlyLetters(guessString)) {
                System.out.println("Invalid: Guesses can only contain letters");
                continue;
            }
            
            // guessed a letter
            if (guessString.length() == 1) {
                boolean isSolved = false;
                try {
                    isSolved = singleLetterGuess(guessString, scratchWork, targetWord);
                } catch (Exception e) {
                    System.out.println("Invalid: " + e.getMessage());
                }
                if (isSolved)
                    break;
            }
            // guessed the word
            else if (guessString.length() == targetWord.length()) {
                scratchWork.incrementGuesses();
                if (guessString.equals(targetWord)) // Got it!
                    break;
                System.out.printf("Unfortunately, %s is not the word\n", guessString);
            }
            else {
                System.out.println("Invalid: Guesses must be a single letter or the length of the word");
            }
        }
        
        System.out.printf("\nCongratulations! The word was %s.\n", targetWord);
        System.out.printf("You solved it in %d guess%s and made %d mistake%s.\n", 
            scratchWork.getGuessCount(), scratchWork.getGuessCount() > 1 ? "es" : "",  
            scratchWork.getIncorrectGuessCount(), scratchWork.getIncorrectGuessCount() == 1 ? "" : "s");
    }

    // The string is free of whitespace and already in lower case
    public static boolean containsOnlyLetters(String guessString) {
        for (int i = 0; i < guessString.length(); i++) {
            char c = guessString.charAt(i);
            if (c < 'a' || c > 'z')
                return false;
        }
        return true;
    }
    
    // Generate a word at random from the EOWL dictionary
    public static String generateRandomWord() throws IOException {
        String filePath = "EOWL-v1.1.2\\EOWL-v1.1.2\\CSV Format\\";

        Random randInt = new Random();
        char startingLetter = (char) ('A' + randInt.nextInt(26)); // Get a random letter of the alphabet

        filePath += startingLetter + " Words.csv";        
        
        List<String> words = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                words.add(line);
            }
        }
        
        String generateWord = "";
        do {
            generateWord = words.get(randInt.nextInt(words.size())); // generate a random word using the chosen starting letter
        } while (generateWord.equals("exit") && generateWord.equals("concede")); // ensure generate word is not a command
        return generateWord;
    }

    // Function to handle the guess of a single letter
    public static boolean singleLetterGuess(String guessString, ScratchWork scratchWork, String targetWord) throws Exception {
        char letter = guessString.charAt(0);
        if (scratchWork.getAllGuesses().contains(letter)) // verify this has not been guessed
            throw new Exception(letter + " has already been guessed");
        
        // Add to guess count
        scratchWork.setAllGueses(letter);

        // Incorrect guess
        if (targetWord.indexOf(letter) < 0) {
            scratchWork.setIncorrectLetter(letter);
            return false;
        }

        // Correct guess
        ArrayList<Integer> positions = new ArrayList<>();
        for (int i = 0; i < targetWord.length(); i++) {
            char c = targetWord.charAt(i);
            if (c == letter)
                positions.add(i);
        }
        
        scratchWork.setCorrectLetters(letter, positions);
        
        return scratchWork.isSolved();
    }

    
    private static void prettyPrint(ScratchWork scratchWork) {
        ArrayList<Character> correctList = scratchWork.getCorrectLetters();
        LinkedHashSet<Character> incorrectList = scratchWork.getIncorrectLetters();
        StringBuilder correctPrint = new StringBuilder();
        StringBuilder incorrectPrint = new StringBuilder("[ ");

        for (Character c : correctList) {
            if (c == null)
                correctPrint.append('_');
            else
                correctPrint.append(c);
            correctPrint.append(' ');
        }

        for (Character c : incorrectList) {
            if (c != incorrectList.getFirst())
                incorrectPrint.append(", ");
            incorrectPrint.append(c);
        }
        incorrectPrint.append(" ]");

        System.out.printf("""
                
                %s

                Incorrect Letters: %s

                """, correctPrint, incorrectPrint);
    }
}
