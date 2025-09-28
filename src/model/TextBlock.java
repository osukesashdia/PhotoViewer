package model;

import java.awt.*;
import interfaces.ITextRenderer;
import interfaces.IWordWrapper;
import utils.TextUtils;
import utils.WordWrapUtils;
import utils.Constants;

public class TextBlock {
    private Point insertionPoint;
    private StringBuilder text;
    private boolean committed;  
    
    public TextBlock(Point insertionPoint) {
        this.insertionPoint = new Point(insertionPoint);
        this.text = new StringBuilder();
        this.committed = false;
    }
    
    public void addCharacter(char c) {
        text.append(c);
    }
    
    public void backspace() {
        if (text.length() > 0) {
            text.setLength(text.length() - 1);
        }
    }
    
    public Point getInsertionPoint() {
        return new Point(insertionPoint);
    }
    
    public String getText() {
        return text.toString();
    }
    
    public boolean isEmpty() {
        return text.length() == 0;
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
        if (maxWidth <= 0) return;
        
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
}
