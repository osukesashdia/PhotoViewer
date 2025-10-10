package controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import interfaces.IPhotoModel;
import interfaces.IPhotoView;
import model.PhotoModel;
import model.TextBlock;
import model.Stroke;
import model.Annotation;
import view.PhotoView;
import utils.Constants;

public class PhotoComponent extends PACController {
    private final IPhotoModel model;  
    private final IPhotoView view;    
    private Stroke currentStroke;  
    private boolean isDrawing;     
    private boolean mousePressed;  
    private boolean mouseMoved;

    public PhotoComponent(String imagePath) {
        this.model = new PhotoModel();
        this.view = new PhotoView(); 
        setupEventHandlers();
    }

    public void toggleFlip() {
        model.toggleFlipped();
        refreshView();
    }

    public void toggleAnnotationsVisible() {
        model.toggleAnnotationsVisible();
        refreshView();
    }
    
    public void setAnnotationColor(Color color) {
        Object selectedObject = model.getSelectedObject();
        if (selectedObject != null) {
            if (selectedObject instanceof Annotation) {
                ((Annotation) selectedObject).setColor(color);
            } else if (selectedObject instanceof TextBlock) {
                ((TextBlock) selectedObject).setColor(color);
            }
            // Strokes are not selectable, so no color change needed
            refreshView();
        }
    }

    // Hit testing methods
    private Object findObjectAt(int x, int y, int photoWidth) {
        // Strokes are not selectable - skip stroke hit testing
        // for (Stroke stroke : model.getStrokes()) {
        //     if (stroke.containsPoint(x, y)) {
        //         return stroke;
        //     }
        // }
        
        // Check text blocks
        for (TextBlock textBlock : model.getTextBlocks()) {
            // Don't select empty TextBlocks that are currently being edited
            if (textBlock == model.getCurrentTextBlock() && textBlock.isEmpty()) {
                continue;
            }
            if (textBlock.containsPoint(x, y, photoWidth)) {
                return textBlock;
            }
        }
        
        // Check annotations
        for (Annotation annotation : model.getAnnotations()) {
            if (annotation.containsPoint(x, y, photoWidth)) {
                return annotation;
            }
        }
        
        return null;
    }

    private Point getObjectPosition(Object obj) {
        if (obj instanceof Stroke) {
            return ((Stroke) obj).getCenter();
        } else if (obj instanceof TextBlock) {
            return ((TextBlock) obj).getPosition();
        } else if (obj instanceof Annotation) {
            return ((Annotation) obj).getPosition();
        }
        return new Point(0, 0);
    }

    private void moveObject(Object obj, int dx, int dy) {
        if (obj instanceof Stroke) {
            ((Stroke) obj).moveBy(dx, dy);
        } else if (obj instanceof TextBlock) {
            ((TextBlock) obj).moveBy(dx, dy);
        } else if (obj instanceof Annotation) {
            ((Annotation) obj).moveBy(dx, dy);
        }
        refreshView();
    }

    public void loadImage(File imageFile) {
        model.loadImage(imageFile);
        refreshView();
    }

    private void startDrawing(int x, int y) {
        if (isWithinPhotoBounds(x, y)) {
            currentStroke = new Stroke(Constants.STROKE_COLOR, Constants.STROKE_WIDTH);
            currentStroke.addPoint(x, y);
            isDrawing = true;
        }
    }

    private void continueDrawing(int x, int y) {
        if (isDrawing && currentStroke != null && isWithinPhotoBounds(x, y)) {
            currentStroke.addPoint(x, y);
            repaint(); 
        }
    }

    private void finishDrawing() {
        if (isDrawing && currentStroke != null && !currentStroke.isEmpty()) {
            model.addStroke(currentStroke);
            refreshView();
        }
        currentStroke = null;
        isDrawing = false;
    }

    private boolean isWithinPhotoBounds(int x, int y) {
        if (!model.hasImage()) {
            Dimension componentSize = getSize();
            return x >= 0 && x < componentSize.width && y >= 0 && y < componentSize.height;
        }
        Dimension photoSize = model.getImageDimensions();
        return x >= 0 && x < photoSize.width && y >= 0 && y < photoSize.height;
    }

    private void setTextInsertionPoint(int x, int y) {
        if (isWithinPhotoBounds(x, y)) {
            model.setTextInsertionPoint(new Point(x, y));
            requestFocusInWindow(); 
            repaint();
        }
    }

    @Override
    protected void refreshView() {
        revalidate();
        repaint();
    }

    private void setupEventHandlers() {
        setupMouseHandlers();
        setupKeyboardHandlers();
        setFocusable(true);
    }
    
    private void setupMouseHandlers() {
        addMouseListener(createMouseListener());
        addMouseMotionListener(createMouseMotionListener());
    }
    
