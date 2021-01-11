package csvtranslator;


import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class TranslatorUI {

    private static CsvHandler csvHandler;

    private JFrame mainFrame;
    private JButton findFileButton;
    private JButton createButton;
    private JButton closeButton;
    private JLabel filePath;

    List<String> languages = new ArrayList<>();

    private JComboBox<String> languageToSearch;

    private Runnable runUI = () -> {
        startUI();
    };


    TranslatorUI() {
    }

    public Runnable getRunUI() {
        return runUI;
    }

    private void startUI() {
        checkForLastCsvPath();
        csvHandler = new CsvHandler();
        createUIComponents();
    }
    private void checkForLastCsvPath() {

        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("last_csv_path.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (!scanner.hasNext()) {
            System.out.println("file empty, no previous paths found");
        } else {
            System.out.println(scanner.nextLine());
        }
        scanner.close();

    }
    private void createUIComponents() {
        mainFrame = new JFrame("Translator");

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

        findFileButton.addActionListener(e -> fileChooser());

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
        languageToSearch = new JComboBox<>();
        languageToSearch.addItem("Available languages");
        matchingPanel.add(languageToSearch);
        mainPanel.add(matchingPanel);

        JPanel bottomPanel = new JPanel();
        createButton = new JButton("Create");
        closeButton = new JButton("Close");
        bottomPanel.add(createButton);
        bottomPanel.add(closeButton);
        mainPanel.add(bottomPanel);

        createButton.addActionListener(e -> handleCsvToTranslateFiles());

        closeButton.addActionListener(e -> mainFrame.dispose());

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
        System.out.println(currentDir);

        int returnVal = fileChooser.showOpenDialog(mainFrame);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String chosenPath = file.getAbsolutePath();
            csvHandler.setFileName(chosenPath);

            saveLastCsvPath(chosenPath);

            languages = csvHandler.findLanguages(chosenPath);
            addLanguagesToDropDown();
        }
    }


    private void saveLastCsvPath(String chosenPath) {
        File saveFile = new File("last_csv_path.txt");

        try {
            FileWriter fileWriter = new FileWriter(saveFile);
            fileWriter.write(chosenPath);
            fileWriter.close();
            filePath.setText(chosenPath);

            Scanner fileReader = new Scanner(saveFile);
            System.out.println(String.format("This is the path of the last visited location: " + fileReader.nextLine()));
            fileReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addLanguagesToDropDown() {
        languageToSearch.removeAllItems();
        for (String lang : languages) {
            languageToSearch.addItem(lang);
        }
    }

    private void handleCsvToTranslateFiles() {
        csvHandler.readCsvAndCreateTranslateFiles();
    }
}