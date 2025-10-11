package controller;

import java.awt.*;
import java.awt.image.BufferedImage;
import model.Annotation;
import utils.TextUtils;

public class AnnotationHitTester {
    private final TextUtils textRenderer = new TextUtils();
    
    // For Annotation objects
    public boolean containsPoint(Annotation annotation, int x, int y, int photoWidth) {
        return containsPointGeneric(annotation, x, y, photoWidth);
    }
    
    
    // Generic method that works with both types
    private boolean containsPointGeneric(Object textObject, int x, int y, int photoWidth) {
        try {
            // Check if object is empty using reflection
            boolean isEmpty = (Boolean) textObject.getClass().getMethod("isEmpty").invoke(textObject);
            if (isEmpty) return false;
            
            // Get bounds using reflection
            Rectangle bounds = (Rectangle) textObject.getClass().getMethod("getBounds").invoke(textObject);
            if (bounds == null) {
                // Calculate bounds if not cached
                bounds = calculateBounds(textObject, photoWidth);
                textObject.getClass().getMethod("setBounds", Rectangle.class).invoke(textObject, bounds);
            }
            
            return bounds.contains(x, y);
        } catch (Exception e) {
            return false;
        }
    }
    
    private Rectangle calculateBounds(Object textObject, int photoWidth) {
        try {
            // Check if object is empty using reflection
            boolean isEmpty = (Boolean) textObject.getClass().getMethod("isEmpty").invoke(textObject);
            if (isEmpty) {
                Point pos = (Point) textObject.getClass().getMethod("getPosition").invoke(textObject);
                return new Rectangle(pos.x, pos.y, 0, 0);
            }
            
            BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics2D tempG2 = tempImage.createGraphics();
            textRenderer.setupFont(tempG2); // Use regular font for both
            FontMetrics fm = tempG2.getFontMetrics();
            
            Point pos = (Point) textObject.getClass().getMethod("getPosition").invoke(textObject);
            String text = (String) textObject.getClass().getMethod("getText").invoke(textObject);
            
            int maxWidth = textRenderer.calculateMaxWidth(photoWidth, pos.x);
            Rectangle bounds = textRenderer.calculateTextBounds(fm, text, pos.x, pos.y, maxWidth);
            
            tempG2.dispose();
            tempImage.flush();
            
            return bounds;
        } catch (Exception e) {
            return new Rectangle(0, 0, 0, 0);
        }
    }
}
