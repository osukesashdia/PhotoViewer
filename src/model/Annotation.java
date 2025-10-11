package model;

import java.awt.*;

public class Annotation {
    private StringBuilder text;
    private int x, y;
    private boolean isEditing;
    private boolean committed;
    private Color color;
    private Rectangle cachedBounds = null;
    
    // Constructor for existing annotations
    public Annotation(String text, int x, int y) {
        this.text = new StringBuilder(text != null ? text : "");
        this.x = x;
        this.y = y;
        this.isEditing = false;
        this.committed = true; // Existing annotations are committed
        this.color = Color.BLACK;
    }
    
    // Constructor for new text input (like TextBlock)
    public Annotation(Point insertionPoint) {
        this.text = new StringBuilder();
        this.x = insertionPoint.x;
        this.y = insertionPoint.y;
        this.isEditing = false;
        this.committed = false; // New annotations start uncommitted
        this.color = Color.BLACK; // Default color for new text
    }
    
    // Data access methods
    public String getText() {
        return text.toString();
    }
    
    public void setText(String text) {
        this.text = new StringBuilder(text != null ? text : "");
        invalidateBounds();
    }
    
    public Point getPosition() {
        return new Point(x, y);
    }
    
    public void setPosition(Point position) {
        this.x = position.x;
        this.y = position.y;
        invalidateBounds();
    }
    
    public boolean isEditing() {
        return isEditing;
    }
    
    public void setEditing(boolean editing) {
        this.isEditing = editing;
    }
    
    public Color getColor() {
        return color;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    public boolean isEmpty() {
        return text.length() == 0;
    }
    
    // Text manipulation methods
    public void addCharacter(char c) {
        text.append(c);
        invalidateBounds();
    }
    
    public void backspace() {
        if (text.length() > 0) {
            text.setLength(text.length() - 1);
            invalidateBounds();
        }
    }
    
    // TextBlock-specific methods
    public boolean isCommitted() {
        return committed;
    }
    
    public void setCommitted(boolean committed) {
        this.committed = committed;
    }
    
    // Movement methods
    public void moveBy(int dx, int dy) {
        this.x += dx;
        this.y += dy;
        invalidateBounds();
    }
    
    // Bounds caching for performance
    public Rectangle getBounds() {
        return cachedBounds;
    }
    
    public void setBounds(Rectangle bounds) {
        this.cachedBounds = bounds;
    }
    
    public void invalidateBounds() {
        cachedBounds = null;
    }
}
