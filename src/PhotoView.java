import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import java.io.File;
/**
 * Presentation layer in PAC pattern
 * Handles all rendering and visual presentation logic
 */
class PhotoView {
    // No direct reference to model - receives data from Controller
    public PhotoView() {
        // Presentation layer doesn't need model reference
    }

    /**
     * Main rendering method - receives data from Controller
     */
    public void draw(Graphics g, JComponent c, boolean isFlipped, BufferedImage image, 
                    List<Stroke> strokes, List<TextBlock> textBlocks, List<Annotation> annotations) {
        Graphics2D g2 = (Graphics2D) g.create();
        
        try {
            // Enable anti-aliasing for better quality
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            // Render background
            drawBackground(g2, c);
            
            // Two rendering paths based on flip state
            if (isFlipped) {
                // Photo back: white surface + strokes + text blocks + annotations
                drawPhotoBack(g2, c, image);
                drawStrokes(g2, strokes);
                drawTextBlocks(g2, textBlocks, image);
                drawAnnotations(g2, annotations, image);
            } else {
                // Photo front: image only (no annotations on front)
                drawPhoto(g2, c, image, isFlipped);
            }
            
        } finally {
            g2.dispose();
        }
    }

    /**
     * Presentation method: Draw checkerboard background
     */
    private void drawBackground(Graphics2D g2, JComponent c) {
        int size = 20;
        for (int y = 0; y < c.getHeight(); y += size) {
            for (int x = 0; x < c.getWidth(); x += size) {
                if (((x / size) + (y / size)) % 2 == 0) {
                    g2.setColor(new Color(220, 220, 220));
                } else {
                    g2.setColor(new Color(200, 200, 200));
                }
                g2.fillRect(x, y, size, size);
            }
        }
    }

    /**
     * Presentation method: Draw the photo with flip transformation
     */
    private void drawPhoto(Graphics2D g2, JComponent c, BufferedImage image, boolean isFlipped) {
        if (image != null) {
            int w = image.getWidth();
            int h = image.getHeight();

            if (isFlipped) {
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-w, 0);
                g2.drawImage(image, tx, null);
            } else {
                g2.drawImage(image, 0, 0, c);
            }
        }
    }

    /**
     * Presentation method: Draw the photo back (white surface)
     */
    private void drawPhotoBack(Graphics2D g2, JComponent c, BufferedImage image) {
        if (image != null) {
            // Draw white surface the same size as the photo
            int photoWidth = image.getWidth();
            int photoHeight = image.getHeight();
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, photoWidth, photoHeight);
            
            // Draw a subtle border to indicate it's the back
            g2.setColor(Color.LIGHT_GRAY);
            g2.setStroke(new java.awt.BasicStroke(2));
            g2.drawRect(0, 0, photoWidth - 1, photoHeight - 1);
            
            // Draw "PHOTO BACK" text in the center
            g2.setColor(Color.GRAY);
            g2.setFont(new Font("Arial", Font.ITALIC, 16));
            String backText = "PHOTO BACK";
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(backText);
            int textHeight = fm.getHeight();
            int x = (photoWidth - textWidth) / 2;
            int y = (photoHeight - textHeight) / 2 + fm.getAscent();
            g2.drawString(backText, x, y);
        }
    }

    /**
     * Presentation method: Draw freehand strokes
     */
    private void drawStrokes(Graphics2D g2, List<Stroke> strokes) {
        for (Stroke stroke : strokes) {
            stroke.draw(g2);
        }
    }

    /**
     * Presentation method: Draw text blocks with word wrap
     */
    private void drawTextBlocks(Graphics2D g2, List<TextBlock> textBlocks, BufferedImage image) {
        g2.setColor(Color.BLUE);
        
        for (TextBlock textBlock : textBlocks) {
            if (!textBlock.isEmpty()) {
                int photoWidth = image != null ? image.getWidth() : 0;
                textBlock.draw(g2, photoWidth);
            }
        }
    }

    /**
     * Presentation method: Draw annotations with word wrapping
     */
    private void drawAnnotations(Graphics2D g2, List<Annotation> annotations, BufferedImage image) {
        for (Annotation ann : annotations) {
            int photoWidth = image != null ? image.getWidth() : 0;
            ann.draw(g2, photoWidth);
        }
    }

    /**
     * Presentation method: Get preferred size based on image data
     */
    public Dimension getPreferredSize(BufferedImage image) {
        if (image != null) {
            return new Dimension(image.getWidth(), image.getHeight());
        }
        return new Dimension(400, 300);
    }

    /**
     * Presentation method: Create menu bar for the photo viewer
     */
    public JMenuBar createMenuBar(PhotoComponent controller) {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        
        // Import menu item
        JMenuItem importItem = new JMenuItem("Import");
        importItem.setMnemonic('I');
        importItem.setAccelerator(KeyStroke.getKeyStroke("ctrl I"));
        importItem.addActionListener(e -> controller.importImage());
        
        // Delete menu item
        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.setMnemonic('D');
        deleteItem.setAccelerator(KeyStroke.getKeyStroke("ctrl D"));
        deleteItem.addActionListener(e -> {
            // Confirm deletion with user
            int result = JOptionPane.showConfirmDialog(controller,
                "Are you sure you want to delete this photo?",
                "Delete Photo",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                controller.deletePhoto();
            }
        });
        
        // Quit menu item
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.setMnemonic('Q');
        quitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
        quitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(importItem);
        fileMenu.add(deleteItem);
        fileMenu.addSeparator();
        fileMenu.add(quitItem);
        
        // View menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');
        
        // Photo menu item
        JMenuItem photoItem = new JMenuItem("Photo");
        photoItem.setMnemonic('P');
        photoItem.setAccelerator(KeyStroke.getKeyStroke("ctrl P"));
        photoItem.addActionListener(e -> {
            // TODO: Implement photo view functionality
        });
        
        // Browse menu item
        JMenuItem browseItem = new JMenuItem("Browse");
        browseItem.setMnemonic('B');
        browseItem.setAccelerator(KeyStroke.getKeyStroke("ctrl B"));
        browseItem.addActionListener(e -> {
            // TODO: Implement browse functionality
        });
        
        viewMenu.add(photoItem);
        viewMenu.add(browseItem);
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        
        return menuBar;
    }

    /**
     * Presentation method: Create toolbar panel
     */
    public JPanel createToolBar() {
        JPanel toolBarPanel = new JPanel();
        String[] categories = {"People", "Foods"};

        for (String category : categories) {
            JToggleButton categoryToggleButton = new JToggleButton(category);
            // TODO: Add action listener when functionality is implemented
            toolBarPanel.add(categoryToggleButton);
        }

        toolBarPanel.setBackground(Color.LIGHT_GRAY);
        return toolBarPanel;
    }

    /**
     * Presentation method: Show import image dialog
     */
    public void showImportDialog(PhotoComponent controller) {
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
                showSuccessDialog(controller, "Image loaded successfully!");
            } catch (Exception e) {
                showErrorDialog(controller, "Error loading image: " + e.getMessage());
            }
        }
    }

    /**
     * Presentation method: Show success dialog
     */
    public void showSuccessDialog(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, 
            message, 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Presentation method: Show error dialog
     */
    public void showErrorDialog(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, 
            message, 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}
