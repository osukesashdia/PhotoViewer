import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Main application class demonstrating PAC pattern
 * Creates and configures the PhotoViewer with proper PAC architecture
 */
public class Main {
    public static void main(String[] args) {
        // Create the main window
        JFrame frame = new JFrame("Photo Viewer - PAC Pattern Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the Controller (PhotoComponent) which manages the PAC components
        PhotoComponent photoController = new PhotoComponent(null); // Start without image

        // Create and set up the menu bar
        frame.setJMenuBar(createMenuBar(photoController));
        
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


    /**
     * Create menu bar with Import functionality
     */
    private static JMenuBar createMenuBar(PhotoComponent controller) {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        
        // Import menu item
        JMenuItem importItem = new JMenuItem("Import");
        importItem.setMnemonic('I');
        importItem.setAccelerator(KeyStroke.getKeyStroke("ctrl I"));
        importItem.addActionListener(e -> importImage(controller));
        
        // Exit menu item
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic('E');
        exitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(importItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // View menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');
        
        // Flip menu item (photo front/back)
        JMenuItem flipItem = new JMenuItem("Flip Photo (F)");
        flipItem.setMnemonic('F');
        flipItem.setAccelerator(KeyStroke.getKeyStroke("F"));
        flipItem.addActionListener(e -> controller.toggleFlip());
        
        // Clear annotations menu item
        JMenuItem clearItem = new JMenuItem("Clear Annotations");
        clearItem.setMnemonic('C');
        clearItem.setAccelerator(KeyStroke.getKeyStroke("ctrl C"));
        clearItem.addActionListener(e -> {
            controller.getModel().clearAnnotations();
            controller.repaint();
        });
        
        viewMenu.add(flipItem);
        viewMenu.add(clearItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.setMnemonic('A');
        aboutItem.addActionListener(e -> showAboutDialog());
        
        helpMenu.add(aboutItem);
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
        
        return menuBar;
    }

    /**
     * Import image using file chooser
     */
    private static void importImage(PhotoComponent controller) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Image File");
        
        // Set file filter for image files
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                String name = f.getName().toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
                       name.endsWith(".png") || name.endsWith(".gif") || 
                       name.endsWith(".bmp");
            }
            
            @Override
            public String getDescription() {
                return "Image Files (*.jpg, *.jpeg, *.png, *.gif, *.bmp)";
            }
        });
        
        // Set current directory to the img folder if it exists
        File imgDir = new File("img");
        if (imgDir.exists()) {
            fileChooser.setCurrentDirectory(imgDir);
        }
        
        int result = fileChooser.showOpenDialog(controller);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                controller.loadImage(selectedFile.getAbsolutePath());
                JOptionPane.showMessageDialog(controller, 
                    "Image loaded successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(controller, 
                    "Error loading image: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Show about dialog
     */
    private static void showAboutDialog() {
        String message = "Photo Viewer - PAC Pattern Demo\n\n" +
                        "This application demonstrates the PAC (Presentation-Abstraction-Controller) pattern:\n\n" +
                        "• Controller: PhotoComponent\n" +
                        "• Abstraction: PhotoModel\n" +
                        "• Presentation: PhotoView\n\n" +
                        "Features:\n" +
                        "• Import and view images\n" +
                        "• Add annotations ONLY on the back side\n" +
                        "• Double-click or press 'F' to flip between front and back\n" +
                        "• Clear annotations for current side\n\n" +
                        "Note: Annotations are only visible on the back side,\n" +
                        "just like writing on the back of a physical photo!\n\n" +
                        "Built with Java Swing";
        
        JOptionPane.showMessageDialog(null, message, "About Photo Viewer", JOptionPane.INFORMATION_MESSAGE);
    }
}
