package csvtranslator;

import javax.swing.*;
import javax.swing.plaf.basic.BasicOptionPaneUI;
import java.awt.*;

class TranslatorUI implements Runnable {
    private JFrame mainFrame;

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
        JComboBox fileDropDown = new JComboBox();
        filePanel.add(chooseFile);
        filePanel.add(fileDropDown);
        choosingPanel.add(filePanel, BorderLayout.NORTH);
        mainPanel.add(choosingPanel);

        JPanel osPanel = new JPanel();



        //mainPanel.add(new JLabel("Choose file:"));

        mainFrame.add(mainPanel);
        mainFrame.add(mainPanel);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);
        container.add(mainFrame);

    }


}