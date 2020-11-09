package csvtranslator;

import javax.swing.*;
import java.awt.*;

class TranslatorUI implements Runnable {
    private JFrame mainFrame;

    public void run() {
        Container container = new Container();

        mainFrame = new JFrame("Translator");
        mainFrame.setPreferredSize(new Dimension(200, 220));

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel titleFrame = new JPanel();
        JLabel title = new JLabel("CSV Translator");
        titleFrame.add(title);
        mainPanel.add(titleFrame, BorderLayout.NORTH);


        JPanel choosingPanel = new JPanel();
        BoxLayout layout = new BoxLayout(choosingPanel, BoxLayout.Y_AXIS);
        choosingPanel.setLayout(layout);

        JPanel filePanel = new JPanel();
        JLabel chooseFile = new JLabel("Choose file:");
        JButton findFileButton = new JButton("Search");

        filePanel.add(chooseFile);
        filePanel.add(findFileButton);
        choosingPanel.add(filePanel);

        JPanel osPanel = new JPanel();
        JLabel targetOS = new JLabel("Target OS:");
        String OS[] = {"android", "ios"};
        JComboBox osDropDown = new JComboBox(OS);

        osPanel.add(targetOS);
        osPanel.add(osDropDown);
        choosingPanel.add(osPanel);

        JPanel languagePanel = new JPanel();
        JLabel language = new JLabel("Language:");
        JTextField languageTextField = new JTextField(5);

        languagePanel.add(language);
        languagePanel.add(languageTextField);
        choosingPanel.add(languagePanel);

        mainPanel.add(choosingPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        JButton createBtn = new JButton("Create");
        JButton closeBtn = new JButton("Close");
        buttonPanel.add(createBtn);
        buttonPanel.add(closeBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainFrame.setResizable(false);

        mainFrame.add(mainPanel);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);
        container.add(mainFrame);

    }


}