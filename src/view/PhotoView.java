package view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import java.io.File;
import interfaces.IPhotoView;
import interfaces.IDrawingRenderer;
import model.TextBlock;
import model.Stroke;
import model.Annotation;
import controller.PhotoComponent;
import utils.Constants;
import utils.DrawingUtils;

public class PhotoView implements IPhotoView {
    private final IDrawingRenderer drawingRenderer = new DrawingUtils();
    
    public PhotoView() {
    }

    public void draw(Graphics g, JComponent c, boolean isFlipped, BufferedImage image, 
                    List<Stroke> strokes, List<TextBlock> textBlocks, List<Annotation> annotations, TextBlock currentTextBlock) {
        Graphics2D g2 = (Graphics2D) g.create();

        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            drawBackground(g2, c);
            
            if (isFlipped) {
                drawPhotoBack(g2, c, image);
                drawStrokes(g2, strokes);
                drawTextBlocks(g2, textBlocks, image, currentTextBlock);
                drawAnnotations(g2, annotations, image);
                } else {
                drawPhoto(g2, c, image, isFlipped);
            }
        } finally {
            g2.dispose();
        }
    }

    private void drawBackground(Graphics2D g2, JComponent c) {
        drawingRenderer.drawCheckerboardBackground(g2, c);
    }

    private void drawPhoto(Graphics2D g2, JComponent c, BufferedImage image, boolean isFlipped) {
        if (image != null) {
            int w = image.getWidth();
            if (isFlipped) {
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-w, 0);
                g2.drawImage(image, tx, null);
            } else {
                g2.drawImage(image, 0, 0, c);
            }
        }
    }

    private void drawPhotoBack(Graphics2D g2, JComponent c, BufferedImage image) {
        int surfaceWidth, surfaceHeight;
        if (image != null) {
            surfaceWidth = image.getWidth();
            surfaceHeight = image.getHeight();
        } else {
            surfaceWidth = c.getWidth();
            surfaceHeight = c.getHeight();
        }
        drawingRenderer.drawWhiteSurface(g2, surfaceWidth, surfaceHeight);
    }

    private void drawStrokes(Graphics2D g2, List<Stroke> strokes) {
        drawingRenderer.drawStrokes(g2, strokes);
    }

    private void drawTextBlocks(Graphics2D g2, List<TextBlock> textBlocks, BufferedImage image, TextBlock currentTextBlock) {
        int surfaceWidth, surfaceHeight;
        if (image != null) {
            surfaceWidth = image.getWidth();
            surfaceHeight = image.getHeight();
        } else {
            surfaceWidth = Constants.DEFAULT_WIDTH;
            surfaceHeight = Constants.DEFAULT_HEIGHT;
        }
        
        g2.setClip(0, 0, surfaceWidth, surfaceHeight);
        drawingRenderer.drawTextBlocks(g2, textBlocks, image, currentTextBlock);
    }

    private void drawAnnotations(Graphics2D g2, List<Annotation> annotations, BufferedImage image) {
        int surfaceWidth, surfaceHeight;
        if (image != null) {
            surfaceWidth = image.getWidth();
            surfaceHeight = image.getHeight();
        } else {
            surfaceWidth = Constants.DEFAULT_WIDTH;
            surfaceHeight = Constants.DEFAULT_HEIGHT;
        }
        
        g2.setClip(0, 0, surfaceWidth, surfaceHeight);
        drawingRenderer.drawAnnotations(g2, annotations, image);
    }

    public Dimension getPreferredSize(BufferedImage image) {
        if (image != null) {
            return new Dimension(image.getWidth(), image.getHeight());
        }
        return new Dimension(Constants.DEFAULT_WIDTH, Constants.DEFAULT_HEIGHT);
    }

    public JMenuBar createMenuBar(PhotoComponent controller) {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu(controller));
        menuBar.add(createViewMenu());
        return menuBar;
    }
    
    private JMenu createFileMenu(PhotoComponent controller) {
        JMenu fileMenu = new JMenu("File");
        
        fileMenu.add(createImportMenuItem(controller));
        fileMenu.add(createDeleteMenuItem(controller));
        fileMenu.add(createQuitMenuItem());
        
        return fileMenu;
    }
    
    private JMenuItem createImportMenuItem(PhotoComponent controller) {
        JMenuItem importItem = new JMenuItem("Import");
        importItem.addActionListener(e -> controller.importImage());
        return importItem;
    }
    
    private JMenuItem createDeleteMenuItem(PhotoComponent controller) {
        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(e -> controller.deletePhoto());
        return deleteItem;
    }
    
    private JMenuItem createQuitMenuItem() {
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.addActionListener(e -> System.exit(0));
        return quitItem;
    }
    
    private JMenu createViewMenu() {
        JMenu viewMenu = new JMenu("View");
        
        ButtonGroup viewGroup = new ButtonGroup();
        JRadioButtonMenuItem photoItem = createPhotoMenuItem();
        JRadioButtonMenuItem browseItem = createBrowseMenuItem();
        
        viewGroup.add(photoItem);
        viewGroup.add(browseItem);
        viewMenu.add(photoItem);
        viewMenu.add(browseItem);
        
        return viewMenu;
    }
    
    private JRadioButtonMenuItem createPhotoMenuItem() {
        JRadioButtonMenuItem photoItem = new JRadioButtonMenuItem("Photo");
        photoItem.setSelected(true);
        photoItem.addActionListener(e -> {
        });
        return photoItem;
    }
    
    private JRadioButtonMenuItem createBrowseMenuItem() {
        JRadioButtonMenuItem browseItem = new JRadioButtonMenuItem("Browse");
        browseItem.addActionListener(e -> {
        });
        return browseItem;
    }

    public JPanel createToolBar() {
        JPanel toolBarPanel = new JPanel();
        String[] categories = {"People", "Foods"};
        for (String category : categories) {
            JToggleButton categoryToggleButton = new JToggleButton(category);
            categoryToggleButton.addActionListener(e -> {
                updateStatusBar(toolBarPanel, category + " button clicked");
            });
            toolBarPanel.add(categoryToggleButton);
        }
        toolBarPanel.setBackground(Color.LIGHT_GRAY);
        return toolBarPanel;
    }

    public JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
        statusBar.setBackground(Color.LIGHT_GRAY);
        JLabel statusLabel = new JLabel("Ready");
        statusLabel.setName("statusLabel"); 
        statusBar.add(statusLabel, BorderLayout.WEST);
        return statusBar;
    }

    private void updateStatusBar(JPanel toolBarPanel, String message) {
        JComponent parent = (JComponent) toolBarPanel.getParent();
        while (parent != null) {
            JLabel statusLabel = findStatusLabel(parent);
            if (statusLabel != null) {
                statusLabel.setText(message);
                break;
            }
            parent = (JComponent) parent.getParent();
        }
    }

    private JLabel findStatusLabel(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JLabel && "statusLabel".equals(comp.getName())) {
                return (JLabel) comp;
            } else if (comp instanceof Container) {
                JLabel found = findStatusLabel((Container) comp);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    public void showImportDialog(PhotoComponent controller) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Image File");
        File imgDir = new File("img");
        if (imgDir.exists()) {
            fileChooser.setCurrentDirectory(imgDir);
        }
        int result = fileChooser.showOpenDialog(controller);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            controller.loadImage(selectedFile);
        }
    }
    
}
