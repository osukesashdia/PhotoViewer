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
                    List<Stroke> strokes, List<TextBlock> textBlocks, List<Annotation> annotations, TextBlock currentTextBlock) {
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
                drawTextBlocks(g2, textBlocks, image, currentTextBlock);
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
        int surfaceWidth, surfaceHeight;
        
        if (image != null) {
            // Use photo dimensions
            surfaceWidth = image.getWidth();
            surfaceHeight = image.getHeight();
        } else {
            // Use component dimensions when no image
            surfaceWidth = c.getWidth();
            surfaceHeight = c.getHeight();
        }
        
        // Draw white surface
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, surfaceWidth, surfaceHeight);
        
        // Draw a subtle border to indicate it's the back
        g2.setColor(Color.LIGHT_GRAY);
        g2.setStroke(new java.awt.BasicStroke(2));
        g2.drawRect(0, 0, surfaceWidth - 1, surfaceHeight - 1);

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
    private void drawTextBlocks(Graphics2D g2, List<TextBlock> textBlocks, BufferedImage image, TextBlock currentTextBlock) {
        g2.setColor(Color.BLUE);
        
        for (TextBlock textBlock : textBlocks) {
            if (!textBlock.isEmpty()) {
                int photoWidth = image != null ? image.getWidth() : 0;
                // Pass whether this is the current active text block
                boolean isActive = (textBlock == currentTextBlock);
                textBlock.draw(g2, photoWidth, isActive);
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
            controller.deletePhoto();
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
        
        // Create button group for radio buttons
        ButtonGroup viewGroup = new ButtonGroup();
        
        // Photo menu item (radio button)
        JRadioButtonMenuItem photoItem = new JRadioButtonMenuItem("Photo");
        photoItem.setMnemonic('P');
        photoItem.setAccelerator(KeyStroke.getKeyStroke("ctrl P"));
        photoItem.setSelected(true); // Default selected
        photoItem.addActionListener(e -> {
            // Photo view functionality not implemented yet
        });
        
        // Browse menu item (radio button)
        JRadioButtonMenuItem browseItem = new JRadioButtonMenuItem("Browse");
        browseItem.setMnemonic('B');
        browseItem.setAccelerator(KeyStroke.getKeyStroke("ctrl B"));
        browseItem.addActionListener(e -> {
            // Browse functionality not implemented yet
        });
        
        // Add to button group
        viewGroup.add(photoItem);
        viewGroup.add(browseItem);
        
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
            categoryToggleButton.addActionListener(e -> {
                // Update status bar when toolbar button is clicked
                updateStatusBar(toolBarPanel, category + " button clicked");
            });
            toolBarPanel.add(categoryToggleButton);
        }

        toolBarPanel.setBackground(Color.LIGHT_GRAY);
        return toolBarPanel;
    }

    /**
     * Create status bar with label to show toolbar button clicks
     */
    public JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
        statusBar.setBackground(Color.LIGHT_GRAY);
        
        JLabel statusLabel = new JLabel("Ready");
        statusLabel.setName("statusLabel"); // For easy access
        statusBar.add(statusLabel, BorderLayout.WEST);
        
        return statusBar;
    }

    /**
     * Update status bar message
     */
    private void updateStatusBar(JPanel toolBarPanel, String message) {
        // Find the status bar in the component hierarchy
        JComponent parent = (JComponent) toolBarPanel.getParent();
        while (parent != null) {
            JLabel statusLabel = findStatusLabel(parent);
            if (statusLabel != null) {
                statusLabel.setText(message);
                break;
            }
            parent = (JComponent) parent.getParent();
        }
    }

    /**
     * Helper method to find status label in component hierarchy
     */
    private JLabel findStatusLabel(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JLabel && "statusLabel".equals(comp.getName())) {
                return (JLabel) comp;
            } else if (comp instanceof Container) {
                JLabel found = findStatusLabel((Container) comp);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
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
            } catch (Exception e) {
                // Silently handle error - no dialog
            }
        }
    }

}
