package interfaces;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JComponent;
import model.TextBlock;
import model.Stroke;
import model.Annotation;

public interface IDrawingRenderer {
    void drawCheckerboardBackground(Graphics2D g2, JComponent c);
    void drawWhiteSurface(Graphics2D g2, int width, int height);
    void drawStrokes(Graphics2D g2, List<Stroke> strokes);
    void drawTextBlocks(Graphics2D g2, List<TextBlock> textBlocks, BufferedImage image, TextBlock currentTextBlock);
    void drawAnnotations(Graphics2D g2, List<Annotation> annotations, BufferedImage image);
}
