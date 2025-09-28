package utils;

import java.awt.*;
import interfaces.ITextRenderer;
import utils.Constants;

public class TextUtils implements ITextRenderer {
    
    public static Font createFont(int style) {
        return new Font(Constants.FONT_NAME, style, Constants.FONT_SIZE);
    }
    
    public static Font createBoldFont() {
        return new Font(Constants.FONT_NAME, Font.BOLD, Constants.FONT_SIZE_BOLD);
    }
    
    @Override
    public void setupFont(Graphics2D g2) {
        g2.setFont(createFont(Font.PLAIN));
    }
    
    @Override
    public int calculateLineHeight(FontMetrics fm) {
        return fm.getAscent() + fm.getDescent() + fm.getLeading();
    }
    
    @Override
    public int calculateMaxWidth(int photoWidth, int x) {
        return photoWidth - x - Constants.TEXT_MARGIN;
    }
    
    @Override
    public void drawCursor(Graphics2D g2, FontMetrics fm, int x, int y) {
        g2.setStroke(new java.awt.BasicStroke(Constants.CURSOR_STROKE_WIDTH));
        g2.drawLine(x, y - fm.getAscent(), x, y);
    }
    
    @Override
    public void drawCursorAtEnd(Graphics2D g2, FontMetrics fm, int x, int y, String text) {
        int textWidth = fm.stringWidth(text);
        int cursorX = x + textWidth;
        int cursorY = y;
        g2.setStroke(new java.awt.BasicStroke(Constants.CURSOR_STROKE_WIDTH));
        g2.drawLine(cursorX, cursorY - fm.getAscent(), cursorX, cursorY);
    }
}
