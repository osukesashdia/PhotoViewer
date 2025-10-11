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
    private Annotation currentTextAnnotation;
    private Object selectedObject;
    private boolean isDragging;
    private Point dragOffset;

    public PhotoModel() {
        this.image = null;
        this.flipped = false;  
        this.annotationsVisible = true;
        this.annotations = new ArrayList<>();
        this.strokes = new ArrayList<>();
        this.currentTextAnnotation = null;
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
            currentTextAnnotation = new Annotation(point);
            annotations.add(currentTextAnnotation);
        } else {
            if (currentTextAnnotation != null && currentTextAnnotation.isEmpty()) {
                annotations.remove(currentTextAnnotation);
            }
            currentTextAnnotation = null;
        }
    }

    public void setCurrentTextAnnotation(Annotation annotation) {
        this.currentTextAnnotation = annotation;
    }


    public Annotation getCurrentTextAnnotation() {
        return currentTextAnnotation;
    }

    public void addToCurrentText(char c) {
        if (currentTextAnnotation != null) {
            currentTextAnnotation.addCharacter(c);
        }
    }

    public void backspaceCurrentText() {
        if (currentTextAnnotation != null) {
            currentTextAnnotation.backspace();
        }
    }

    public void commitCurrentText() {
        if (currentTextAnnotation != null) {
            if (!currentTextAnnotation.isEmpty()) {
                currentTextAnnotation.setCommitted(true);
            } else {
                annotations.remove(currentTextAnnotation);
            }
            currentTextAnnotation = null;
        }
    }

    public void clearAll() {
        image = null;  
        flipped = false;  
        annotationsVisible = true;
        annotations.clear();
        strokes.clear();
        currentTextAnnotation = null;
        clearSelection();
    }

    public void clearAnnotations() {
        annotations.clear();
        strokes.clear();
        currentTextAnnotation = null;
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
