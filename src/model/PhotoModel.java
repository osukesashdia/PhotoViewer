package model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import interfaces.IPhotoModel;

public class PhotoModel implements IPhotoModel {
    private BufferedImage image;
    private boolean flipped;  
    private boolean annotationsVisible;
    private final List<Annotation> annotations;  
    private final List<Stroke> strokes;  
    private final List<TextBlock> textBlocks;  
    private TextBlock currentTextBlock;
    
    // Selection state
    private Object selectedObject;
    private boolean isDragging;
    private Point dragOffset;

    public PhotoModel() {
        this.image = null;
        this.flipped = false;  
        this.annotationsVisible = true;
        this.annotations = new ArrayList<>();
        this.strokes = new ArrayList<>();
        this.textBlocks = new ArrayList<>();
        this.currentTextBlock = null;
        this.selectedObject = null;
        this.isDragging = false;
        this.dragOffset = null;
    }

    public void loadImage(File file) {
        clearAnnotations();
        if (file == null) {
            this.image = null;
            return;
        }
        try {
            this.image = ImageIO.read(file);
        } catch (Exception e) {
            e.printStackTrace();
            this.image = null;
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    public boolean isFlipped() {
        return flipped;
    }

    public void toggleFlipped() {
        this.flipped = !this.flipped;
    }

    public boolean isAnnotationsVisible() {
        return annotationsVisible;
    }

    public void toggleAnnotationsVisible() {
        this.annotationsVisible = !this.annotationsVisible;
    }

    // Selection management methods
    public Object getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Object object) {
        this.selectedObject = object;
    }

    public boolean isDragging() {
        return isDragging;
    }

    public void setDragging(boolean dragging) {
        this.isDragging = dragging;
    }

    public Point getDragOffset() {
        return dragOffset;
    }

    public void setDragOffset(Point offset) {
        this.dragOffset = offset;
    }

    public void clearSelection() {
        this.selectedObject = null;
        this.isDragging = false;
        this.dragOffset = null;
    }

    public List<Annotation> getAnnotations() {
        return new ArrayList<>(annotations);
    }

    public void addStroke(Stroke stroke) {
        strokes.add(stroke);
    }

    public List<Stroke> getStrokes() {
        return new ArrayList<>(strokes);
    }

    public void setTextInsertionPoint(Point point) {
        if (point != null) {
            currentTextBlock = new TextBlock(point);
            textBlocks.add(currentTextBlock);
        } else {
            // Clean up empty TextBlock if it exists
            if (currentTextBlock != null && currentTextBlock.isEmpty()) {
                textBlocks.remove(currentTextBlock);
            }
            currentTextBlock = null;
        }
    }

    public void setCurrentTextBlock(TextBlock textBlock) {
        this.currentTextBlock = textBlock;
    }


    public TextBlock getCurrentTextBlock() {
        return currentTextBlock;
    }

    public List<TextBlock> getTextBlocks() {
        return new ArrayList<>(textBlocks);
    }

    public void addToCurrentText(char c) {
        if (currentTextBlock != null) {
            currentTextBlock.addCharacter(c);
        }
    }

    public void backspaceCurrentText() {
        if (currentTextBlock != null) {
            currentTextBlock.backspace();
        }
    }

    public void commitCurrentText() {
        if (currentTextBlock != null) {
            if (!currentTextBlock.isEmpty()) {
                // Commit non-empty TextBlock
                currentTextBlock.setCommitted(true);
            } else {
                // Remove empty TextBlock from the list
                textBlocks.remove(currentTextBlock);
            }
            currentTextBlock = null;
        }
    }

    public void clearAll() {
        image = null;  
        flipped = false;  
        annotationsVisible = true;
        annotations.clear();
        strokes.clear();
        textBlocks.clear();
        currentTextBlock = null;
        clearSelection();
    }

    public void clearAnnotations() {
        annotations.clear();
        strokes.clear();
        textBlocks.clear();
        currentTextBlock = null;
    }

    public boolean hasImage() {
        return image != null;
    }

    public Dimension getImageDimensions() {
        if (image != null) {
            return new Dimension(image.getWidth(), image.getHeight());
        }
        return new Dimension(0, 0);
    }
}
