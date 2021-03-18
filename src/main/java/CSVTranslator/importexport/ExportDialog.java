package CSVTranslator.importexport;

import CSVTranslator.FireBaseRequests;
import CSVTranslator.auth.AuthHelper;
import CSVTranslator.util.Pair;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ExportDialog extends JDialog {
    JFrame frame;

    private JPanel contentPane;
    private JButton leftButton;
    private JButton rightButton;
    private JComboBox langComboBox;
    private JLabel LangLabel;
    private JLabel infoLabel;

    private static final File outputTokenFile = new File(System.getProperty("user.dir") + "\\outputDirFile.txt");
    private String outputPath;
    private String libraryID;
    private String outputFileType;
    private String selectedLang;
    private Component calledFrom;

    private final Runnable runUI = this::createUI;

    public final Runnable runUI() {
        return runUI;
    }

    private void createUI() {
        frame = new JFrame("Export");
        frame.setContentPane(contentPane);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(calledFrom);
        frame.pack();
        frame.setVisible(true);
    }

    public ExportDialog(String libraryID, Component calledFrom) {
        this.calledFrom = calledFrom;
        this.libraryID = libraryID;
        setModal(true);
        getRootPane().setDefaultButton(leftButton);

        langComboBox.setEnabled(false);
        langComboBox.addItem("Every language");
        Pair<String, Boolean> langResponseInfo = new FireBaseRequests().getData(String.format(
                "https://csv-android-app-f0353-default-rtdb.firebaseio.com/libraries/%s/languages.json" +
                        "?auth=%s", libraryID, AuthHelper.getInstance().getIDToken()));
        if (langResponseInfo.getValue()) {
            JsonObject langInfoObject = JsonParser.parseString(langResponseInfo.getKey()).getAsJsonObject();
            for (String langKey : langInfoObject.keySet()) {
                langComboBox.addItem(langKey);
            }
        }
        LangLabel.setEnabled(false);
        infoLabel.setText("Directory to export to:");
        leftButton.setText("Default/previous");
        rightButton.setText("Custom");

        leftButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        rightButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        switch (leftButton.getText()) {
            case "Default/previous":
                advanceDialogState();
                tryFindPreviousDir();
                break;
            case ".csv":
                finalizeExportDialog(leftButton.getText());
                break;
            default:
                System.out.println("What the heck did you do?");
                dispose();
        }
    }

    private void tryFindPreviousDir() {
        try {
            Scanner scanner = new Scanner(outputTokenFile);
            outputPath = scanner.nextLine();
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Export path file not found! Using default path.");
        }
    }

    private void onCancel() {
        switch (rightButton.getText()) {
            case "Custom":
                dirChooser();
                break;
            case "Platform files":
                finalizeExportDialog("Platform files");
                break;
            default:
                System.out.println("What the heck did you do?");
                dispose();
        }
    }

    private void dirChooser() {
        JFileChooser directoryChooser = new JFileChooser();
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        directoryChooser.setAcceptAllFileFilterUsed(false);

        int returnVal = directoryChooser.showOpenDialog(contentPane);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File outputDir = directoryChooser.getSelectedFile();
            outputPath = outputDir.getAbsolutePath();
            advanceDialogState();

            try {
                FileWriter tokenWriter = new FileWriter(System.getProperty("user.dir") + "\\outputDirFile.txt");
                tokenWriter.write(outputPath);
                tokenWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void advanceDialogState() {
        infoLabel.setText("Which filetype do you want to export as?");
        leftButton.setText(".csv");
        rightButton.setText("Platform files");
        LangLabel.setEnabled(true);
        langComboBox.setEnabled(true);
    }

    private void finalizeExportDialog(String outputFileType) {
        LibraryExporter exporter = new LibraryExporter();
        exporter.exportLibrary(libraryID, outputFileType, langComboBox.getSelectedItem().toString(), outputPath);
        dispose();
    }
}
