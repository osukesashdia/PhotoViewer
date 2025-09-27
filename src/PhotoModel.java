import java.awt.Dimension;
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

    public PhotoModel() {
        this.image = null;
        this.flipped = false;  // false = photo front, true = photo back
        this.annotations = new ArrayList<>();
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

    /**
     * Business logic: Clear all annotations
     */
    public void clearAnnotations() {
        annotations.clear();
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

