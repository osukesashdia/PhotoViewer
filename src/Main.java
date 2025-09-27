import javax.swing.*;
import java.awt.*;



public class Main {
    private static JFrame frame;
    private static JScrollPane scrollPane;
    private static PhotoComponent photoController;

    public static void main(String[] args) {
        // Create the main window
        frame = new JFrame("Photo Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create initial photo component
        photoController = new PhotoComponent(null); // Start without image

        // Create and set up the menu bar
        frame.setJMenuBar(photoController.createMenuBar());

        // Wrap in scroll pane for better user experience
        scrollPane = new JScrollPane(photoController,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        // Layout the components
        frame.setLayout(new BorderLayout());
        frame.add(photoController.createToolBar(), BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Set focus to enable keyboard interactions
        photoController.requestFocusInWindow();

        // Configure and show the window
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


}
