package csvtranslator;

import javax.swing.*;

public class CsvAppUiClass {
    private JPanel basePanel;
    private JButton importButton;
    private JButton profileButton;

    private JList librariesList;
    private JLabel librariesLabel;

    private JTextField libraryNameTextField;
    private JList textsList;

    private JPanel langAddingPanel;
    private JTextField langCodeField;
    private JTextField langNameField;
    private JButton langAddingButton;
    private JLabel langAmountLabel;
    private JButton newTextButton;
    private JButton exportOptionsButton;
    private JPopupMenu exportOptionsMenu;
    private JMenuItem currentDirExportOption;
    private JMenuItem customDirExportOption;

    private JTextField textNameField;
    private JTextArea textDescArea;
    private JTextField webKeyField;
    private JTextField androidKeyField;
    private JTextField iosKeyField;
    private JSpinner langSpinner;
    private JTextField translationField;
}
