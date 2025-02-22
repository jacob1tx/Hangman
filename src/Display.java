package src;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Display {
    private static boolean DEBUG = false;
    private static ScratchWork scratchWork;
    private static String targetWord;

    public static void main(String[] args) {
        startNewGame();

        // Object creation
        JFrame frame = new JFrame("Hangman, the Game");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JTextField inputTextField = new JTextField(10);
        inputTextField.setFont(new Font("Times New Roman", Font.PLAIN, 18));

        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new GridLayout(3, 1));

        JTextField workspaceTextField = new JTextField(updateWorkspace(scratchWork));
        workspaceTextField.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        workspaceTextField.setHorizontalAlignment(JTextField.CENTER);
        workspaceTextField.setEditable(false);
        workspaceTextField.setFocusable(false);

        JTextField incorrectGuessesTextField = new JTextField("[ ]");
        incorrectGuessesTextField.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        incorrectGuessesTextField.setHorizontalAlignment(JTextField.CENTER);
        incorrectGuessesTextField.setEditable(false);
        incorrectGuessesTextField.setFocusable(false);
        
        // for debugging
        JTextField targetWordTextField = new JTextField(targetWord);
        targetWordTextField.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        targetWordTextField.setHorizontalAlignment(JTextField.CENTER);
        targetWordTextField.setEditable(false);
        targetWordTextField.setFocusable(false);

        JButton concedeButton = new JButton("Concede");
        JButton guessButton = new JButton("Guess");

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // Action Handlers

        // Handle a guess
        guessButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String inputText = inputTextField.getText();
                handleGuess(inputText);
                workspaceTextField.setText(updateWorkspace(scratchWork));
                incorrectGuessesTextField.setText(updateIncorrectGuesses(scratchWork));
                inputTextField.setText("");
            }
        });

        // Handle a concession
        concedeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gameOver();
            }
        });

        // Handle an `enter` keypress
        inputTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String inputText = inputTextField.getText();
                handleGuess(inputText);
                workspaceTextField.setText(updateWorkspace(scratchWork));
                incorrectGuessesTextField.setText(updateIncorrectGuesses(scratchWork));
                inputTextField.setText("");
            }
        });

        // Field formatting
        messagePanel.add(workspaceTextField);
        messagePanel.add(incorrectGuessesTextField);
        if (DEBUG)
            messagePanel.add(targetWordTextField);

        bottomPanel.add(inputTextField);
        bottomPanel.add(guessButton);
        bottomPanel.add(concedeButton);

        frame.add(messagePanel, BorderLayout.NORTH);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null); // center the window
        frame.setVisible(true);
    }

    public static void handleGuess(String guessString) {
        if (!HangmanCLI.containsOnlyLetters(guessString)) {
            JOptionPane.showMessageDialog(null, "Guesses can only contain letters.", "Invalid Guess", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // guessed a letter
        if (guessString.length() == 1) {
            boolean isSolved = false;
            try {
                isSolved = HangmanCLI.singleLetterGuess(guessString, scratchWork, targetWord);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Invalid Guess", JOptionPane.ERROR_MESSAGE);
            }
            if (isSolved)
                hangmanCompleted();
        }
        // guessed the word
        else if (guessString.length() == targetWord.length()) {
            scratchWork.incrementGuesses();
            if (guessString.equals(targetWord)) // Got it!
                hangmanCompleted();
            String incorrectGuessMessage = "Unfortunately, " + guessString + " is not the word.";
            JOptionPane.showMessageDialog(null, incorrectGuessMessage, "Incorrect Guess", JOptionPane.INFORMATION_MESSAGE);
        }
        else {
            JOptionPane.showMessageDialog(null, "Guesses must be a single letter or the length of the word.", "Invalid Guess", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void startNewGame() {
        try {
            targetWord = HangmanCLI.generateRandomWord();
        } catch (IOException e) {
            e.printStackTrace();
        }
        scratchWork = new ScratchWork(targetWord.length());
    }

    private static String updateWorkspace(ScratchWork scratchWork) {
        ArrayList<Character> correctList = scratchWork.getCorrectLetters();
        StringBuilder correctPrint = new StringBuilder();

        for (Character c : correctList) {
            if (c == null)
                correctPrint.append('_');
            else
                correctPrint.append(c);
            correctPrint.append(' ');
        }
        return correctPrint.toString();
    }

    private static String updateIncorrectGuesses(ScratchWork scratchWork) {
        LinkedHashSet<Character> incorrectList = scratchWork.getIncorrectLetters();
        StringBuilder incorrectPrint = new StringBuilder("[ ");
        for (Character c : incorrectList) {
            if (c != incorrectList.getFirst())
                incorrectPrint.append(", ");
            incorrectPrint.append(c);
        }
        incorrectPrint.append(" ]");
        return incorrectPrint.toString();
    }

    private static void hangmanCompleted() {
        String completeString = String.format("""
                Congratulations! The word was %s.
                You solved it in %d guess%s and made %d mistake%s.
                """, targetWord, scratchWork.getGuessCount(), scratchWork.getGuessCount() > 1 ? "es" : "",  
                scratchWork.getIncorrectGuessCount(), scratchWork.getIncorrectGuessCount() == 1 ? "" : "s");
        JOptionPane.showMessageDialog(null, completeString, "Congratulations", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    private static void gameOver() {
        String concedeString = String.format("The word was %s.", targetWord);
        JOptionPane.showMessageDialog(null, concedeString, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }
}
