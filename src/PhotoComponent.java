import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;


public class PhotoComponent extends JPanel {
    private PhotoModel model;
    private PhotoView view;

    public PhotoComponent(String imagePath) {
        this.model = new PhotoModel(new File(imagePath));
        this.view = new PhotoView(model);

        // Mouse click → add annotation
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                String text = JOptionPane.showInputDialog(
                        PhotoComponent.this,
                        "Enter annotation text:");
                if (text != null && !text.isBlank()) {
                    model.addAnnotation(new Annotation(text, e.getX(), e.getY()));
                    repaint();
                }
            }
        });

        // Keyboard press → flip
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == 'f') {
                    model.toggleFlipped();
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        view.draw(g, this);
    }

    @Override
    public Dimension getPreferredSize() {
        if (model.getImage() != null) {
            return new Dimension(model.getImage().getWidth(), model.getImage().getHeight());
        }
        return new Dimension(400, 300);
    }

}