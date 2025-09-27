import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Presentation layer in PAC pattern
 * Handles all rendering and visual presentation logic
 */
class PhotoView {
    private PhotoModel model;

    public PhotoView(PhotoModel model) {
        this.model = model;
    }

    /**
     * Main rendering method - delegates to specific rendering methods
     */
    public void draw(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();
        
        try {
            // Enable anti-aliasing for better quality
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            // Render background
            drawBackground(g2, c);
            
            // Two rendering paths based on flip state
            if (model.isFlipped()) {
                // Photo back: white surface + annotations
                drawPhotoBack(g2, c);
                drawAnnotations(g2, c);
            } else {
                // Photo front: image only (no annotations on front)
                drawPhoto(g2, c);
            }
            
        } finally {
            g2.dispose();
        }
    }

    /**
     * Presentation method: Draw checkerboard background
     */
    private void drawBackground(Graphics2D g2, JComponent c) {
        int size = 20;
        for (int y = 0; y < c.getHeight(); y += size) {
            for (int x = 0; x < c.getWidth(); x += size) {
                if (((x / size) + (y / size)) % 2 == 0) {
                    g2.setColor(new Color(220, 220, 220));
                } else {
                    g2.setColor(new Color(200, 200, 200));
                }
                g2.fillRect(x, y, size, size);
            }
        }
    }

    /**
     * Presentation method: Draw the photo with flip transformation
     */
    private void drawPhoto(Graphics2D g2, JComponent c) {
        BufferedImage img = model.getImage();
        if (img != null) {
            int w = img.getWidth();
            int h = img.getHeight();

            if (model.isFlipped()) {
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-w, 0);
                g2.drawImage(img, tx, null);
            } else {
                g2.drawImage(img, 0, 0, c);
            }
        }
    }

    /**
     * Presentation method: Draw the photo back (white surface)
     */
    private void drawPhotoBack(Graphics2D g2, JComponent c) {
        if (model.hasImage()) {
            // Draw white surface the same size as the photo
            Dimension photoSize = model.getImageDimensions();
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, photoSize.width, photoSize.height);
            
            // Draw a subtle border to indicate it's the back
            g2.setColor(Color.LIGHT_GRAY);
            g2.setStroke(new java.awt.BasicStroke(2));
            g2.drawRect(0, 0, photoSize.width - 1, photoSize.height - 1);
            
            // Draw "PHOTO BACK" text in the center
            g2.setColor(Color.GRAY);
            g2.setFont(new Font("Arial", Font.ITALIC, 16));
            String backText = "PHOTO BACK";
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(backText);
            int textHeight = fm.getHeight();
            int x = (photoSize.width - textWidth) / 2;
            int y = (photoSize.height - textHeight) / 2 + fm.getAscent();
            g2.drawString(backText, x, y);
        }
    }

    /**
     * Presentation method: Draw annotations with enhanced styling
     */
    private void drawAnnotations(Graphics2D g2, JComponent c) {
        g2.setColor(Color.RED);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        
        for (Annotation ann : model.getAnnotations()) {
            // Draw text with shadow for better visibility
            g2.setColor(Color.BLACK);
            g2.drawString(ann.text, ann.x + 1, ann.y + 1);
            g2.setColor(Color.RED);
            g2.drawString(ann.text, ann.x, ann.y);
        }
    }

    /**
     * Presentation method: Get preferred size based on model data
     */
    public Dimension getPreferredSize() {
        if (model.hasImage()) {
            return model.getImageDimensions();
        }
        return new Dimension(400, 300);
    }
}
