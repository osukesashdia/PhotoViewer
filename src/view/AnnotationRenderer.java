package view;

import java.awt.*;
import java.awt.image.BufferedImage;
import utils.TextUtils;
import utils.WordWrapUtils;

public class AnnotationRenderer {
    private final TextUtils textRenderer = new TextUtils();
    private final WordWrapUtils wordWrapper = new WordWrapUtils();
    
    public void drawAnnotation(Graphics2D g2, Object annotation, int photoWidth) {
        if (isEmpty(annotation)) return;
        
        setupFont(g2);
        FontMetrics fm = g2.getFontMetrics();
        Point pos = getPosition(annotation);
        int maxWidth = calculateMaxWidth(photoWidth, pos.x);
        
        if (maxWidth <= 0) return;
        
        // Set the color for drawing
        g2.setColor(getColor(annotation));
        wordWrapper.drawWrappedText(g2, fm, getText(annotation), pos.x, pos.y, maxWidth);
    }
    
    private void setupFont(Graphics2D g2) {
        g2.setFont(TextUtils.createBoldFont());
    }
    
    private int calculateMaxWidth(int photoWidth, int x) {
        return textRenderer.calculateMaxWidth(photoWidth, x);
    }
    
    public Rectangle calculateBounds(Object annotation, int photoWidth) {
        if (isEmpty(annotation)) {
            Point pos = getPosition(annotation);
            return new Rectangle(pos.x, pos.y, 0, 0);
        }
        
        BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D tempG2 = tempImage.createGraphics();
        setupFont(tempG2);
        FontMetrics fm = tempG2.getFontMetrics();
        
        Point pos = getPosition(annotation);
        int maxWidth = calculateMaxWidth(photoWidth, pos.x);
        Rectangle bounds = textRenderer.calculateTextBounds(fm, getText(annotation), pos.x, pos.y, maxWidth);
        
        tempG2.dispose();
        tempImage.flush();
        
        return bounds;
    }
    
    // Helper methods to access annotation data without knowing the model class
    private boolean isEmpty(Object annotation) {
        try {
            return (Boolean) annotation.getClass().getMethod("isEmpty").invoke(annotation);
        } catch (Exception e) {
            return true;
        }
    }
    
    private String getText(Object annotation) {
        try {
            return (String) annotation.getClass().getMethod("getText").invoke(annotation);
        } catch (Exception e) {
            return "";
        }
    }
    
    private Point getPosition(Object annotation) {
        try {
            return (Point) annotation.getClass().getMethod("getPosition").invoke(annotation);
        } catch (Exception e) {
            return new Point(0, 0);
        }
    }
    
    private Color getColor(Object annotation) {
        try {
            return (Color) annotation.getClass().getMethod("getColor").invoke(annotation);
        } catch (Exception e) {
            return Color.BLACK;
        }
    }
}
