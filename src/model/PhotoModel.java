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
    private final List<Annotation> annotations;  
    private final List<Stroke> strokes;  
    private final List<TextBlock> textBlocks;  
    private TextBlock currentTextBlock;

    public PhotoModel() {
        this.image = null;
        this.flipped = false;  
        this.annotations = new ArrayList<>();
        this.strokes = new ArrayList<>();
        this.textBlocks = new ArrayList<>();
        this.currentTextBlock = null;
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

    public void addAnnotation(Annotation ann) {
        annotations.add(ann);
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
            currentTextBlock = null;
        }
    }
    
    public Point getTextInsertionPoint() {
        return currentTextBlock != null ? currentTextBlock.getInsertionPoint() : null;
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
        if (currentTextBlock != null && !currentTextBlock.isEmpty()) {
            currentTextBlock.setCommitted(true);
            currentTextBlock = null;
        }
    }

    public void clearAll() {
        image = null;  
        flipped = false;  
        annotations.clear();
        strokes.clear();
        textBlocks.clear();
        currentTextBlock = null;
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
