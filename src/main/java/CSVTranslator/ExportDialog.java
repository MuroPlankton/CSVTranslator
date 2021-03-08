package CSVTranslator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ExportDialog extends JDialog {
    JFrame frame;

    private JPanel contentPane;
    private JButton leftButton;
    private JButton rightButton;
    private JComboBox langComboBox;
    private JLabel LangLabel;
    private JLabel infoLabel;

    private static final File outputTokenFile = new File(System.getProperty("user.dir") + "outputDirFile.txt");
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
        frame = new JFrame("LogInAndSignIn");
        frame.setContentPane(contentPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(calledFrom);
        frame.pack();
        frame.setVisible(true);
    }

    public ExportDialog(String libraryID, Component calledFrom) {
        this.calledFrom = calledFrom;
        this.libraryID = libraryID;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(leftButton);

        langComboBox.setVisible(false);
        langComboBox.addItem("Every language");
        LangLabel.setVisible(false);
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
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
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
            case "Platform specific files":
                finalizeExportDialog("Platform files");
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
        }
    }

    private void advanceDialogState() {
        infoLabel.setText("Which filetype do you want to export as?");
        leftButton.setText(".csv");
        rightButton.setText("Platform specific files");
        LangLabel.setVisible(true);
        langComboBox.setVisible(false);
    }

    private void finalizeExportDialog(String outputFileType) {
        CsvHandler csvHandler = new CsvHandler();
        csvHandler.exportLibrary(libraryID, outputFileType, langComboBox.getSelectedItem().toString());
        dispose();
    }

    public static void main(String[] args) {
//        ExportDialog dialog = new ExportDialog("");
//        dialog.pack();
//        dialog.setVisible(true);
//        System.exit(0);
    }
}
