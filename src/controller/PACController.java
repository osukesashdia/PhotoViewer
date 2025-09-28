package controller;

import java.awt.Graphics;
import javax.swing.JComponent;

public abstract class PACController extends JComponent {

    protected abstract void refreshView();

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
