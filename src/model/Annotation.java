package model;

import java.awt.*;
import java.awt.image.BufferedImage;
import interfaces.ITextRenderer;
import interfaces.IWordWrapper;
import utils.TextUtils;
import utils.WordWrapUtils;
import utils.Constants;

public class Annotation {
    private String text;
    private int x, y;
    private boolean isEditing;
    private Color color;
    private Rectangle cachedBounds = null; // Cache bounds when text changes
    
    public Annotation(String text, int x, int y) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.isEditing = false;
        this.color = Color.BLACK; // Default color
    }

    public void draw(Graphics2D g2, int photoWidth) {
        if (text == null || text.isEmpty()) return;
        
        setupFont(g2);
        FontMetrics fm = g2.getFontMetrics();
        int maxWidth = calculateMaxWidth(photoWidth);
        if (maxWidth <= 0) return;
        
        // Set the color for drawing
        g2.setColor(color);
        drawWrappedText(g2, fm, maxWidth);
    }
    
    private final ITextRenderer textRenderer = new TextUtils();
    private final IWordWrapper wordWrapper = new WordWrapUtils();
    
    private void setupFont(Graphics2D g2) {
        g2.setFont(TextUtils.createBoldFont());
    }
    
    private int calculateMaxWidth(int photoWidth) {
        return textRenderer.calculateMaxWidth(photoWidth, x);
    }
    
    private void drawWrappedText(Graphics2D g2, FontMetrics fm, int maxWidth) {
        wordWrapper.drawWrappedText(g2, fm, text, x, y, maxWidth);
    }

    // Hit testing and movement methods
    public boolean containsPoint(int px, int py, int photoWidth) {
        // Use accurate text bounds calculation
        if (text == null || text.isEmpty()) {
            return false;
        }
        
        // Create a temporary Graphics2D to get FontMetrics
        BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D tempG2 = tempImage.createGraphics();
        setupFont(tempG2);
        FontMetrics fm = tempG2.getFontMetrics();
        
        // Calculate accurate bounds using the SAME photoWidth as rendering
        int maxWidth = calculateMaxWidth(photoWidth);
        System.out.println("🔍 Annotation.containsPoint: Using photoWidth=" + photoWidth + ", maxWidth=" + maxWidth + " for bounds calculation");
        Rectangle bounds = textRenderer.calculateTextBounds(fm, text, x, y, maxWidth);
        
        tempG2.dispose();
        tempImage.flush();
        
        System.out.println("🔍 Annotation.containsPoint: bounds.height = " + bounds.height + ", contains(" + px + "," + py + ") = " + bounds.contains(px, py));
        return bounds.contains(px, py);
    }

    public void moveBy(int dx, int dy) {
        x += dx;
        y += dy;
        invalidateBounds(); // Recalculate bounds when position changes
    }

    public Point getPosition() {
        return new Point(x, y);
    }

    public void setPosition(Point position) {
        this.x = position.x;
        this.y = position.y;
        invalidateBounds(); // Recalculate bounds when position changes
    }

    // Text editing methods
    public boolean isEditing() {
        return isEditing;
    }

    public void setEditing(boolean editing) {
        this.isEditing = editing;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void addCharacter(char c) {
        if (text == null) {
            text = "";
        }
        text += c;
        invalidateBounds(); // Recalculate bounds when text changes
    }

    public void backspace() {
        if (text != null && text.length() > 0) {
            text = text.substring(0, text.length() - 1);
            invalidateBounds(); // Recalculate bounds when text changes
        }
    }

    public boolean isEmpty() {
        return text == null || text.isEmpty();
    }
    
    private void invalidateBounds() {
        cachedBounds = null; // Clear cached bounds when text changes
    }
    
    public Rectangle getBounds(int photoWidth) {
        if (cachedBounds == null) {
            // Calculate bounds using the SAME photoWidth as rendering
            BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics2D tempG2 = tempImage.createGraphics();
            setupFont(tempG2);
            FontMetrics fm = tempG2.getFontMetrics();
            
            int maxWidth = calculateMaxWidth(photoWidth);
            System.out.println("🔍 Annotation.getBounds: Calculating bounds with photoWidth=" + photoWidth + ", maxWidth=" + maxWidth);
            cachedBounds = textRenderer.calculateTextBounds(fm, text, x, y, maxWidth);
            
            tempG2.dispose();
            tempImage.flush();
        }
        return cachedBounds;
    }
    
    public Color getColor() {
        return color;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
}
