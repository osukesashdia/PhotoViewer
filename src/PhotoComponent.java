import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * Controller in PAC pattern
 * Handles user interactions and coordinates between Model and View
 */
public class PhotoComponent extends PACController {
    private static final Dimension DEFAULT_SIZE = new Dimension(400, 300);

    private PhotoModel model;  // Abstraction layer
    private PhotoView view;    // Presentation layer
    private Stroke currentStroke;  // Current stroke being drawn
    private boolean isDrawing;     // Whether currently drawing
    private boolean mousePressed;  // Whether mouse is currently pressed
    private boolean mouseMoved;    // Whether mouse moved after press

    public PhotoComponent(String imagePath) {
        // Initialize PAC components
        this.model = new PhotoModel();
        this.view = new PhotoView(model);

        // Load initial image if provided
        if (imagePath != null) {
            loadImage(imagePath);
        }

        // Set up user interaction handlers (Controller responsibility)
        setupEventHandlers();
    }

    /**
     * Controller method: Handle image loading
     */
    public void loadImage(String imagePath) {
        model.loadImage(new File(imagePath));
        refreshView();
    }

    /**
     * Controller method: Handle annotation addition (only on back side)
     */
    public void addAnnotation(String text, int x, int y) {
        if (text != null && !text.isBlank()) {
            model.addAnnotation(new Annotation(text, x, y));
            refreshView();
        }
    }

    /**
     * Controller method: Start drawing a new stroke
     */
    private void startDrawing(int x, int y) {
        if (isWithinPhotoBounds(x, y)) {
            currentStroke = new Stroke(Color.BLACK, 2.0f);
            currentStroke.addPoint(x, y);
            isDrawing = true;
        }
    }

    /**
     * Controller method: Continue drawing the current stroke
     */
    private void continueDrawing(int x, int y) {
        if (isDrawing && currentStroke != null && isWithinPhotoBounds(x, y)) {
            currentStroke.addPoint(x, y);
            repaint(); // Immediate feedback
        }
    }

    /**
     * Controller method: Finish drawing the current stroke
     */
    private void finishDrawing() {
        if (isDrawing && currentStroke != null && !currentStroke.isEmpty()) {
            model.addStroke(currentStroke);
            refreshView();
        }
        currentStroke = null;
        isDrawing = false;
    }

    /**
     * Controller method: Check if point is within photo bounds
     */
    private boolean isWithinPhotoBounds(int x, int y) {
        if (!model.hasImage()) return false;
        Dimension photoSize = model.getImageDimensions();
        return x >= 0 && x < photoSize.width && y >= 0 && y < photoSize.height;
    }

    /**
     * Controller method: Set text insertion point
     */
    private void setTextInsertionPoint(int x, int y) {
        if (isWithinPhotoBounds(x, y)) {
            model.setTextInsertionPoint(new Point(x, y));
            repaint();
        }
    }

    /**
     * Controller method: Handle flip toggle (photo front/back)
     */
    public void toggleFlip() {
        model.toggleFlipped();
        refreshView();
    }

    /**
     * Controller method: Refresh the view
     */
    @Override
    protected void refreshView() {
        revalidate();
        repaint();
    }

    /**
     * Controller method: Handle user input events
     */
    @Override
    protected void handleUserInput() {
        // This method is called by setupEventHandlers
        // The actual input handling is done in the event listeners
    }

    /**
     * Controller method: Set up all event handlers
     */
    private void setupEventHandlers() {
        // Mouse: drawing, text insertion, and double-click to flip photo
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (model.isFlipped() && e.getButton() == MouseEvent.BUTTON1) {
                    mousePressed = true;
                    mouseMoved = false;
                    // Don't start drawing yet - wait to see if mouse moves
                }
            }
            
