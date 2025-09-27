import javax.swing.*;
import java.awt.*;

import javax.swing.*;
import java.awt.*;


public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Photo Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PhotoComponent photoPanel = new PhotoComponent("src/img/iamge.jpeg"); // <-- change path
        frame.add(new JScrollPane(photoPanel));

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }
}

