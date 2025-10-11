package utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JComponent;
import model.Stroke;
import model.Annotation;
import view.AnnotationRenderer;
import view.StrokeRenderer;
import utils.Constants;

public class DrawingUtils {
    private final AnnotationRenderer annotationRenderer = new AnnotationRenderer();
    private final StrokeRenderer strokeRenderer = new StrokeRenderer();
    
    public void drawCheckerboardBackground(Graphics2D g2, JComponent c) {
        int size = Constants.CHECKERBOARD_SIZE;
        
        for (int y = 0; y < c.getHeight(); y += size) {
            for (int x = 0; x < c.getWidth(); x += size) {
                if (((x / size) + (y / size)) % 2 == 0) {
                    g2.setColor(Constants.CHECKER_LIGHT);
                } else {
                    g2.setColor(Constants.CHECKER_DARK);
                }
                g2.fillRect(x, y, size, size);
            }
        }
    }
    
    public void drawWhiteSurface(Graphics2D g2, int width, int height) {
        g2.setColor(Constants.WHITE);
        g2.fillRect(0, 0, width, height);
        g2.setColor(Constants.LIGHT_GRAY);
        g2.setStroke(new java.awt.BasicStroke(Constants.BORDER_STROKE_WIDTH));
        g2.drawRect(0, 0, width - 1, height - 1);
    }

    public void drawStrokes(Graphics2D g2, List<Stroke> strokes) {
        for (Stroke stroke : strokes) {
            strokeRenderer.drawStroke(g2, stroke);
        }
    }
    
    public void drawAnnotations(Graphics2D g2, List<Annotation> annotations, BufferedImage image) {
        for (Annotation ann : annotations) {
            int photoWidth = image != null ? image.getWidth() : Constants.DEFAULT_WIDTH;
            annotationRenderer.drawAnnotation(g2, ann, photoWidth);
        }
    }
}
