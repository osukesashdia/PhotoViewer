import javax.swing.*;
import java.awt.*;
import controller.PhotoComponent;
import utils.Constants;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame(Constants.WINDOW_TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        PhotoComponent photoController = new PhotoComponent(null);
        frame.setJMenuBar(photoController.createMenuBar());
        
        JScrollPane scrollPane = new JScrollPane(photoController,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        frame.setLayout(new BorderLayout());
        frame.add(photoController.createToolBar(), BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(photoController.createStatusBar(), BorderLayout.SOUTH);
        photoController.requestFocusInWindow();

        frame.setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
