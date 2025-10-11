package view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import java.io.File;
// Removed model imports - view should not know about model classes
import java.util.function.Consumer;
import utils.Constants;
import utils.DrawingUtils;
import utils.TextUtils;

public class PhotoView {
    private final DrawingUtils drawingRenderer = new DrawingUtils();
    private final AnnotationRenderer annotationRenderer = new AnnotationRenderer();
    private final StrokeRenderer strokeRenderer = new StrokeRenderer();
    
    // Event listeners
    private java.awt.event.ActionListener importActionListener;
    private java.awt.event.ActionListener deleteActionListener;
    private java.awt.event.ActionListener colorActionListener;
    private Consumer<String> statusUpdateListener;
    
    public PhotoView() {
    }

    // Event listener setters
    public void setImportActionListener(java.awt.event.ActionListener listener) {
        this.importActionListener = listener;
    }
    
    public void setDeleteActionListener(java.awt.event.ActionListener listener) {
        this.deleteActionListener = listener;
    }
    
    public void setColorActionListener(java.awt.event.ActionListener listener) {
        this.colorActionListener = listener;
    }
    
    public void setStatusUpdateListener(Consumer<String> listener) {
        this.statusUpdateListener = listener;
    }

    public void draw(Graphics g, JComponent c, boolean isFlipped, boolean annotationsVisible, BufferedImage image, 
                    List<?> strokes, List<?> annotations, Object currentTextAnnotation, Object selectedObject) {
        Graphics2D g2 = (Graphics2D) g.create();

        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            drawBackground(g2, c);
            
            if (isFlipped) {
                drawPhotoBack(g2, c, image);
                if (annotationsVisible) {
                    drawStrokes(g2, strokes, selectedObject);
                    drawAnnotations(g2, annotations, image, currentTextAnnotation, selectedObject);
                }
                } else {
                drawPhoto(g2, c, image, isFlipped);
                if (annotationsVisible) {
                    drawStrokes(g2, strokes, selectedObject);
                    drawAnnotations(g2, annotations, image, currentTextAnnotation, selectedObject);
                }
            }
        } finally {
            g2.dispose();
        }
    }

    private void drawBackground(Graphics2D g2, JComponent c) {
        drawingRenderer.drawCheckerboardBackground(g2, c);
    }

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

    private void drawPhotoBack(Graphics2D g2, JComponent c, BufferedImage image) {
        int surfaceWidth, surfaceHeight;
        if (image != null) {
            surfaceWidth = image.getWidth();
            surfaceHeight = image.getHeight();
        } else {
            surfaceWidth = c.getWidth();
            surfaceHeight = c.getHeight();
        }
        drawingRenderer.drawWhiteSurface(g2, surfaceWidth, surfaceHeight);
    }

    private void drawStrokes(Graphics2D g2, List<?> strokes, Object selectedObject) {
        for (Object stroke : strokes) {
            strokeRenderer.drawStroke(g2, stroke);
        }
    }


    private void drawAnnotations(Graphics2D g2, List<?> annotations, BufferedImage image, Object currentTextAnnotation, Object selectedObject) {
        int surfaceWidth, surfaceHeight;
        if (image != null) {
            surfaceWidth = image.getWidth();
            surfaceHeight = image.getHeight();
        } else {
            surfaceWidth = Constants.DEFAULT_WIDTH;
            surfaceHeight = Constants.DEFAULT_HEIGHT;
        }
        
        g2.setClip(0, 0, surfaceWidth, surfaceHeight);
        for (Object annotation : annotations) {
            // Only draw non-empty annotations
            if (!isEmpty(annotation)) {
                // Use AnnotationRenderer for all annotations
                boolean isActive = (annotation == currentTextAnnotation);
                annotationRenderer.drawAnnotation(g2, annotation, surfaceWidth);
                
                // Draw editing cursor if this annotation is being edited
                if (annotation == selectedObject && isEditing(annotation)) {
                    drawEditingCursor(g2, annotation);
                }
            }
        }
    }


    private Rectangle getStrokeBounds(Object stroke) {
        // This is a simplified bounding box calculation
        // In a real implementation, you'd want to calculate the actual bounds
        try {
            Point center = (Point) stroke.getClass().getMethod("getCenter").invoke(stroke);
            return new Rectangle(center.x - 50, center.y - 10, 100, 20);
        } catch (Exception e) {
            // Fallback to a default bounds if reflection fails
            return new Rectangle(0, 0, 100, 20);
        }
    }


    private void drawEditingCursor(Graphics2D g2, Object annotation) {
        g2.setColor(Color.BLUE);
        g2.setStroke(new java.awt.BasicStroke(1.0f));
        Point pos = getPosition(annotation);
        // Draw a simple cursor at the end of the text
        int cursorX = pos.x + 100; // Approximate end of text
        g2.drawLine(cursorX, pos.y - 15, cursorX, pos.y + 5);
    }
    
    // Helper methods to access object data without knowing the model class
    private boolean isEmpty(Object obj) {
        try {
            return (Boolean) obj.getClass().getMethod("isEmpty").invoke(obj);
        } catch (Exception e) {
            return true;
        }
    }
    
    private boolean isEditing(Object obj) {
        try {
            return (Boolean) obj.getClass().getMethod("isEditing").invoke(obj);
        } catch (Exception e) {
            return false;
        }
    }
    
    private Point getPosition(Object obj) {
        try {
            return (Point) obj.getClass().getMethod("getPosition").invoke(obj);
        } catch (Exception e) {
            return new Point(0, 0);
        }
    }

    public Dimension getPreferredSize(BufferedImage image) {
        if (image != null) {
            return new Dimension(image.getWidth(), image.getHeight());
        }
        return new Dimension(Constants.DEFAULT_WIDTH, Constants.DEFAULT_HEIGHT);
    }

    public JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createViewMenu());
        return menuBar;
    }
    
    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        
        fileMenu.add(createImportMenuItem());
        fileMenu.add(createDeleteMenuItem());
        fileMenu.add(createQuitMenuItem());
        
        return fileMenu;
    }
    
    private JMenuItem createImportMenuItem() {
        JMenuItem importItem = new JMenuItem("Import");
        importItem.addActionListener(e -> {
            if (importActionListener != null) {
                importActionListener.actionPerformed(e);
            }
        });
        return importItem;
    }
    
    private JMenuItem createDeleteMenuItem() {
        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(e -> {
            if (deleteActionListener != null) {
                deleteActionListener.actionPerformed(e);
            }
        });
        return deleteItem;
    }
    
    private JMenuItem createQuitMenuItem() {
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.addActionListener(e -> System.exit(0));
        return quitItem;
    }
    
    private JMenu createViewMenu() {
        JMenu viewMenu = new JMenu("View");
        
        ButtonGroup viewGroup = new ButtonGroup();
        JRadioButtonMenuItem photoItem = createPhotoMenuItem();
        JRadioButtonMenuItem browseItem = createBrowseMenuItem();
        
        viewGroup.add(photoItem);
        viewGroup.add(browseItem);
        viewMenu.add(photoItem);
        viewMenu.add(browseItem);
        
        return viewMenu;
    }
    
    private JRadioButtonMenuItem createPhotoMenuItem() {
        JRadioButtonMenuItem photoItem = new JRadioButtonMenuItem("Photo");
        photoItem.setSelected(true);
        photoItem.addActionListener(e -> {
        });
        return photoItem;
    }
    
    private JRadioButtonMenuItem createBrowseMenuItem() {
        JRadioButtonMenuItem browseItem = new JRadioButtonMenuItem("Browse");
        browseItem.addActionListener(e -> {
        });
        return browseItem;
    }

    public JPanel createToolBar() {
        JPanel toolBarPanel = new JPanel();
        
        // Color chooser button
        JButton colorButton = new JButton("Color");
        colorButton.addActionListener(e -> {
            if (colorActionListener != null) {
                colorActionListener.actionPerformed(e);
            }
        });
        toolBarPanel.add(colorButton);
        
        // Category buttons
        String[] categories = {"People", "Foods"};
        for (String category : categories) {
            JToggleButton categoryToggleButton = new JToggleButton(category);
            categoryToggleButton.addActionListener(e -> {
                if (statusUpdateListener != null) {
                    statusUpdateListener.accept(category + " button clicked");
                }
            });
            toolBarPanel.add(categoryToggleButton);
        }
        toolBarPanel.setBackground(Color.LIGHT_GRAY);
        return toolBarPanel;
    }

    public JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
        statusBar.setBackground(Color.LIGHT_GRAY);
        JLabel statusLabel = new JLabel("Ready");
        statusLabel.setName("statusLabel"); 
        statusBar.add(statusLabel, BorderLayout.WEST);
        return statusBar;
    }


    
}
