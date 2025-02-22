import javax.swing.JOptionPane;

public class Display {
    public void printOnScreen(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
