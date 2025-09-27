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
     * Controller method: Handle annotation addition
     */
    public void addAnnotation(String text, int x, int y) {
        if (text != null && !text.isBlank()) {
            model.addAnnotation(new Annotation(text, x, y));
            refreshView();
        }
    }

    /**
     * Controller method: Handle flip toggle (horizontal flip)
     */
    public void toggleFlip() {
        model.toggleFlipped();
        refreshView();
    }

    /**
     * Controller method: Handle photo back/front flip toggle
     */
    public void togglePhotoBack() {
        model.toggleShowingBack();
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
        // Mouse: add annotation and double-click to flip photo
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    // Double-click: flip between photo front and back
                    togglePhotoBack();
                } else if (e.getClickCount() == 1) {
                    // Single-click: add annotation
                    String text = JOptionPane.showInputDialog(
                            PhotoComponent.this,
                            "Enter annotation text:");
                    addAnnotation(text, e.getX(), e.getY());
                }
            }
        });

        // Keyboard: flip
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == 'f') {
                    toggleFlip();
                } else if (e.getKeyChar() == 'b') {
                    // 'B' key to toggle photo back
                    togglePhotoBack();
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
}
