import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;


class PhotoView {
    private PhotoModel model;

    public PhotoView(PhotoModel model) {
        this.model = model;
    }

    public void draw(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();

        BufferedImage img = model.getImage();
        if (img != null) {
            int w = img.getWidth();
            int h = img.getHeight();

            // Handle flipping
            if (model.isFlipped()) {
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-w, 0);
                g2.drawImage(img, tx, null);
            } else {
                g2.drawImage(img, 0, 0, c);
            }
        }

        // Draw annotations
        g2.setColor(Color.RED);
        for (Annotation ann : model.getAnnotations()) {
            g2.drawString(ann.text, ann.x, ann.y);
        }

        g2.dispose();
    }
}
