import javax.swing.*;
import java.awt.*;



public class Main {
    public static void main(String[] args) {
        // Create the main window
        JFrame frame = new JFrame("Photo Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the Controller (PhotoComponent) which manages the PAC components
        PhotoComponent photoController = new PhotoComponent(null); // Start without image

        // Create and set up the menu bar
        frame.setJMenuBar(photoController.createMenuBar());

        // Wrap in scroll pane for better user experience
        JScrollPane scrollPane = new JScrollPane(photoController,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        // Add some UI controls to demonstrate PAC pattern

        // Layout the components
        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);

        // Configure and show the window
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Set focus to enable keyboard interactions
        photoController.requestFocusInWindow();
    }
}
