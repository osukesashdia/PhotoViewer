package model;

import java.awt.*;
import java.awt.image.BufferedImage;
import interfaces.ITextRenderer;
import interfaces.IWordWrapper;
import utils.TextUtils;
import utils.WordWrapUtils;
import utils.Constants;

public class TextBlock {
    private Point insertionPoint;
    private StringBuilder text;
    private boolean committed;
    private Color color;
    private Rectangle cachedBounds = null; // Cache bounds when text changes
    
    public TextBlock(Point insertionPoint) {
        this.insertionPoint = new Point(insertionPoint);
        this.text = new StringBuilder();
        this.committed = false;
        this.color = Color.BLACK; // Default color
    }
    
    public void addCharacter(char c) {
        text.append(c);
        invalidateBounds(); // Recalculate bounds when text changes
    }
    
    public void backspace() {
        if (text.length() > 0) {
            text.setLength(text.length() - 1);
            invalidateBounds(); // Recalculate bounds when text changes
        }
    }

    
    public boolean isEmpty() {
        return text.length() == 0;
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
            System.out.println("🔍 TextBlock.getBounds: Calculating bounds with photoWidth=" + photoWidth + ", maxWidth=" + maxWidth);
            cachedBounds = textRenderer.calculateTextBounds(fm, text.toString(), insertionPoint.x, insertionPoint.y, maxWidth);
            
            tempG2.dispose();
            tempImage.flush();
        }
        return cachedBounds;
    }
    
    public String getText() {
        return text.toString();
    }
    
    public Color getColor() {
        return color;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    public void setCommitted(boolean committed) {
        this.committed = committed;
    }
    
    public boolean isCommitted() {
        return committed;
    }
    public void draw(Graphics2D g2, int photoWidth, boolean isActive) {
        setupFont(g2);
        FontMetrics fm = g2.getFontMetrics();
        int x = insertionPoint.x;
        int y = insertionPoint.y;
        
        if (text.length() == 0) {
            drawEmptyCursor(g2, fm, x, y, isActive);
            return;
        }
        
        int maxWidth = calculateMaxWidth(photoWidth);
        System.out.println("🎨 TextBlock.draw: Using photoWidth=" + photoWidth + ", position=" + insertionPoint + ", maxWidth=" + maxWidth + " for rendering");
        if (maxWidth <= 0) return;
        
        // Set the color for drawing
        g2.setColor(color);
        int finalY = drawWrappedText(g2, fm, x, y, maxWidth);
        drawActiveCursor(g2, fm, x, finalY, isActive, maxWidth);
    }
    
    private final ITextRenderer textRenderer = new TextUtils();
    private final IWordWrapper wordWrapper = new WordWrapUtils();
    
    private void setupFont(Graphics2D g2) {
        textRenderer.setupFont(g2);
    }
    
    private void drawEmptyCursor(Graphics2D g2, FontMetrics fm, int x, int y, boolean isActive) {
        if (isActive) {
            textRenderer.drawCursor(g2, fm, x, y);
        }
    }
    
    private int calculateMaxWidth(int photoWidth) {
        return textRenderer.calculateMaxWidth(photoWidth, insertionPoint.x);
    }
    
    private int drawWrappedText(Graphics2D g2, FontMetrics fm, int x, int y, int maxWidth) {
        return wordWrapper.drawWrappedText(g2, fm, text.toString(), x, y, maxWidth);
    }
    
    private void drawActiveCursor(Graphics2D g2, FontMetrics fm, int x, int y, boolean isActive, int maxWidth) {
        if (isActive) {
            textRenderer.drawCursorAtEnd(g2, fm, x, y, text.toString());
        }
    }

    // Hit testing and movement methods
    public boolean containsPoint(int x, int y, int photoWidth) {
        if (isEmpty()) return false;
        
        // Use cached bounds calculated with the same photoWidth as rendering
        Rectangle bounds = getBounds(photoWidth);
        System.out.println("🔍 TextBlock.containsPoint: Using cached bounds.height = " + bounds.height + ", contains(" + x + "," + y + ") = " + bounds.contains(x, y));
        return bounds.contains(x, y);
    }

    public void moveBy(int dx, int dy) {
        insertionPoint.x += dx;
        insertionPoint.y += dy;
        invalidateBounds(); // Recalculate bounds when position changes
    }

    public Point getPosition() {
        return new Point(insertionPoint);
    }

    public void setPosition(Point position) {
        this.insertionPoint = new Point(position);
        invalidateBounds(); // Recalculate bounds when position changes
    }
}
