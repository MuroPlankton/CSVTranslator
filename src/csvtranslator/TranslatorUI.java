package csvtranslator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

class TranslatorUI {

    private static CsvHandler csvHandler;

    private JFrame mainFrame;
    private JButton findFileButton;
    private JButton createButton;
    private JButton closeButton;
    private JComboBox osDropDown;
    private JTextField languageTextField;
    private JTextField filePath;

    private Runnable runUI = () -> {
        startUI();
    };

    public Runnable getRunUI() {
        return runUI;
    }

    private void startUI() {
        createUIComponents();
    }

    private void createUIComponents() {
        Container container = new Container();
        mainFrame = new JFrame("Translator");
        mainFrame.setPreferredSize(new Dimension(190, 270));

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
        findFileButton = new JButton("Search");

        filePath = new JTextField();
        filePath.setBorder(javax.swing.BorderFactory.createEmptyBorder());

        filePath.setEditable(false);

        filePanel.add(chooseFile);
        filePanel.add(findFileButton);
        filePanel.add(filePath);
        choosingPanel.add(filePanel);

        JPanel osPanel = new JPanel();
        JLabel targetOS = new JLabel("Target OS:");
        String OS[] = {"android", "ios"};
        osDropDown = new JComboBox(OS);

        osPanel.add(targetOS);
        osPanel.add(osDropDown);
        choosingPanel.add(osPanel);

        JPanel languagePanel = new JPanel();
        JLabel language = new JLabel("Language:");
        languageTextField = new JTextField(5);

        languagePanel.add(language);
        languagePanel.add(languageTextField);
        choosingPanel.add(languagePanel);

        mainPanel.add(choosingPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        createButton = new JButton("Create");
        closeButton = new JButton("Close");
        buttonPanel.add(createButton);
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        createButton.addActionListener(e -> {
            try {
                createMethod();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        mainFrame.setResizable(false);
        mainFrame.add(mainPanel);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);
        container.add(mainFrame);
    }

    private void createMethod() throws IOException {
        String osSelected = osDropDown.getSelectedItem().toString();
        String languageSelected = languageTextField.getText();

        csvHandler = new CsvHandler("file", osSelected, languageSelected);
        csvHandler.beginWriting();
        csvHandler.csvReader(csvHandler.fileName);
        csvHandler.writeOneRow((csvHandler.key == 0) ? "</resources>" : "");
        csvHandler.stopWriting();
    }
}