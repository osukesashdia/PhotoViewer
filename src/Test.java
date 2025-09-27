import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

// =======================
// Model: PhotoModel
// =======================
class PhotoModel2 {
    private BufferedImage image;
    private boolean flipped;
    private List<String> annotations; // for simplicity, just text annotations

    public PhotoModel2(File file) {
        try {
            this.image = ImageIO.read(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.flipped = false;
        this.annotations = new ArrayList<>();
    }

    public BufferedImage getImage() {
        return image;
    }

    public boolean isFlipped() {
        return flipped;
    }

    public void toggleFlipped() {
        this.flipped = !this.flipped;
    }

    public void addAnnotation(String text) {
        annotations.add(text);
    }

    public List<String> getAnnotations() {
        return annotations;
    }
}

// =======================
// View: PhotoView
// =======================
class PhotoView2 {
    private PhotoModel2 model;

    public PhotoView2(PhotoModel2 model) {
        this.model = model;
    }

    public void draw(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();

        BufferedImage img = model.getImage();
        if (img != null) {
            int w = img.getWidth();
            int h = img.getHeight();

            // Handle flipping
            if (model.isFlipped()) {
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-w, 0);
                g2.drawImage(img, tx, null);
            } else {
                g2.drawImage(img, 0, 0, c);
            }
        }

        // Draw annotations
        g2.setColor(Color.RED);
        int y = 20;
        for (String note : model.getAnnotations()) {
            g2.drawString(note, 10, y);
            y += 20;
        }

        g2.dispose();
    }
}

// =======================
// Controller: PhotoComponent
// =======================
class PhotoComponent2 extends JComponent {
    private PhotoModel2 model;
    private PhotoView2 view;

    public PhotoComponent2(PhotoModel2 model) {
        this.model = model;
        this.view = new PhotoView2(model);

        // Add mouse listener for adding annotations
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                String text = JOptionPane.showInputDialog(
                        PhotoComponent2.this,
                        "Enter annotation text:");
                if (text != null && !text.isBlank()) {
                    model.addAnnotation(text + " @(" + e.getX() + "," + e.getY() + ")");
                    repaint();
                }
            }
        });

        // Add key listener for flipping
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

// =======================
// Demo Application
// =======================
public class Test {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Load example image
                File file = new File("src/img/iamge.jpeg"); // change path
                PhotoModel2 model = new PhotoModel2(file);

                JFrame frame = new JFrame("Photo Viewer");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new JScrollPane(new PhotoComponent2(model)));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Could not load image file.");
            }
        });
    }
}
