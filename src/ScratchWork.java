package src;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class ScratchWork {
    private int length;
    private int guessCount;
    private ArrayList<Character> correctLetters;
    private LinkedHashSet<Character> incorrectLetters;
    private LinkedHashSet<Character> allGuesses;

    public ScratchWork(int length) {
        this.length = length;
        guessCount = 0;
        correctLetters = new ArrayList<>(length);
        incorrectLetters = new LinkedHashSet<>(26);
        allGuesses = new LinkedHashSet<>(26);

        for (int i = 0; i < length; i++) {
            correctLetters.add(null);
        }
    }

    // Getters
    public ArrayList<Character> getCorrectLetters() {
        return correctLetters;
    }

    public LinkedHashSet<Character> getIncorrectLetters() {
        return incorrectLetters;
    }

    public LinkedHashSet<Character> getAllGuesses() {
        return allGuesses;
    }

    public int getIncorrectGuessCount() {
        return incorrectLetters.size();
    }

    public int getGuessCount() {
        return guessCount;
    }

    // Setters
    public void setCorrectLetters(char letter, ArrayList<Integer> positions) throws Exception {
        for (int pos : positions) {
            if (pos >= length || pos < 0)
                throw new Exception("Invalid index: " + pos + " must be between 0 and " + length);
            
            correctLetters.set(pos, letter);
        }
    }

    public void setIncorrectLetter(char letter) {
        incorrectLetters.add(letter);
    }

    public void setAllGueses(char letter) {
        allGuesses.add(letter);
        incrementGuesses();
    }

    // Misc
    public boolean isSolved() {
        for (Character i : correctLetters) {
            if (i == null)
                return false;
        }
        return true;
    }

    public void incrementGuesses() {
        guessCount++;
    }
}
