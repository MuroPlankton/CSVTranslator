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
    private JLabel filePath;

    private JTextField textToMatch;
    private JComboBox<String> languageToSearch;
    private JButton findMatchButton;

    private Runnable runUI = () -> {
        startUI();
    };

    TranslatorUI() {
    }

    public Runnable getRunUI() {
        return runUI;
    }

    private void startUI() {
        csvHandler = new CsvHandler();

        createUIComponents();
    }

    private void createUIComponents() {
        mainFrame = new JFrame("Translator");
//        mainFrame.setPreferredSize(new Dimension(400, 350));

        JPanel mainPanel = new JPanel();
        BoxLayout mainLayout = new BoxLayout(mainPanel, BoxLayout.Y_AXIS);
        mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setLayout(mainLayout);

        JLabel title = new JLabel("CSV Translator");
        title.setFont(title.getFont().deriveFont(20.0f));
        title.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(title);

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

        filePath = new JLabel();
        filePath.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        choosingPanel.add(filePath);

        choosingPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        mainPanel.add(choosingPanel);

        JPanel matchingPanel = new JPanel();

        matchingPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        textToMatch = new JTextField("Key or value");
        textToMatch.setColumns(20);
        languageToSearch = new JComboBox<>();
        languageToSearch.addItem("lang");
        JButton findMatchButton = new JButton("Find match");
        JLabel result = new JLabel("The best match will display here.");
        result.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        result.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(textToMatch);
        matchingPanel.add(languageToSearch);
        matchingPanel.add(findMatchButton);
        mainPanel.add(matchingPanel);
        mainPanel.add(result);

        JPanel bottomPanel = new JPanel();
        createButton = new JButton("Create");
        closeButton = new JButton("Close");
        bottomPanel.add(createButton);
        bottomPanel.add(closeButton);
        mainPanel.add(bottomPanel);

        findMatchButton.addActionListener(e -> {
            findBestMatch();
        });

        createButton.addActionListener(e -> {
            handleCsvToTranslateFiles();
        });

        closeButton.addActionListener(e -> {
            mainFrame.dispose();
        });

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mainFrame.add(mainPanel);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }


    private void fileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV files only", "csv"));
        String currentDir = System.getProperty("user.dir");
        fileChooser.setCurrentDirectory(new File(currentDir));
        int returnVal = fileChooser.showOpenDialog(mainFrame);

        if (returnVal == fileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String chosenPath = file.getAbsolutePath();
            csvHandler.setFileName(chosenPath);
            filePath.setText(chosenPath);
            System.out.println(chosenPath);
        }
    }

    private void handleCsvToTranslateFiles() {
        csvHandler.readCsvAndCreateTranslateFiles();
    }


    public void findBestMatch() {
        String sentence1 = textToMatch.getText();

        csvHandler.matchSentence(sentence1);

    }


}