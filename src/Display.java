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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Display {
    private ScratchWork scratchWork;
    private String targetWord;
    private Difficulty difficulty;
    private int MAXGUESSES = 6;
    private JFrame frame;
    private boolean DEBUG = false;
    
        enum Difficulty {
            EASY,
            HARD
        }

        public void startNewGame() {
            difficulty = null;
            try {
                targetWord = HangmanCLI.generateRandomWord();
            } catch (IOException e) {
                e.printStackTrace();
            }
            scratchWork = new ScratchWork(targetWord.length());
    
            JFrame welcomeFrame = new JFrame();
            welcomeFrame.setTitle("Select Difficulty");
            welcomeFrame.setSize(300, 150);
            welcomeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            welcomeFrame.setLayout(new BorderLayout());
    
            // Welcome Message
            JLabel welcomeLabel = new JLabel("<html>Welcome to Hangman!<br>Select a difficulty below.<html>", JLabel.CENTER);
            welcomeLabel.setFont(new Font("Times New Roman", Font.PLAIN, 18));
            welcomeFrame.add(welcomeLabel, BorderLayout.NORTH);
    
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    
            // Easy button
            JButton easyButton = new JButton("Easy");
            easyButton.setToolTipText("Play for fun with unlimited mistakes");
            easyButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    difficulty = Difficulty.EASY; // Set difficulty to easy
                    welcomeFrame.dispose();
                }
            });
    
            // Hard button
            JButton hardButton = new JButton("Hard");
            hardButton.setToolTipText("Challenge yourself with only 6 mistakes");
            hardButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    difficulty = Difficulty.HARD; // Set difficulty to hard
                    welcomeFrame.dispose();
                }
            });
    
            // Add buttons to the button panel
            buttonPanel.add(easyButton);
            buttonPanel.add(hardButton);
    
            welcomeFrame.add(buttonPanel, BorderLayout.SOUTH);
    
            welcomeFrame.setLocationRelativeTo(null); // Center the dialog on the screen
            welcomeFrame.setVisible(true);
            
            // start the game
            displayHangman();
        }
    
        private void displayHangman() {
            // Wait for a difficulty selection to be made
            while (difficulty == null) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    
            // Object creation
            frame = new JFrame("Hangman, the Game");
            frame.setSize(400, difficulty == Difficulty.EASY ? 150 : 200);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
    
            JTextField inputTextField = new JTextField(10);
            inputTextField.setFont(new Font("Times New Roman", Font.PLAIN, 18));
    
            JPanel messagePanel = new JPanel();
            messagePanel.setLayout(new GridLayout(3, 1));
    
            JLabel workspaceLabel = new JLabel(updateWorkspace(scratchWork));
            workspaceLabel.setFont(new Font("Times New Roman", Font.PLAIN, 18));
            workspaceLabel.setHorizontalAlignment(JTextField.CENTER);
            workspaceLabel.setFocusable(false);
    
            JLabel incorrectGuessesLabel = new JLabel("[ ]");
            incorrectGuessesLabel.setFont(new Font("Times New Roman", Font.PLAIN, 18));
            incorrectGuessesLabel.setHorizontalAlignment(JTextField.CENTER);
            incorrectGuessesLabel.setFocusable(false);

            // for hard mode
            JLabel remainingGuessCountLabel = new JLabel(String.valueOf(MAXGUESSES - scratchWork.getIncorrectGuessCount()));
            remainingGuessCountLabel.setFont(new Font("Times New Roman", Font.PLAIN, 18));
            remainingGuessCountLabel.setHorizontalAlignment(JTextField.CENTER);
            remainingGuessCountLabel.setFocusable(false);
            
            // for debugging
            JLabel targetWordLabel = new JLabel(targetWord);
            targetWordLabel.setFont(new Font("Times New Roman", Font.PLAIN, 18));
            targetWordLabel.setHorizontalAlignment(JTextField.CENTER);
            targetWordLabel.setFocusable(false);
    
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
                    workspaceLabel.setText(updateWorkspace(scratchWork));
                    incorrectGuessesLabel.setText(updateIncorrectGuesses(scratchWork));
                    inputTextField.setText("");
                    if (difficulty == Difficulty.HARD)
                        remainingGuessCountLabel.setText(String.valueOf(MAXGUESSES - scratchWork.getIncorrectGuessCount()));
                }
            });
    
            // Handle an `enter` keypress
            inputTextField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String inputText = inputTextField.getText();
                    handleGuess(inputText);
                    workspaceLabel.setText(updateWorkspace(scratchWork));
                    incorrectGuessesLabel.setText(updateIncorrectGuesses(scratchWork));
                    inputTextField.setText("");
                    if (difficulty == Difficulty.HARD)
                        remainingGuessCountLabel.setText(String.valueOf(MAXGUESSES - scratchWork.getIncorrectGuessCount()));
                }
            });
 
            // Handle a concession
            concedeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    gameOver();
                }
            });
    
            // Field formatting
            messagePanel.add(workspaceLabel);
            messagePanel.add(incorrectGuessesLabel);
            if (DEBUG)
                messagePanel.add(targetWordLabel);
    
            bottomPanel.add(inputTextField);
            bottomPanel.add(guessButton);
            bottomPanel.add(concedeButton);
            
            frame.add(messagePanel, BorderLayout.NORTH);
            frame.add(bottomPanel, BorderLayout.SOUTH);
            if (difficulty == Difficulty.HARD)
                frame.add(remainingGuessCountLabel, BorderLayout.CENTER);
    
            frame.setLocationRelativeTo(null); // center the window
            frame.setVisible(true);
        }
    
        public void handleGuess(String guessString) {
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
            else
                JOptionPane.showMessageDialog(null, "Guesses must be a single letter or the length of the word.", "Invalid Guess", JOptionPane.ERROR_MESSAGE);
            
            if (difficulty == Difficulty.HARD && scratchWork.getIncorrectGuessCount() >= MAXGUESSES) // out of guesses
                gameOver();
    }

    private String updateWorkspace(ScratchWork scratchWork) {
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

    private String updateIncorrectGuesses(ScratchWork scratchWork) {
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

    private void hangmanCompleted() {
        String completeString = String.format("""
                Congratulations! The word was %s.
                You solved it in %d guess%s and made %d mistake%s.
                """, targetWord, scratchWork.getGuessCount(), scratchWork.getGuessCount() > 1 ? "es" : "",  
                scratchWork.getIncorrectGuessCount(), scratchWork.getIncorrectGuessCount() == 1 ? "" : "s");
        JOptionPane.showMessageDialog(null, completeString, "Congratulations", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    private void gameOver() {
        String concedeString = String.format("The word was %s.", targetWord);
        JOptionPane.showMessageDialog(null, concedeString, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }
}
