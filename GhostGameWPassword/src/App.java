import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        int boardWidth = 360;
        int boardHeight = 640;

        JFrame frame = new JFrame("Ghost Approach");
        frame.setSize(boardWidth, boardHeight);  // Set the size of the window
        frame.setLocationRelativeTo(null);  // Center the window
        frame.setResizable(false);  // Disable resizing
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Exit on close

        ghostApproach ghostApproach = new ghostApproach();
        frame.add(ghostApproach);
        frame.pack();
        ghostApproach.requestFocus();
        frame.setVisible(true);
    }
}