            public void mouseReleased(MouseEvent e) {
                if (mousePressed && model.isFlipped() && e.getButton() == MouseEvent.BUTTON1) {
                    if (mouseMoved && isDrawing) {
                        // Mouse was dragged - finish drawing
                        finishDrawing();
                    } else if (!mouseMoved) {
                        // Mouse was clicked without movement - set text insertion point
                        setTextInsertionPoint(e.getX(), e.getY());
                    }
                    mousePressed = false;
                    mouseMoved = false;
                }
            }
            
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    // Double-click: flip between photo front and back
                    toggleFlip();
                }
            }
        });
        
        // Mouse motion: detect drawing and continue drawing
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (mousePressed && model.isFlipped()) {
                    mouseMoved = true;
                    if (!isDrawing) {
                        // First movement - start drawing
                        startDrawing(e.getX(), e.getY());
                    } else {
                        // Continue drawing
                        continueDrawing(e.getX(), e.getY());
                    }
                }
            }
        });

        // Keyboard: flip and text typing
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == 'f') {
                    toggleFlip();
                } else if (model.isFlipped() && model.getCurrentTextBlock() != null) {
                    // Handle text typing
                    if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                        model.backspaceCurrentText();
                        repaint();
                    } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        model.commitCurrentText();
                        repaint();
                    } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        model.setTextInsertionPoint(null);
                        repaint();
                    } else if (e.getKeyChar() >= 32 && e.getKeyChar() <= 126) {
                        // Printable characters
                        model.addToCurrentText(e.getKeyChar());
                        repaint();
                    }
                }
            }
        });
    }

    /**
     * Controller method: Delegate rendering to View
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        view.draw(g, this);
        
        // Draw current stroke being drawn for immediate feedback
        if (isDrawing && currentStroke != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            currentStroke.draw(g2);
            g2.dispose();
        }
    }

    /**
     * Controller method: Get preferred size based on model data
     */
    @Override
    public Dimension getPreferredSize() {
        if (model.getImage() != null) {
            return new Dimension(model.getImage().getWidth(), model.getImage().getHeight());
        }
        return DEFAULT_SIZE;
    }

    /**
     * Controller method: Load new photo (public API)
     */
    public void loadNewPhoto(String path) {
        loadImage(path);
    }

    /**
     * Controller method: Get model for external access if needed
     */
    @Override
    public PhotoModel getModel() {
        return model;
    }

    /**
     * Controller method: Get view for external access if needed
     */
    @Override
    public PhotoView getView() {
        return view;
    }

    /**
     * Controller method: Create menu bar for the photo viewer
     */
    public JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        
        // Import menu item
        JMenuItem importItem = new JMenuItem("Import");
        importItem.setMnemonic('I');
        importItem.setAccelerator(KeyStroke.getKeyStroke("ctrl I"));
        importItem.addActionListener(e -> importImage());
        
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
        flipItem.addActionListener(e -> toggleFlip());
        
        // Clear annotations menu item
        JMenuItem clearItem = new JMenuItem("Clear Annotations");
        clearItem.setMnemonic('C');
        clearItem.setAccelerator(KeyStroke.getKeyStroke("ctrl C"));
        clearItem.addActionListener(e -> {
            model.clearAnnotations();
            repaint();
        });
        
        // Clear strokes menu item
        JMenuItem clearStrokesItem = new JMenuItem("Clear Strokes");
        clearStrokesItem.setMnemonic('S');
        clearStrokesItem.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        clearStrokesItem.addActionListener(e -> {
            model.clearStrokes();
            repaint();
        });
        
        // Clear text blocks menu item
        JMenuItem clearTextItem = new JMenuItem("Clear Text Blocks");
        clearTextItem.setMnemonic('T');
        clearTextItem.setAccelerator(KeyStroke.getKeyStroke("ctrl T"));
        clearTextItem.addActionListener(e -> {
            model.clearTextBlocks();
            repaint();
        });
        
        // Clear all menu item
        JMenuItem clearAllItem = new JMenuItem("Clear All");
        clearAllItem.setMnemonic('A');
        clearAllItem.setAccelerator(KeyStroke.getKeyStroke("ctrl A"));
        clearAllItem.addActionListener(e -> {
            model.clearAll();
            repaint();
        });
        
        viewMenu.add(flipItem);
        viewMenu.addSeparator();
        viewMenu.add(clearItem);
        viewMenu.add(clearStrokesItem);
        viewMenu.add(clearTextItem);
        viewMenu.add(clearAllItem);
        
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
     * Controller method: Import image using file chooser
     */
    public void importImage() {
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
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                loadImage(selectedFile.getAbsolutePath());
                JOptionPane.showMessageDialog(this, 
                    "Image loaded successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading image: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Controller method: Show about dialog
     */
    private void showAboutDialog() {
        String message = "Photo Viewer - PAC Pattern Demo\n\n" +
                        "This application demonstrates the PAC (Presentation-Abstraction-Controller) pattern:\n\n" +
                        "• Controller: PhotoComponent\n" +
                        "• Abstraction: PhotoModel\n" +
                        "• Presentation: PhotoView\n\n" +
                        "Features:\n" +
                        "• Import and view images\n" +
                        "• Draw freehand strokes on the back side\n" +
                        "• Type text directly on the back side\n" +
                        "• Double-click or press 'F' to flip between front and back\n" +
                        "• Clear annotations, strokes, or all content\n\n" +
                        "Drawing Controls:\n" +
                        "• Click and drag: Draw freehand strokes\n" +
                        "• Click (no drag): Set text insertion point\n" +
                        "• Type: Add text at insertion point with word wrap\n" +
                        "• Enter: Commit text as annotation\n" +
                        "• Escape: Cancel text insertion\n" +
                        "• Double-click: Flip photo\n\n" +
                        "Note: Drawing and text are only visible on the back side,\n" +
                        "just like writing on the back of a physical photo!\n\n" +
                        "Built with Java Swing";
        
        JOptionPane.showMessageDialog(null, message, "About Photo Viewer", JOptionPane.INFORMATION_MESSAGE);
    }
}
