package interfaces;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import model.TextBlock;
import model.Stroke;
import model.Annotation;

public interface IPhotoModel {
    // Image operations
    void loadImage(File file);
    BufferedImage getImage();
    boolean hasImage();
    Dimension getImageDimensions();
    
    // Flip operations
    boolean isFlipped();
    void toggleFlipped();
    
    // Annotation operations
    void addStroke(Stroke stroke);
    List<Stroke> getStrokes();
    
    // Text operations
    void setTextInsertionPoint(Point point);
    Point getTextInsertionPoint();
    void addToCurrentText(char c);
    void backspaceCurrentText();
    void commitCurrentText();
    TextBlock getCurrentTextBlock();
    List<TextBlock> getTextBlocks();
    
    // Annotation management
    void addAnnotation(Annotation annotation);
    List<Annotation> getAnnotations();
    
    // Clear operations
    void clearAll();
    void clearAnnotations();
}
