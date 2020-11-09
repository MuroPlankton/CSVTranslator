package csvtranslator;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;

class TranslatorUI {

    private static CsvHandler csvHandler;

    private JFrame mainFrame;
    private JButton findFileButton;
    private JButton createButton;
    private JButton closeButton;
    private JComboBox osDropDown;
    private JTextField languageTextField;
    private JTextArea filePath;
    private String chosenPath;

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

        filePanel.add(chooseFile);
        filePanel.add(findFileButton);
        choosingPanel.add(filePanel);

        findFileButton.addActionListener(e -> {
            fileChooser();
            System.out.println("search button pressed");
        });

        filePanel.add(chooseFile);
        filePanel.add(findFileButton);
        choosingPanel.add(filePanel);

        JPanel pathPanel = new JPanel();
        filePath = new JTextArea(".");
        filePath.setLineWrap(true);
        filePath.setEditable(false);
        filePath.setBorder(null);
        pathPanel.add(filePath);
        choosingPanel.add(pathPanel);

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

    private void fileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV files only", "csv"));
        int returnVal = fileChooser.showOpenDialog(mainFrame);

        if (returnVal == fileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            chosenPath = file.getAbsolutePath();
            filePath.setText(chosenPath);
            System.out.println(chosenPath);
        }
    }
    private void createMethod() throws IOException {
        String osSelected = osDropDown.getSelectedItem().toString();
        String languageSelected = languageTextField.getText();

        csvHandler = new CsvHandler(chosenPath, osSelected, languageSelected);
        csvHandler.beginWriting();
        csvHandler.csvReader(csvHandler.fileName);
        csvHandler.writeOneRow((csvHandler.key == 0) ? "</resources>" : "");
        csvHandler.stopWriting();
    }
}