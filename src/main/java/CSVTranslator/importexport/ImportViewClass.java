package CSVTranslator.importexport;

import com.google.gson.JsonObject;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Stack;

public class ImportViewClass {

    JFrame frame;
    private JComboBox langComboBox;
    private JTextArea langNameArea;
    private JButton nextLanguageButton;
    private JLabel langNameLabel;
    private JPanel contentPane;

    private Component calledFrom;
    private CSVImporter csvImporter;
    private Stack<String> langStack = new Stack<>();
    private String chosenFile;
    private String fileName;
    private JsonObject languagesObject = new JsonObject();

    private final Runnable runUI = this::createUI;

    public final Runnable runUI() {
        return runUI;
    }

    private void createUI() {
        frame = new JFrame("Import");
        frame.setContentPane(contentPane);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(calledFrom);
        frame.pack();
        frame.setVisible(true);
    }

    public ImportViewClass(Component calledFrom) {
        this.calledFrom = calledFrom;
        csvImporter = new CSVImporter();
        fileChooser();

        langNameLabel.setText(String.format("%s stands for:", langStack.peek()));
        nextLanguageButton.addActionListener(actionListener);
    }

    ActionListener actionListener = e -> {
        if (langNameArea.getText().isEmpty()) {

        } else {
            languagesObject.addProperty(langStack.pop(), langNameArea.getText());
            if (nextLanguageButton.getText().equals("Next language")) {
                langNameLabel.setText(String.format("%s stands for:", langStack.peek()));
                langNameArea.setText("");
                if (langStack.size() == 1) {
                    nextLanguageButton.setText("Done, import");
                }
            } else {
                frame.setVisible(false);
                frame.dispose();
                csvImporter.readCsvAndImportToFirebase(chosenFile, fileName, languagesObject, langComboBox.getSelectedItem().toString());
            }
        }
    };

    private void fileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV files only", "csv"));
        String currentDir = System.getProperty("user.dir");
        fileChooser.setCurrentDirectory(new File(currentDir));

        int returnVal = fileChooser.showOpenDialog(contentPane);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            chosenFile = file.getAbsolutePath();
            fileName = file.getName();

            List<String> languages = csvImporter.findLanguages(chosenFile);
            langComboBox.removeAllItems();
            for (String lang : languages) {
                langComboBox.addItem(lang);
                langStack.add(lang);
            }
        }
    }
}