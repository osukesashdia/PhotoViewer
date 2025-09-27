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
    private final List<Annotation> annotations;  // Annotations only for back side
    private final List<Stroke> strokes;  // Freehand strokes for back side
    private final List<TextBlock> textBlocks;  // Text blocks for word wrap
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
     * Business logic: Clear all annotations, strokes, and text blocks
     */
    public void clearAll() {
        image = null;  // Clear the image
        flipped = false;  // Reset flip state
        annotations.clear();
        strokes.clear();
        textBlocks.clear();
        currentTextBlock = null;
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

}

