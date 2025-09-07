import javax.swing.*;
import java.awt.*;

public class Gui {
    private JFrame frame;
    private JPanel mainPanel;
    private JLabel statusLabel;
    private JPanel viewPanel;
    private JLabel imageLabel;

    public static void main(String[] args) {
        var photoViewer = new Gui();
        photoViewer.setGUI();
    }

    private void setGUI(){
        frame = new JFrame("PhotoViewer");
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        viewPanel = new JPanel();


        var icon = new ImageIcon("src/shishito.jpg");
        imageLabel = new JLabel(icon);

        setStatusLabel();
        setMenuBar();
        setToolBar();
        setViewArea();

        frame.setSize(800, 1000);
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void setStatusLabel(){
        statusLabel = new JLabel("None");
        frame.add(statusLabel, BorderLayout.SOUTH);
    }

    private void setMenuBar(){
        var menuBar = new JMenuBar();
        var fileMenu = new JMenu("File");
        var importItem = new JMenuItem("Import");
        var deleteItem = new JMenuItem("Delete");
        var quitItem = new JMenuItem("Quit");
        var fileChooser = new JFileChooser();


        importItem.addActionListener(e -> {fileChooser.showOpenDialog(frame);});
        deleteItem.addActionListener(e -> {statusLabel.setText(deleteItem.getText());});
        quitItem.addActionListener(e -> {System.exit(0);});
        fileMenu.add(importItem);
        fileMenu.add(deleteItem);
        fileMenu.add(quitItem);

        var viewMenu = new JMenu("View");
        var photoItem = new JRadioButtonMenuItem("Photo", true);
        var browseItem = new JRadioButtonMenuItem("Browse");
        var viewGroup = new ButtonGroup();


        viewGroup.add(photoItem);
        viewGroup.add(browseItem);

        photoItem.addActionListener(e -> {
            viewPanel.add(imageLabel);
            viewPanel.revalidate();
            viewPanel.repaint();
        });
        browseItem.addActionListener(e -> {
            statusLabel.setText(browseItem.getText());
            viewPanel.removeAll();                     // remove any images/components
            viewPanel.revalidate();                    // refresh layout
            viewPanel.repaint();
        });
        viewMenu.add(photoItem);
        viewMenu.add(browseItem);


        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        frame.setJMenuBar(menuBar);
    }

    private void setToolBar(){
        var toolBarPanel = new JPanel();
        String[] categories = {"People", "Foods"};

        for(String category : categories){
            var categoryToggleButton = new JToggleButton(category);
            categoryToggleButton.addActionListener(e -> {statusLabel.setText(categoryToggleButton.getText());});
            toolBarPanel.add(categoryToggleButton);
        }

        toolBarPanel.setBackground(Color.LIGHT_GRAY);
        mainPanel.add(toolBarPanel,  BorderLayout.NORTH);
    }

    private void setViewArea(){
        viewPanel.add(imageLabel);
        mainPanel.add(viewPanel,  BorderLayout.CENTER);
    }
}
