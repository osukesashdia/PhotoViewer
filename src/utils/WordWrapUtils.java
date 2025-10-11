package utils;

import java.awt.*;
import utils.TextUtils;

public class WordWrapUtils {
    
    public int drawWrappedText(Graphics2D g2, FontMetrics fm, String text, int x, int y, int maxWidth) {
        String[] words = text.split(" ");
        TextUtils textUtils = new TextUtils();
        int lineHeight = textUtils.calculateLineHeight(fm);
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            int lineWidth = fm.stringWidth(testLine);
            
            if (lineWidth <= maxWidth) {
                currentLine = new StringBuilder(testLine);
            } else {
                if (currentLine.length() > 0) {
                    y = drawCurrentLineAndAdvance(g2, fm, x, y, currentLine, lineHeight, maxWidth);
                }
                
                if (fm.stringWidth(word) > maxWidth) {
                    y = drawLongWord(g2, fm, x, y, lineHeight, maxWidth, word);
                    currentLine = new StringBuilder();
                } else {
                    currentLine = new StringBuilder(word);
                }
            }
        }
        
        if (currentLine.length() > 0) {
            g2.drawString(currentLine.toString(), x, y);
        }
        return y;
    }
    
    public int drawCurrentLineAndAdvance(Graphics2D g2, FontMetrics fm, int x, int y, 
                                       StringBuilder currentLine, int lineHeight, int maxWidth) {
        if (currentLine.length() > 0) {
            g2.drawString(currentLine.toString(), x, y);
            return y + lineHeight;
        } else {
            return y;
        }
    }
    
    public int drawLongWord(Graphics2D g2, FontMetrics fm, int x, int y, int lineHeight, int maxWidth, String word) {
        String remainingWord = word;
        
        while (remainingWord.length() > 0) {
            String testWord = remainingWord;
            while (fm.stringWidth(testWord) > maxWidth && testWord.length() > 1) {
                testWord = testWord.substring(0, testWord.length() - 1);
            }
            g2.drawString(testWord, x, y);
            y += lineHeight;
            remainingWord = remainingWord.substring(testWord.length());
        }
        return y;
    }
}
