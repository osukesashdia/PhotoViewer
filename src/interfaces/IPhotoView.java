package interfaces;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import model.TextBlock;
import model.Stroke;
import model.Annotation;
import controller.PhotoComponent;

public interface IPhotoView {
    // Drawing operations
    void draw(Graphics g, JComponent c, boolean isFlipped, BufferedImage image, 
              List<Stroke> strokes, List<TextBlock> textBlocks, List<Annotation> annotations, TextBlock currentTextBlock);
    
    // Size operations
    Dimension getPreferredSize(BufferedImage image);
    
    // UI creation
    JMenuBar createMenuBar(PhotoComponent controller);
    JPanel createToolBar();
    JPanel createStatusBar();
    void showImportDialog(PhotoComponent controller);
}
