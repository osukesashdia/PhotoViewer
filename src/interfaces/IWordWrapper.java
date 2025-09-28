package interfaces;

import java.awt.Graphics2D;
import java.awt.FontMetrics;

public interface IWordWrapper {
    int drawWrappedText(Graphics2D g2, FontMetrics fm, String text, int x, int y, int maxWidth);
    int drawCurrentLineAndAdvance(Graphics2D g2, FontMetrics fm, int x, int y, 
                                 StringBuilder currentLine, int lineHeight, int maxWidth);
    int drawLongWord(Graphics2D g2, FontMetrics fm, int x, int y, int lineHeight, int maxWidth, String word);
}
