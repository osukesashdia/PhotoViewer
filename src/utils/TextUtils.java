package utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import utils.Constants;
import utils.WordWrapUtils;

public class TextUtils {
    
    public static Font createFont(int style) {
        return new Font(Constants.FONT_NAME, style, Constants.FONT_SIZE);
    }
    
    public static Font createBoldFont() {
        return new Font(Constants.FONT_NAME, Font.BOLD, Constants.FONT_SIZE_BOLD);
    }
    
    public void setupFont(Graphics2D g2) {
        g2.setFont(createFont(Font.PLAIN));
    }
    
    public int calculateLineHeight(FontMetrics fm) {
        return fm.getAscent() + fm.getDescent() + fm.getLeading();
    }
    
    public int calculateMaxWidth(int photoWidth, int x) {
        return photoWidth - x - Constants.TEXT_MARGIN;
    }
    

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

