package utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import interfaces.ITextRenderer;
import utils.Constants;
import utils.WordWrapUtils;

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

    // Text bounds calculation methods
    @Override
    public Rectangle calculateTextBounds(FontMetrics fm, String text, int x, int y, int maxWidth) {
        if (text == null || text.isEmpty()) {
            return new Rectangle(x, y - fm.getAscent(), 0, fm.getHeight());
        }
        
        BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D tempG2 = tempImage.createGraphics();
        tempG2.setFont(fm.getFont());
        
        WordWrapUtils wordWrapUtils = new WordWrapUtils();
        int startY = y;
        int finalY = wordWrapUtils.drawWrappedText(tempG2, fm, text, x, y, maxWidth);
        
        int lineHeight = calculateLineHeight(fm);
        int numberOfLines = Math.max(1, (finalY - startY) / lineHeight);
        int totalHeight = numberOfLines * lineHeight;
        
        tempG2.dispose();
        
        int maxWidthUsed = 0;
        String currentLine = "";
        String[] words = text.split(" ");
        
        for (String word : words) {
            String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;
            int lineWidth = fm.stringWidth(testLine);
            
            if (lineWidth <= maxWidth) {
                currentLine = testLine;
                maxWidthUsed = Math.max(maxWidthUsed, lineWidth);
            } else {
                if (!currentLine.isEmpty()) {
                    maxWidthUsed = Math.max(maxWidthUsed, fm.stringWidth(currentLine));
                    currentLine = "";
                }
                
                int wordWidth = fm.stringWidth(word);
                if (wordWidth > maxWidth) {
                    String remainingWord = word;
                    while (remainingWord.length() > 0) {
                        String testWord = remainingWord;
                        while (fm.stringWidth(testWord) > maxWidth && testWord.length() > 1) {
                            testWord = testWord.substring(0, testWord.length() - 1);
                        }
                        maxWidthUsed = Math.max(maxWidthUsed, fm.stringWidth(testWord));
                        remainingWord = remainingWord.substring(testWord.length());
                    }
                } else {
                    currentLine = word;
                    maxWidthUsed = Math.max(maxWidthUsed, fm.stringWidth(word));
                }
            }
        }
        
        if (!currentLine.isEmpty()) {
            maxWidthUsed = Math.max(maxWidthUsed, fm.stringWidth(currentLine));
        }
        
        return new Rectangle(x, y - fm.getAscent(), maxWidthUsed, totalHeight);
    }
}

