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
    void loadImage(File file);
    BufferedImage getImage();
    boolean hasImage();
    Dimension getImageDimensions();
    
    boolean isFlipped();
    void toggleFlipped();
    
    void addStroke(Stroke stroke);
    List<Stroke> getStrokes();
    
    void setTextInsertionPoint(Point point);
    void addToCurrentText(char c);
    void backspaceCurrentText();
    void commitCurrentText();
    TextBlock getCurrentTextBlock();
    List<TextBlock> getTextBlocks();

    List<Annotation> getAnnotations();
    
    void clearAll();
    void clearAnnotations();
}
