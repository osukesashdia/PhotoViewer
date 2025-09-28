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
        if (model.isFlipped() && e.getButton() == MouseEvent.BUTTON1) {
            mousePressed = true;
            mouseMoved = false;
            requestFocusInWindow();
        }
    }
    
    private void handleMouseReleased(MouseEvent e) {
        if (mousePressed && model.isFlipped() && e.getButton() == MouseEvent.BUTTON1) {
            if (mouseMoved && isDrawing) {
                finishDrawing();
            } else if (!mouseMoved) {
                setTextInsertionPoint(e.getX(), e.getY());
            }
            mousePressed = false;
            mouseMoved = false;
        }
    }
    
    private void handleMouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            toggleFlip();
        }
    }
    
    private void handleMouseDragged(MouseEvent e) {
        if (mousePressed && model.isFlipped()) {
            mouseMoved = true;
            if (!isDrawing) {
                startDrawing(e.getX(), e.getY());
            } else {
                continueDrawing(e.getX(), e.getY());
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
        if (model.isFlipped() && model.getCurrentTextBlock() != null) {
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        boolean isFlipped = model.isFlipped();
        BufferedImage image = model.getImage();
        List<Stroke> strokes = model.getStrokes();
        List<TextBlock> textBlocks = model.getTextBlocks();
        List<Annotation> annotations = model.getAnnotations();
        TextBlock currentTextBlock = model.getCurrentTextBlock();
        view.draw(g, this, isFlipped, image, strokes, textBlocks, annotations, currentTextBlock);
        if (isDrawing && currentStroke != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            currentStroke.draw(g2);
            g2.dispose();
        }
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
        return view.createToolBar();
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
