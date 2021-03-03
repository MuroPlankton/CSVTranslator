package CSVTranslator;

import CSVTranslator.auth.AuthHelper;
import okhttp3.MediaType;
import org.json.JSONObject;
import org.json.JSONTokener;


import javax.swing.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainView {
    FireBaseRequests fireBaseRequests = new FireBaseRequests();
    AuthHelper authHelper = AuthHelper.getInstance();

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
    private JTextField libraryNameTextField;

    private int languageCount;
    private final String userID = "feHvfGJ3Iwc8D565wQU7GHnH5hu2";
    private final String libraryID = "044fe665-821d-4e7e-be96-678125a40527";
    private String translationID = null;

    public MainView() {
        authHelper.logExistingUserIn("ryhanenjarno@gmail.com", "12345678");

        addButton.addActionListener(e -> addNewLanguage());
        newTranslation.addActionListener(e -> addNewTranslation());

        saveButton.addActionListener(e -> {
            addTranslationToLibraries();
        });

        addLanguageToDropDown("suomi");
        addLanguageToDropDown("english");
        addLanguageToDropDown("svenska");

        languageCountLabe.setText(String.format("amount of languages: $d", languageCount));
        loadAllLibraries();
    }

    private void loadAllLibraries() {
        String url = "https://csv-android-app-f0353-default-rtdb.firebaseio.com/user_libraries/" + userID + ".json?auth=" + authHelper.getIDToken();
        System.out.println(fireBaseRequests.getData(url));
        System.out.println("lol");

        String myResponse = fireBaseRequests.getData(url);

        JSONObject jsonObject = new JSONObject(myResponse);
        System.out.println(jsonObject);
        System.out.println(jsonObject.length());

//        System.out.println(jsonObject.names());
//        System.out.println(jsonObject.keys());
        System.out.println(jsonObject.keySet());

        List<Set<String>> keyList = new ArrayList<>();
        keyList.add(jsonObject.keySet());
        System.out.println(keyList.get(0));

        DefaultListModel<String> defaultListModel = new DefaultListModel<>();

//        jsonObject.getString(key)

        for (String key : jsonObject.keySet()) {
//            libraryList.add(jsonObject.getString(key));
//            defaultListModel.addElement(jsonObject.getString(key));
            System.out.println(jsonObject.getString(key));
        }
//        libraryList = new JList(defaultListModel);

//        libraryList.add();
    }


    private void addNewTranslation() {
        //todo we maybe dont need this button, since addTranslationToLibraries already adds a new translation
//        translationID = UUID.randomUUID().toString();
//
//        String jsonBody = ;
//
//        String url = "https://csv-android-app-f0353-default-rtdb.firebaseio.com/libraries/" + libraryID + "/texts/" + translationID + ".json?auth=" + userIDToken;
//        fireBaseRequests.addData(url, jsonBody);
    }

    private void addNewLanguage() {
        String languageCode = languageCodeTextField.getText();
        String languageName = languageNameTextField.getText();

        String jsonBody = "{\n" +
                "  \" " + languageCode + "\":\"" + languageName + "\"\n" +
                "}";

        String url = "https://csv-android-app-f0353-default-rtdb.firebaseio.com/libraries/" + libraryID + "/languages.json?auth=" + authHelper.getIDToken();

        fireBaseRequests.patchData(url, jsonBody, MediaType.parse("application/json"));
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

        String libraryName = libraryNameTextField.getText();

        String jsonBody = "{\n" +
                "  \"android_key\":\"" + androidKey + "\",\n" +
                "  \"ios_key\":\"" + iosKey + "\",\n" +
                "  \"web_key\":\"" + webKey + "\",\n" +
                "  \"name\":\"" + translationName + "\",\n" +
                "  \"description\":\"" + translationDescription + "\",\n" +
                "  \"translations\":{\n" +
                "  \"" + language + "\":\"" + translation + "\"\n" +
                "\t}\n" +
                "}\n";

        String url = "https://csv-android-app-f0353-default-rtdb.firebaseio.com/libraries/" + libraryID + "/texts/" + translationID + ".json?auth=" + authHelper.getIDToken();
        System.out.println(jsonBody);
        System.out.println(url);
        fireBaseRequests.patchData(url, jsonBody, MediaType.parse("application/json"));

        String userLibaryJsonBody = "{\n" +
                "  \"" + libraryID + "\":\"" + libraryName + "\"\n" +
                "}";

        String userLibraryUrl = "https://csv-android-app-f0353-default-rtdb.firebaseio.com/user_libraries/" + userID + ".json?auth=" + authHelper.getIDToken();

        fireBaseRequests.patchData(userLibraryUrl, userLibaryJsonBody, MediaType.parse("application/json"));
    }

    private static void makeJMenu(JFrame frame) {
        JMenu jMenu = new JMenu("Options");
        JMenuBar jMenuBar = new JMenuBar();

        JMenuItem importFile = new JMenuItem("Import");
        JMenuItem profile = new JMenuItem("Profile");
        JMenuItem addNewFile = new JMenuItem("Add new library");

        importFile.addActionListener(e -> System.out.println("import pressed"));
        profile.addActionListener(e -> System.out.println("profile pressed"));
        addNewFile.addActionListener(e -> System.out.println("add new library pressed"));

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