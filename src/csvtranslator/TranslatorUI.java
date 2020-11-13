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
    private String chosenPath;

    private Runnable runUI = () -> {
        startUI();
    };

    TranslatorUI() {
    }

    public Runnable getRunUI() {
        return runUI;
    }

    private void startUI() {
        createUIComponents();
    }

    private void createUIComponents() {
        mainFrame = new JFrame("Translator");
//        mainFrame.setPreferredSize(new Dimension(400, 350));

        JPanel mainPanel = new JPanel();
        BoxLayout mainLayout = new BoxLayout(mainPanel, BoxLayout.Y_AXIS);
        mainPanel.setLayout(mainLayout);

        JLabel title = new JLabel("CSV Translator");
        title.setFont(title.getFont().deriveFont(20.0f));
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
            try {
                fileChooser();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            System.out.println("search button pressed");
        });

        filePanel.add(chooseFile);
        filePanel.add(findFileButton);
        choosingPanel.add(filePanel);

        JPanel pathPanel = new JPanel();
        filePath = new JLabel();

        pathPanel.add(filePath);
        choosingPanel.add(pathPanel);

        mainPanel.add(choosingPanel);

        JPanel matchingPanel = new JPanel();
        JTextField textToMatch = new JTextField("Key or value");
        JComboBox<String> languageToSearch = new JComboBox<>();
        JButton findMatchButton = new JButton("Find match");
        JLabel result = new JLabel("The best match will display here.");

        matchingPanel.add(textToMatch);
        matchingPanel.add(languageToSearch);
        matchingPanel.add(findMatchButton);
        mainPanel.add(matchingPanel);
        mainPanel.add(result);

        createButton = new JButton("Create");
        closeButton = new JButton("Close");
        mainPanel.add(createButton);
        mainPanel.add(closeButton);

        createButton.addActionListener(e -> {
            try {
                createMethod();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
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

    private void fileChooser() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV files only", "csv"));
        String currentDir = System.getProperty("user.dir");
        fileChooser.setCurrentDirectory(new File(currentDir));
        int returnVal = fileChooser.showOpenDialog(mainFrame);

        if (returnVal == fileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            chosenPath = file.getAbsolutePath();
            filePath.setText(chosenPath);
            System.out.println(chosenPath);
        }
    }

    private void createMethod() throws IOException {
        csvHandler = new CsvHandler(chosenPath);
        csvHandler.csvReader(csvHandler.fileName);
    }
}