import javax.swing.*;
import java.awt.*;

public class GUI {
    private JFrame frame;
    private JPanel mainPanel;
    private JLabel statusLabel;

    public static void main(String[] args) {
        var PhotoViewer = new GUI();
        PhotoViewer.setGUI();
    }

    private void setGUI(){
        frame = new JFrame("PhotoViewer");
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        setStatusLabel();
        setMenuBar();
        setToolBar();
        set_view_area();

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

        photoItem.addActionListener(e -> {statusLabel.setText(photoItem.getText());});
        browseItem.addActionListener(e -> {statusLabel.setText(browseItem.getText());});
        viewMenu.add(photoItem);
        viewMenu.add(browseItem);


        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        frame.setJMenuBar(menuBar);
    }

    private void setToolBar(){
        var category_selector1 = new JToggleButton("People");
        var category_selector2 = new JToggleButton("Foods");
        var tool_bar_panel = new JPanel();

        category_selector1.addActionListener(e -> {statusLabel.setText(category_selector1.getText());});
        category_selector2.addActionListener(e -> {statusLabel.setText(category_selector2.getText());});

        tool_bar_panel.add(category_selector1);
        tool_bar_panel.add(category_selector2);
        tool_bar_panel.setBackground(Color.LIGHT_GRAY);
        mainPanel.add(tool_bar_panel,  BorderLayout.NORTH);
    }

    private void set_view_area(){
        var view_panel = new JPanel();
        view_panel.setBackground(Color.BLACK);
        mainPanel.add(view_panel, BorderLayout.CENTER);

    }
}
