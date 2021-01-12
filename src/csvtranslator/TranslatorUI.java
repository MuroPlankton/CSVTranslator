package csvtranslator;


import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    List<String> languages = new ArrayList<>();

    private JComboBox<String> languageToSearch;

    private final Runnable runUI = this::startUI;

    TranslatorUI() {
    }

    public Runnable RunUI() {
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
        title.setFont(title.getFont().deriveFont(50.0f));
        title.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(30, 50, 40, 50));
        mainPanel.add(title);

        findFileButton = new JButton("Choose file");
        findFileButton.setFont(new Font("Arial", Font.PLAIN, 30));
        findFileButton.setBackground(Color.white);

        JPanel filePanel = new JPanel();
        filePanel.add(findFileButton);

        JLabel filePath = new JLabel();
        filePath.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JPanel fileChoosingPanel = new JPanel();
        fileChoosingPanel.setLayout(new BoxLayout(fileChoosingPanel, BoxLayout.Y_AXIS));
        fileChoosingPanel.add(filePanel);
        fileChoosingPanel.add(filePath);
        fileChoosingPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(fileChoosingPanel);

        findFileButton.addActionListener(e -> {
            fileChooser(filePath);
            System.out.println("search button pressed");
        });

        languageToSearch = new JComboBox<>();
        languageToSearch.addItem("Available languages");
        languageToSearch.setBackground(Color.white);
        languageToSearch.setPreferredSize(new Dimension(80, 40));
        languageToSearch.addItem("(all)");

        JPanel languagePanel = new JPanel();
        languagePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
        languagePanel.add(languageToSearch);
        mainPanel.add(languagePanel);

        JCheckBox folderSelectorEnabler = new JCheckBox("Choose translation folder (optional)");
        folderSelectorEnabler.setFont(new Font("Arial", Font.PLAIN, 20));

        JPanel folderSelectionPanel = new JPanel();
        folderSelectionPanel.add(folderSelectorEnabler);

        JLabel outputDirLabel = new JLabel();
        outputDirLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JPanel dirChoosingPanel = new JPanel();
        dirChoosingPanel.setLayout(new BoxLayout(dirChoosingPanel, BoxLayout.Y_AXIS));
        dirChoosingPanel.add(folderSelectionPanel);
        dirChoosingPanel.add(outputDirLabel);
        dirChoosingPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        mainPanel.add(dirChoosingPanel);

        createButton = new JButton("Create");
        createButton.setFont(new Font("Arial", Font.PLAIN, 20));
        createButton.setBackground(Color.white);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        bottomPanel.add(createButton);
        mainPanel.add(bottomPanel);

        createButton.addActionListener(e -> handleCsvToTranslateFiles());

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        folderSelectorEnabler.addActionListener(e -> {
            if (folderSelectorEnabler.isSelected()) {
                dirChooser(outputDirLabel, folderSelectorEnabler);
            } else {
                csvHandler.setOutputDir(null);
                outputDirLabel.setText("");
            }
        });

        mainFrame.add(mainPanel);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    private void fileChooser(JLabel filePath) {
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
            filePath.setText(chosenPath);

            saveLastCsvPath(chosenPath, filePath);

            languages = csvHandler.findLanguages(chosenPath);
            addLanguagesToDropDown();
        }
    }

    private void dirChooser(JLabel outputDirLabel, JCheckBox customDirChosen) {
        JFileChooser directoryChooser = new JFileChooser();
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        directoryChooser.setAcceptAllFileFilterUsed(false);

        int returnVal = directoryChooser.showOpenDialog(mainFrame);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File outputDir = directoryChooser.getCurrentDirectory();
            String choserPath = outputDir.getAbsolutePath();
            csvHandler.setOutputDir(choserPath);
            outputDirLabel.setText(choserPath);
        } else {
            customDirChosen.setSelected(false);
        }
    }

    private void saveLastCsvPath(String chosenPath, JLabel filePath) {
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
        languageToSearch.addItem("(all)");
        for (String lang : languages) {
            languageToSearch.addItem(lang);
        }
    }

    private void handleCsvToTranslateFiles() {
        csvHandler.readCsvAndCreateTranslateFiles(languageToSearch.getSelectedItem().toString());
    }
}