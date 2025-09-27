import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
/**
 * Controller in PAC pattern
 * Handles user interactions and coordinates between Model and View
 */
public class PhotoComponent extends PACController {
    private final PhotoModel model;  // Abstraction layer
    private final PhotoView view;    // Presentation layer
    private Stroke currentStroke;  // Current stroke being drawn
    private boolean isDrawing;     // Whether currently drawing
    private boolean mousePressed;  // Whether mouse is currently pressed
    private boolean mouseMoved;    // Whether mouse moved after press

    public PhotoComponent(String imagePath) {
        // Initialize PAC components
        this.model = new PhotoModel();
        this.view = new PhotoView(); // No model reference in View

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
        if (imagePath != null) {
            model.loadImage(new File(imagePath));
        } else {
            model.loadImage(null);
        }
        refreshView();
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
        if (!model.hasImage()) {
            // When no image, allow clicking on the default component area
            Dimension componentSize = getSize();
            return x >= 0 && x < componentSize.width && y >= 0 && y < componentSize.height;
        }
        Dimension photoSize = model.getImageDimensions();
        return x >= 0 && x < photoSize.width && y >= 0 && y < photoSize.height;
    }

    /**
     * Controller method: Set text insertion point
     */
    private void setTextInsertionPoint(int x, int y) {
        if (isWithinPhotoBounds(x, y)) {
            model.setTextInsertionPoint(new Point(x, y));
            requestFocusInWindow(); // Request focus for keyboard events
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

    private void setupEventHandlers() {
        // Mouse: drawing, text insertion, and double-click to flip photo
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (model.isFlipped() && e.getButton() == MouseEvent.BUTTON1) {
                    mousePressed = true;
                    mouseMoved = false;
                    // Request focus for keyboard events
                    requestFocusInWindow();
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

        // Keyboard: text typing only
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (model.isFlipped() && model.getCurrentTextBlock() != null) {
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
     * Controller method: Delegate rendering to View with data from Model
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Get data from Model and pass to View (Controller coordinates)
        boolean isFlipped = model.isFlipped();
        BufferedImage image = model.getImage();
        List<Stroke> strokes = model.getStrokes();
        List<TextBlock> textBlocks = model.getTextBlocks();
        List<Annotation> annotations = model.getAnnotations();
        TextBlock currentTextBlock = model.getCurrentTextBlock();
        
        // Delegate rendering to View with data
        view.draw(g, this, isFlipped, image, strokes, textBlocks, annotations, currentTextBlock);
        
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
        // Get data from Model and pass to View
        BufferedImage image = model.getImage();
        return view.getPreferredSize(image);
    }


    /**
     * Controller method: Get model for external access if needed
     */
    @Override
    public PhotoModel getModel() {
        return model;
    }


    /**
     * Controller method: Delegate menu bar creation to Presentation layer
     */
    public JMenuBar createMenuBar() {
        return view.createMenuBar(this);
    }

    /**
     * Controller method: Delegate import dialog to Presentation layer
     */
    public void importImage() {
        view.showImportDialog(this);
    }

    /**
     * Controller method: Delegate toolbar creation to Presentation layer
     */
    public JPanel createToolBar() {
        return view.createToolBar();
    }

    /**
     * Controller method: Delete the current photo
     * This will clear all data and reset the component to empty state
     */
    public void deletePhoto() {
        // Clear all data from model
        model.clearAll();
        
        // Clear current drawing state
        currentStroke = null;
        isDrawing = false;
        mousePressed = false;
        mouseMoved = false;
        
        // Refresh the view to show empty state
        refreshView();
    }

    public JPanel createStatusBar(){
        return view.createStatusBar();
    }
}
