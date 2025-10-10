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
    
    boolean isAnnotationsVisible();
    void toggleAnnotationsVisible();
    
    // Selection management
    Object getSelectedObject();
    void setSelectedObject(Object object);
    boolean isDragging();
    void setDragging(boolean dragging);
    Point getDragOffset();
    void setDragOffset(Point offset);
    void clearSelection();
    
    void addStroke(Stroke stroke);
    List<Stroke> getStrokes();
    
    void setTextInsertionPoint(Point point);
    void setCurrentTextBlock(TextBlock textBlock);
    void addToCurrentText(char c);
    void backspaceCurrentText();
    void commitCurrentText();
    TextBlock getCurrentTextBlock();
    List<TextBlock> getTextBlocks();

    List<Annotation> getAnnotations();
    
    void clearAll();
    void clearAnnotations();
}
