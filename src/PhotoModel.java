import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
class PhotoModel {
    private BufferedImage image;
    private boolean flipped;
    private List<Annotation> annotations;

    public PhotoModel(File file) {
        try {
            this.image = ImageIO.read(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.flipped = false;
        this.annotations = new ArrayList<>();
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
        return annotations;
    }
}

