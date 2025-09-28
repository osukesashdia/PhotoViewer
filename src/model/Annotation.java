package model;

import java.awt.*;
import interfaces.ITextRenderer;
import interfaces.IWordWrapper;
import utils.TextUtils;
import utils.WordWrapUtils;
import utils.Constants;

public class Annotation {
    String text;
    int x, y;
    
    public Annotation(String text, int x, int y) {
        this.text = text;
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics2D g2, int photoWidth) {
        if (text == null || text.isEmpty()) return;
        
        setupFont(g2);
        FontMetrics fm = g2.getFontMetrics();
        int maxWidth = calculateMaxWidth(photoWidth);
        if (maxWidth <= 0) return;
        
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
}
