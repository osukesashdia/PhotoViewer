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
            Rectangle emptyRect = new Rectangle(x, y - fm.getAscent(), 0, fm.getHeight());
            return emptyRect;
        }
        
        System.out.println("\n📐 WORDWRAP CALCULATION WORKFLOW");
        System.out.println("═══════════════════════════════════");
        System.out.println("Step 1: Text Input");
        System.out.println("  Text: '" + text + "'");
        System.out.println("  Position: (x=" + x + ", y=" + y + ")");
        System.out.println("  MaxWidth: " + maxWidth);
        
        // Use the EXACT same word wrapping logic as rendering by simulating the process
        BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D tempG2 = tempImage.createGraphics();
        tempG2.setFont(fm.getFont());
        
        System.out.println("\nStep 2: Word Splitting");
        String[] words = text.split(" ");
        System.out.println("  Words: " + java.util.Arrays.toString(words));
        
        System.out.println("\nStep 3: Word Testing");
        StringBuilder currentLine = new StringBuilder();
        int maxWidthUsed = 0;
        
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            int lineWidth = fm.stringWidth(testLine);
            
            System.out.println("  Word[" + i + "]: '" + word + "'");
            System.out.println("  TestLine: '" + testLine + "'");
            System.out.println("  LineWidth: " + lineWidth + ", MaxWidth: " + maxWidth);
            
            if (lineWidth <= maxWidth) {
                currentLine = new StringBuilder(testLine);
                maxWidthUsed = Math.max(maxWidthUsed, lineWidth);
                System.out.println("  ✅ FITS -> Add to current line");
            } else {
                System.out.println("  ❌ WRAP OCCURS!");
                if (currentLine.length() > 0) {
                    maxWidthUsed = Math.max(maxWidthUsed, fm.stringWidth(currentLine.toString()));
                    System.out.println("  Finish line: '" + currentLine + "'");
                    currentLine = new StringBuilder();
                }
                
                int wordWidth = fm.stringWidth(word);
                System.out.println("  WordWidth: " + wordWidth + ", MaxWidth: " + maxWidth);
                
                if (wordWidth > maxWidth) {
                    System.out.println("  🔨 Long word needs breaking");
                    String remainingWord = word;
                    int fragmentCount = 0;
                    while (remainingWord.length() > 0) {
                        String testWord = remainingWord;
                        while (fm.stringWidth(testWord) > maxWidth && testWord.length() > 1) {
                            testWord = testWord.substring(0, testWord.length() - 1);
                        }
                        fragmentCount++;
                        maxWidthUsed = Math.max(maxWidthUsed, fm.stringWidth(testWord));
                        System.out.println("  Fragment " + fragmentCount + ": '" + testWord + "'");
                        remainingWord = remainingWord.substring(testWord.length());
                    }
                } else {
                    currentLine = new StringBuilder(word);
                    maxWidthUsed = Math.max(maxWidthUsed, fm.stringWidth(word));
                    System.out.println("  Start new line: '" + currentLine + "'");
                }
            }
        }
        
        if (currentLine.length() > 0) {
            maxWidthUsed = Math.max(maxWidthUsed, fm.stringWidth(currentLine.toString()));
            System.out.println("  Final line: '" + currentLine + "'");
        }
        
        System.out.println("\nStep 4: Use Exact Rendering Logic");
        System.out.println("  🔍 BOUNDS CALCULATION: maxWidth=" + maxWidth + " for text='" + text + "'");
        
        WordWrapUtils wordWrapUtils = new WordWrapUtils();
        int startY = y;
        int finalY = wordWrapUtils.drawWrappedText(tempG2, fm, text, x, y, maxWidth);
        
        System.out.println("  StartY: " + startY);
        System.out.println("  FinalY: " + finalY);
        
        System.out.println("\nStep 5: Calculate Lines and Height");
        int lineHeight = calculateLineHeight(fm);
        int numberOfLines = Math.max(1, (finalY - startY) / lineHeight);
        int totalHeight = numberOfLines * lineHeight;
        
        System.out.println("  LineHeight: " + lineHeight);
        System.out.println("  NumberOfLines: " + numberOfLines);
        System.out.println("  TotalHeight: " + totalHeight);
        System.out.println("  🎯 BOUNDS RESULT: " + numberOfLines + " lines, height=" + totalHeight);
        
        tempG2.dispose();
        
        Rectangle result = new Rectangle(x, y - fm.getAscent(), maxWidthUsed, totalHeight);
        
        System.out.println("\nStep 6: Final Result");
        System.out.println("  BoundingBox: " + result);
        System.out.println("  Height: " + result.height + " pixels");
        System.out.println("═══════════════════════════════════\n");
        
        return result;
    }
}