    private MouseAdapter createMouseListener() {
        return new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }
            public void mouseReleased(MouseEvent e) {
                handleMouseReleased(e);
            }
            public void mouseClicked(MouseEvent e) {
                handleMouseClicked(e);
            }
        };
    }
    
    private MouseMotionAdapter createMouseMotionListener() {
        return new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
            }
        };
    }
    
    private void handleMousePressed(MouseEvent e) {
        if (!model.isAnnotationsVisible() || e.getButton() != MouseEvent.BUTTON1) {
            return;
        }
        
        mousePressed = true;
        mouseMoved = false;
        requestFocusInWindow();
        
        int x = e.getX();
        int y = e.getY();
        
        // Get photo width for accurate bounds calculation
        BufferedImage image = model.getImage();
        int photoWidth = (image != null) ? image.getWidth() : 0;
        
        // Natural interaction: always try to select first
        Object clickedObject = findObjectAt(x, y, photoWidth);
        model.setSelectedObject(clickedObject);
        
        if (clickedObject != null) {
            // Clicked on an object - prepare for potential dragging
            Point objectPos = getObjectPosition(clickedObject);
            model.setDragOffset(new Point(x - objectPos.x, y - objectPos.y));
        } else if (isWithinPhotoBounds(x, y)) {
            // Clicked on empty space - prepare for potential drawing (don't start yet)
            // Drawing will start only if user drags
        }
        
        refreshView();
    }
    
    private void handleMouseReleased(MouseEvent e) {
        if (!mousePressed || !model.isAnnotationsVisible() || e.getButton() != MouseEvent.BUTTON1) {
            return;
        }
        
        int x = e.getX();
        int y = e.getY();
        
        if (mouseMoved && isDrawing) {
            // Finished drawing a stroke
            finishDrawing();
        } else if (!mouseMoved && model.getSelectedObject() == null && isWithinPhotoBounds(x, y)) {
            // Clicked empty space without moving - create text block
            setTextInsertionPoint(x, y);
        } else if (mouseMoved && model.getSelectedObject() != null) {
            // Finished moving an object
            model.setDragging(false);
        }
        
        mousePressed = false;
        mouseMoved = false;
        refreshView();
    }
    
    private void handleMouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            toggleAnnotationsVisible();
        }
    }
    
    private void handleMouseDragged(MouseEvent e) {
        if (!mousePressed || !model.isAnnotationsVisible()) {
            return;
        }
        
        mouseMoved = true;
        int x = e.getX();
        int y = e.getY();
        
        if (isDrawing) {
            // Continue drawing stroke
            continueDrawing(x, y);
        } else if (model.getSelectedObject() != null) {
            // Start dragging if we have a selected object
            if (!model.isDragging()) {
                model.setDragging(true);
            }
            // Move the selected object
            Point dragOffset = model.getDragOffset();
            int dx = x - dragOffset.x - getObjectPosition(model.getSelectedObject()).x;
            int dy = y - dragOffset.y - getObjectPosition(model.getSelectedObject()).y;
            
            moveObject(model.getSelectedObject(), dx, dy);
        } else if (isWithinPhotoBounds(x, y)) {
            // Start drawing stroke when dragging on empty space
            if (!isDrawing) {
                startDrawing(x, y);
            }
        }
    }
    
    private void setupKeyboardHandlers() {
        addKeyListener(createKeyListener());
    }
    
    private KeyAdapter createKeyListener() {
        return new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                handleKeyPressed(e);
            }
        };
    }
    
    private void handleKeyPressed(KeyEvent e) {
        if (!model.isAnnotationsVisible()) {
            return;
        }
        
        // Handle text input for current text block (if actively editing)
        if (model.getCurrentTextBlock() != null) {
            handleTextInput(e);
        }
        // Handle text input for selected annotation (natural editing)
        else if (model.getSelectedObject() instanceof Annotation) {
            Annotation annotation = (Annotation) model.getSelectedObject();
            handleAnnotationTextInput(e, annotation);
        }
        // Handle text input for selected text block (natural editing)
        else if (model.getSelectedObject() instanceof TextBlock) {
            TextBlock textBlock = (TextBlock) model.getSelectedObject();
            model.setCurrentTextBlock(textBlock);
            handleTextInput(e);
        }
    }
    
    private void handleTextInput(KeyEvent e) {
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
            model.addToCurrentText(e.getKeyChar());
            repaint();
        }
    }

    private void handleAnnotationTextInput(KeyEvent e, Annotation annotation) {
        // Automatically start editing when user types
        if (!annotation.isEditing()) {
            annotation.setEditing(true);
        }
        
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            annotation.backspace();
            repaint();
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            annotation.setEditing(false);
            repaint();
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            annotation.setEditing(false);
            model.setSelectedObject(null);
            repaint();
        } else if (e.getKeyChar() >= 32 && e.getKeyChar() <= 126) {
            annotation.addCharacter(e.getKeyChar());
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        boolean isFlipped = model.isFlipped();
        boolean annotationsVisible = model.isAnnotationsVisible();
        BufferedImage image = model.getImage();
        List<Stroke> strokes = model.getStrokes();
        List<TextBlock> textBlocks = model.getTextBlocks();
        List<Annotation> annotations = model.getAnnotations();
        TextBlock currentTextBlock = model.getCurrentTextBlock();
        Object selectedObject = model.getSelectedObject();
        view.draw(g, this, isFlipped, annotationsVisible, image, strokes, textBlocks, annotations, currentTextBlock, selectedObject);
        // Don't draw currentStroke during drawing to prevent selection issues
        // The stroke will be drawn when it's added to the model after drawing is complete
    }

    @Override
    public Dimension getPreferredSize() {
        BufferedImage image = model.getImage();
        return view.getPreferredSize(image);
    }

    public JMenuBar createMenuBar() {
        return view.createMenuBar(this);
    }

    public void importImage() {
        view.showImportDialog(this);
    }

    public JPanel createToolBar() {
        return view.createToolBar(this);
    }

    public void deletePhoto() {
        model.clearAll();
        currentStroke = null;
        isDrawing = false;
        mousePressed = false;
        mouseMoved = false;
        refreshView();
    }

    public JPanel createStatusBar(){
        return view.createStatusBar();
    }
}
