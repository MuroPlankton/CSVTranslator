import com.intellij.uiDesigner.compiler.StringPropertyCodeGenerator;

import javax.swing.*;

public class MainView {
    FireBaseRequests fireBaseRequests = new FireBaseRequests();

    private JPanel mainPanel;
    private JPanel secondPanel;
    private JSplitPane splitPane;
    private JPanel mainView;
    private JLabel librariesLabel;
    private JList libraryList;
    private JPanel editTranslationsPane;
    private JLabel libraryNameLabel;
    private JList libraryAdapter;
    private JPanel libraryActionsPane;
    private JButton newTranslation;
    private JTextField languageNameTextField;
    private JTextField languageCodeTextField;
    private JButton addButton;
    private JTextField translationNameTextField;
    private JTextField translationDescrpitionTextField;
    private JTextField androidKeyTextField;
    private JTextField iosKeyTextField;
    private JTextField webKeyTextField;
    private JTextField translationNameTextField2;
    private JButton saveButton;
    private JLabel languageCountLabe;
    private JLabel languageCodeLabel;
    private JLabel LanguageNameLabel;
    private JLabel translationNameLabel;
    private JLabel translationDescriptionLabel;
    private JLabel iosKeyLabel;
    private JLabel webKeyLabel;
    private JLabel languageSpinnerLabel;
    private JLabel translationsLabel;
    private JLabel androidKeyLabel;
    private JComboBox languagesDropDown;

    private int languageCount;
    private final String userID = "";
    private final String userIDToken = "";
    private final String libraryID = "";

    public MainView() {

        addButton.addActionListener(e -> addNewLanguage());
        newTranslation.addActionListener(e -> System.out.println("new translation pressed"));

        saveButton.addActionListener(e -> {
            addTranslationToLibraries();
        });

        addLanguageToDropDown("suomi");
        addLanguageToDropDown("english");
        addLanguageToDropDown("svenska");

        languageCountLabe.setText(String.format("amount of languages: $d", languageCount));
    }

    private void addNewLanguage() {
        String languageCode = languageCodeTextField.getText();
        String languageName = languageNameTextField.getText();

        String jsonBody = "{\n" +
                "  \" " + languageCode + "\":\"" + languageName + "\"\n" +
                "}";

        String url = "https://csv-android-app-f0353-default-rtdb.firebaseio.com/libraries/" + libraryID + "/languages.json?auth=" + userIDToken;

        fireBaseRequests.addData(url, jsonBody);
    }

    private void addTranslationToLibraries() {
        String translationName = translationDescrpitionTextField.getText();
        String translationDescription = translationDescrpitionTextField.getText();
        String androidKey = androidKeyTextField.getText();
        String iosKey = iosKeyTextField.getText();
        String webKey = webKeyTextField.getText();
        String language = languagesDropDown.getItemAt(languagesDropDown.getSelectedIndex()).toString();
        String translation = translationNameTextField2.getText();
        System.out.println("save pressed");
        String translationID = "";

        String jsonBody = "{\n" +
                "  \"android_key\":\" " + androidKey + "\",\n" +
                "  \"ios_key\":\" " + iosKey + "\",\n" +
                "  \"web_key\":\" " + webKey + "\",\n" +
                "  \"name\":\" " + translationName + "\",\n" +
                "  \"description\":\" " + translationDescription + "\"\n" +
                "  \"translations\":{\n" +
                "  \" " + language + "\":\" " + translation + "\",\n" +
                "\t}\n" +
                "}\n";

        String url = "https://csv-android-app-f0353-default-rtdb.firebaseio.com/libraries/" + libraryID + "/texts/" + translationID + ".json?auth=" + userIDToken;

        fireBaseRequests.addData(url, jsonBody);
    }

    private static void makeJMenu(JFrame frame) {
        JMenu jMenu = new JMenu("Options");
        JMenuBar jMenuBar = new JMenuBar();

        JMenuItem importFile = new JMenuItem("Import");
        JMenuItem profile = new JMenuItem("Profile");
        JMenuItem addNewFile = new JMenuItem("Add new file");

        importFile.addActionListener(e -> System.out.println("import pressed"));
        profile.addActionListener(e -> System.out.println("profile pressed"));
        addNewFile.addActionListener(e -> System.out.println("add new text pressed"));

        jMenu.add(addNewFile);
        jMenu.add(profile);
        jMenu.add(importFile);
        jMenuBar.add(jMenu);
        frame.setJMenuBar(jMenuBar);
    }

    public void addLanguageToDropDown(String language) {
        languagesDropDown.addItem(language);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Main view");
        frame.setContentPane(new MainView().mainPanel);
        makeJMenu(frame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }
}