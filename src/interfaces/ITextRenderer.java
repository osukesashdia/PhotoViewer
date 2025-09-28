package interfaces;

import java.awt.Graphics2D;
import java.awt.FontMetrics;

public interface ITextRenderer {
    void setupFont(Graphics2D g2);
    int calculateMaxWidth(int photoWidth, int x);
    void drawCursor(Graphics2D g2, FontMetrics fm, int x, int y);
    void drawCursorAtEnd(Graphics2D g2, FontMetrics fm, int x, int y, String text);
    int calculateLineHeight(FontMetrics fm);
}
