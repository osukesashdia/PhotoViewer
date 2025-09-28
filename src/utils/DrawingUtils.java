package utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JComponent;
import interfaces.IDrawingRenderer;
import model.TextBlock;
import model.Stroke;
import model.Annotation;
import utils.Constants;

public class DrawingUtils implements IDrawingRenderer {
    
    @Override
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
    
    @Override
    public void drawWhiteSurface(Graphics2D g2, int width, int height) {
        g2.setColor(Constants.WHITE);
        g2.fillRect(0, 0, width, height);
        g2.setColor(Constants.LIGHT_GRAY);
        g2.setStroke(new java.awt.BasicStroke(Constants.BORDER_STROKE_WIDTH));
        g2.drawRect(0, 0, width - 1, height - 1);
    }
    
    @Override
    public void drawStrokes(Graphics2D g2, List<Stroke> strokes) {
        for (Stroke stroke : strokes) {
            stroke.draw(g2);
        }
    }
    
    @Override
    public void drawTextBlocks(Graphics2D g2, List<TextBlock> textBlocks, BufferedImage image, TextBlock currentTextBlock) {
        for (TextBlock textBlock : textBlocks) {
            if (!textBlock.isEmpty()) {
                int photoWidth = image != null ? image.getWidth() : Constants.DEFAULT_WIDTH;
                boolean isActive = (textBlock == currentTextBlock);
                
                setTextBlockColor(g2, textBlock, isActive);
                textBlock.draw(g2, photoWidth, isActive);
            }
        }
    }
    
    @Override
    public void drawAnnotations(Graphics2D g2, List<Annotation> annotations, BufferedImage image) {
        for (Annotation ann : annotations) {
            int photoWidth = image != null ? image.getWidth() : Constants.DEFAULT_WIDTH;
            ann.draw(g2, photoWidth);
        }
    }
    
    private static void setTextBlockColor(Graphics2D g2, TextBlock textBlock, boolean isActive) {
        if (isActive) {
            g2.setColor(Color.GRAY);        
        } else if (textBlock.isCommitted()) {
            g2.setColor(Color.BLUE);        
        } else {
            g2.setColor(Color.LIGHT_GRAY);  
        }
    }
}
