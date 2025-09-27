import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Abstraction layer in PAC pattern
 * Manages data and business logic for photo operations
 */
class PhotoModel {
    private BufferedImage image;
    private boolean flipped;  // true = photo back (white surface), false = photo front (image)
    private List<Annotation> annotations;  // Annotations only for back side
    private List<Stroke> strokes;  // Freehand strokes for back side
    private List<TextBlock> textBlocks;  // Text blocks for word wrap
    private TextBlock currentTextBlock;  // Current active text block

    public PhotoModel() {
        this.image = null;
        this.flipped = false;  // false = photo front, true = photo back
        this.annotations = new ArrayList<>();
        this.strokes = new ArrayList<>();
        this.textBlocks = new ArrayList<>();
        this.currentTextBlock = null;
    }

    public void loadImage(File file) {
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
            // Convert to annotation
            addAnnotation(new Annotation(currentTextBlock.getText(), 
                currentTextBlock.getInsertionPoint().x, 
                currentTextBlock.getInsertionPoint().y));
            // Remove the text block from the list
            textBlocks.remove(currentTextBlock);
            currentTextBlock = null;
        }
    }

    /**
     * Business logic: Clear all annotations
     */
    public void clearAnnotations() {
        annotations.clear();
    }

    /**
     * Business logic: Clear all strokes
     */
    public void clearStrokes() {
        strokes.clear();
    }

    /**
     * Business logic: Clear all text blocks
     */
    public void clearTextBlocks() {
        textBlocks.clear();
        currentTextBlock = null;
    }

    /**
     * Business logic: Clear all annotations, strokes, and text blocks
     */
    public void clearAll() {
        annotations.clear();
        strokes.clear();
        textBlocks.clear();
        currentTextBlock = null;
    }

    /**
     * Business logic: Remove annotation at specific coordinates
     */
    public boolean removeAnnotationAt(int x, int y) {
        return annotations.removeIf(ann -> Math.abs(ann.x - x) < 10 && Math.abs(ann.y - y) < 10);
    }

    /**
     * Business logic: Check if image is loaded
     */
    public boolean hasImage() {
        return image != null;
    }

    /**
     * Business logic: Get image dimensions
     */
    public Dimension getImageDimensions() {
        if (image != null) {
            return new Dimension(image.getWidth(), image.getHeight());
        }
        return new Dimension(0, 0);
    }

    /**
     * Business logic: Get annotation count
     */
    public int getAnnotationCount() {
        return annotations.size();
    }
}

