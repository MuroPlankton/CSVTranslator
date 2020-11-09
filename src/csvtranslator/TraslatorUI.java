package csvtranslator;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

class TranslatorUI implements Runnable {
    private JFrame mainFrame;

    private String chosenPath;

    public void run() {
        Container container = new Container();

        mainFrame = new JFrame("Translator");
        mainFrame.setPreferredSize(new Dimension(300, 500));

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel titleFrame = new JPanel();
        JLabel title = new JLabel("CSV Translator");
        titleFrame.add(title);
        mainPanel.add(titleFrame, BorderLayout.NORTH);


        JPanel choosingPanel = new JPanel(new BorderLayout());

        JPanel filePanel = new JPanel();
        JLabel chooseFile = new JLabel("Choose file:");
        JButton fileButton = new JButton();
        fileButton.setText("Choose file");
        filePanel.add(chooseFile);
        filePanel.add(fileButton);
        choosingPanel.add(filePanel, BorderLayout.NORTH);
        mainPanel.add(choosingPanel);

        JPanel osPanel = new JPanel();

        fileButton.addActionListener(e -> {
            chosenPath = fileChooser();
        });

        //mainPanel.add(new JLabel("Choose file:"));

        mainFrame.add(mainPanel);
        mainFrame.add(mainPanel);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);
        container.add(mainFrame);

    }

    private String fileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV files only", "csv"));
        int returnVal = fileChooser.showOpenDialog(mainFrame);

        if (returnVal == fileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            chosenPath = file.getAbsolutePath();
            System.out.println(chosenPath);
        }
        return "";
    }


}