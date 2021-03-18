package CSVTranslator.mainView;

import CSVTranslator.FireBaseRequests;
import CSVTranslator.Profile;
import CSVTranslator.auth.AuthHelper;
import CSVTranslator.importexport.ExportDialog;
import CSVTranslator.importexport.ImportViewClass;
import CSVTranslator.util.Pair;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.MediaType;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;
import java.util.*;

public class MainView {
    protected FireBaseRequests fireBaseRequests = new FireBaseRequests();
    protected AuthHelper authHelper = AuthHelper.getInstance();

    protected JFrame frame;
    protected JPanel mainPanel;
    protected JPanel mainView;
    protected JPanel secondPanel;
    protected JPanel editTranslationsPane;
    protected JLabel librariesLabel;
    protected JPanel libraryActionsPane;
    protected JSplitPane splitPane;

    protected JList<String> libraryList;
    protected JList<String> libraryContentJList;
    protected JLabel libraryNameLabel;
    protected JButton newTranslation;
    protected JTextField languageNameTextField;
    protected JTextField languageCodeTextField;
    protected JButton addButton;
    protected JTextField translationNameTextField;
    protected JTextField translationDescriptionTextField;
    protected JTextField androidKeyTextField;
    protected JTextField iosKeyTextField;
    protected JTextField webAdminTextField;
    //    protected JTextField translationTextField;
    protected JTextArea translationTextArea;

    protected JButton saveButton;
    protected JLabel languageCountLabel;
    protected JLabel languageCodeLabel;
    protected JLabel LanguageNameLabel;
    protected JLabel translationNameLabel;
    protected JLabel translationDescriptionLabel;
    protected JLabel iosKeyLabel;
    protected JLabel webAdminLabel;
    protected JLabel languageSpinnerLabel;
    protected JLabel translationsLabel;
    protected JLabel androidKeyLabel;
    protected JComboBox<String> languagesDropDown;
    protected JTextField libraryNameTextField;
    protected JButton exportButton;
    protected JTextField webMainTextField;
    protected JTextField webWidgetTextField;
    protected JLabel webMainLabel;
    protected JLabel webWidgetLabel;

    protected int languageCount;
    protected String libraryID = "";
    protected String libraryName = "";
    protected String translationID = "";

    private final List<Pair<String, String>> responseList = new ArrayList<>();
    private final List<Pair<String, String>> translationIDList = new ArrayList<>();
    private final Map<String, String> languageMap = new HashMap<>();

    private final Runnable runUI = this::createUI;


    public final Runnable runUI() {
        return runUI;
    }

    private void saveLibraryName() {
        String libraryName = libraryNameTextField.getText();

        String userLibraryUrl = "https://csv-android-app-f0353-default-rtdb.firebaseio.com/user_libraries/" + authHelper.getUserID() + "/.json?auth=" + authHelper.getIDToken();
        String userLibraryJsonBody = "{\n" +
                "  \"" + libraryID + "\":\"" + libraryName + "\"\n" +
                "}";

        fireBaseRequests.patchData(userLibraryUrl, userLibraryJsonBody, MediaType.parse("application/json"));

        String librariesUrl = "https://csv-android-app-f0353-default-rtdb.firebaseio.com/libraries/" + libraryID + "/.json?auth=" + authHelper.getIDToken();

        String librariesJsonBody = "{\n" +
                "  \"library_name\":\"" + libraryName + "\"\n" +
                "}";

        fireBaseRequests.patchData(librariesUrl, librariesJsonBody, MediaType.parse("application/json"));
    }

    public void createUI() {
        frame = new JFrame("Main view");
        frame.setContentPane(mainPanel);
        makeJMenu();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Object object = e.getSource();
            if (object == languagesDropDown && (!libraryID.equals("") && !translationID.equals(""))) {
                getTranslationForLanguage();
            }
        }
    };

    public MainView() {
        languageCodeTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if ((languageCodeTextField.getText() + e.getKeyChar()).length() > 2) {
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        ActionListener buttonListener = actionEvent -> {
            Object obj = actionEvent.getSource();
            if (obj == addButton) {
                addNewLanguage();
            } else if (obj == saveButton) {
                saveTranslation();
            } else if (obj == exportButton) {
                ExportDialog exportDialog = new ExportDialog(libraryID, mainPanel);
                SwingUtilities.invokeLater(exportDialog.runUI());
            } else if (obj == newTranslation) {
                addNewTranslation();
            }
        };

        addButton.addActionListener(buttonListener);
        saveButton.addActionListener(buttonListener);
        exportButton.addActionListener(buttonListener);
        newTranslation.addActionListener(buttonListener);
        languagesDropDown.addActionListener(actionListener);

        loadAllLibraries();

        ListSelectionListener listSelectionListener = actionEvent -> {
            System.out.println(actionEvent.getValueIsAdjusting());
            if (actionEvent.getValueIsAdjusting()) {
                Object obj = actionEvent.getSource();
                if (obj == libraryList) {
                    clearTranslationTextFields();
                    libraryContentJList.removeAll();
                    System.out.println(libraryList.getSelectedValue());
                    libraryName = libraryList.getSelectedValue();
                    loadSingleLibraryContent(libraryName);
                } else if (obj == libraryContentJList) {
                    clearTranslationTextFields();
                    getTranslationContent(libraryContentJList.getSelectedValue());
                }
            }
        };

        libraryList.addListSelectionListener(listSelectionListener);
        libraryContentJList.addListSelectionListener(listSelectionListener);

        libraryNameTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                saveLibraryName();
            }
        });

    }

    private void addNewTranslation() {
        System.out.println(translationID);
        translationID = "";
        libraryContentJList.clearSelection();
        clearTranslationTextFields();
    }

    private void getTranslationContent(String translationName) {

        for (Pair<String, String> translationValue : translationIDList) {
            if (translationValue.getValue().equals(translationName)) {
                translationID = translationValue.getKey();
            }
        }
        String url = "https://csv-android-app-f0353-default-rtdb.firebaseio.com/libraries/" + libraryID + "/texts/" + translationID + ".json?auth=" + authHelper.getIDToken();
        setTranslationContent(fireBaseRequests.getData(url).getKey());
    }

    private void setTranslationContent(String response) {
        JsonObject responseObject = JsonParser.parseString(response).getAsJsonObject();
        System.out.println("Response: " + responseObject);
        System.out.println("Translation ID: " + translationID);
        if (responseObject != null) {
            String androidKey = responseObject.get("android_key").getAsString();
            String iosKey = responseObject.get("ios_key").getAsString();
            String webMainKey = responseObject.get("web_key").getAsString();
            String webAdminKey = responseObject.get("web_admin_key").getAsString();
            String webWidgetKey = responseObject.get("web_admin_key").getAsString();
            String name = responseObject.get("name").getAsString();
            String description = responseObject.get("description").getAsString();

            System.out.println(androidKey + ", " + iosKey + ", " + webMainKey + ", " + name + ", " + description);

            translationNameTextField.setText(name);
            translationDescriptionTextField.setText(description);
            androidKeyTextField.setText(androidKey);
            iosKeyTextField.setText(iosKey);
            webAdminTextField.setText(webAdminKey);
            webMainTextField.setText(webMainKey);
            webWidgetTextField.setText(webWidgetKey);

            JsonObject translationsObject = responseObject.getAsJsonObject("translations");
            System.out.println("All translations :" + translationsObject);

            getTranslationForLanguage();
        }
    }

    private void getTranslationForLanguage() {
        if (languagesDropDown.getSelectedItem() != null) {
            String url = "https://csv-android-app-f0353-default-rtdb.firebaseio.com/libraries/" + libraryID + "/texts/" + translationID + "/translations.json?auth=" + authHelper.getIDToken();
            String response = fireBaseRequests.getData(url).getKey();

            JsonObject responseObject = JsonParser.parseString(response).getAsJsonObject();
            System.out.println("Response object: " + responseObject);

            String languageKey = "";

            for (String key : languageMap.keySet()) {
                if (languagesDropDown.getSelectedItem().equals(languageMap.get(key))) {
                    languageKey = key;
                }
            }

            if (responseObject.get(languageKey) != null) {
                String translation = responseObject.get(languageKey).getAsString();
                translationTextArea.setText(translation);
                System.out.println(translation);
            } else {
                translationTextArea.setText("");
                System.out.println("No translation for this language");
            }
        }
    }

    private void loadSingleLibraryContent(String library) {
        libraryNameTextField.setText(libraryName);

        for (Pair<String, String> responseValue : responseList) {
            if (responseValue.getValue().equals(library)) {
                libraryID = responseValue.getKey();
            }
        }
        String url = "https://csv-android-app-f0353-default-rtdb.firebaseio.com/libraries/" + libraryID + ".json?auth=" + authHelper.getIDToken();
        parseLibraryData(fireBaseRequests.getData(url).getKey());
    }

    private final DefaultListModel<String> libraryContentJListListModel = new DefaultListModel<>();

    private void parseLibraryData(String response) {
        if (response != null) {

            libraryContentJListListModel.removeAllElements();
            translationIDList.clear();

            JsonObject responseObject = JsonParser.parseString(response).getAsJsonObject();
            if (responseObject.has("texts")) {
                JsonObject singleTranslationObject = responseObject.getAsJsonObject("texts");

                for (String translationID : singleTranslationObject.keySet()) {
                    System.out.println("Translation id: " + translationID);

                    String translationName = responseObject.get("texts")
                            .getAsJsonObject().get(translationID)
                            .getAsJsonObject().get("name")
                            .getAsString();

                    System.out.println(translationName);
                    translationIDList.add(new Pair<>(translationID, translationName));

                    libraryContentJListListModel.addElement(translationName);
                }

                System.out.println(libraryContentJListListModel);
            } else {
                System.out.println("no texts found");
            }
            JsonObject languages = responseObject.getAsJsonObject("languages");
            System.out.println("Response: " + responseObject);
            System.out.println("Languages: " + languages);

            languagesDropDown.removeAllItems();
            if (languages != null) {
                for (String languageKey : languages.keySet()) {
                    languageMap.put(languageKey, languages.get(languageKey).getAsString());
                    languagesDropDown.addItem(languages.get(languageKey).getAsString());
                }
            }
            libraryContentJList.setModel(libraryContentJListListModel);
        }
    }


    private void loadAllLibraries() {
        String url = "https://csv-android-app-f0353-default-rtdb.firebaseio.com/user_libraries/" + authHelper.getUserID() + ".json?auth=" + authHelper.getIDToken();
        Pair<String, Boolean> myResponse = fireBaseRequests.getData(url);

        if (myResponse != null) {
            JsonObject responseObject = JsonParser.parseString(myResponse.getKey()).getAsJsonObject();

            for (String id : responseObject.keySet()) {
                System.out.println("id: " + id + ", " + responseObject.get(id).getAsString());
                responseList.add(new Pair<>(id, responseObject.get(id).getAsString()));
            }
            addAllLibrariesToList();
        } else {
            System.out.println("No libraries exist");
        }
    }

    private void addAllLibrariesToList() {
        DefaultListModel<String> defaultListModel = new DefaultListModel<>();
        defaultListModel.removeAllElements();

        for (Pair<String, String> responseValue : responseList) {
            defaultListModel.addElement(responseValue.getValue());
        }
        libraryList.setModel(defaultListModel);
    }

    private void addNewLibrary() {
        clearTranslationTextFields();
        libraryList.clearSelection();
        libraryContentJListListModel.removeAllElements();

        System.out.println("new library");
        String userLibrariesUrl = "https://csv-android-app-f0353-default-rtdb.firebaseio.com/user_libraries/" + authHelper.getUserID() + ".json?auth=" + authHelper.getIDToken();

        String libraryID = UUID.randomUUID().toString();
        String libraryName = JOptionPane.showInputDialog(frame, "Enter a name for the created library");

        if (libraryName != null) {
            String userLibrariesJsonBody = "{\n" +
                    "  \"" + libraryID + "\":\"" + libraryName + "\"\n" +
                    "}";

            fireBaseRequests.patchData(userLibrariesUrl, userLibrariesJsonBody, MediaType.parse("application/json"));

            String librariesUrl = "https://csv-android-app-f0353-default-rtdb.firebaseio.com/libraries/" + libraryID + ".json?auth=" + authHelper.getIDToken();

            String librariesJsonBody = "{\n" +
                    "  \"library_name\":\"" + libraryName + "\",\n" +
                    "  \"users\":{\n" +
                    "    \"" + authHelper.getUserID() + "\":\"" + authHelper.getDisplayName() + "\"\n" +
                    "  }\n" +
                    "}";

            fireBaseRequests.patchData(librariesUrl, librariesJsonBody, MediaType.parse("application/json"));

            responseList.add(new Pair<>(libraryID, libraryName));
            addAllLibrariesToList();
        }
    }

    private void addNewLanguage() {
        String languageCode = languageCodeTextField.getText();
        String languageName = languageNameTextField.getText();

        String jsonBody = "{\n" +
                "  \"" + languageCode + "\":\"" + languageName + "\"\n" +
                "}";

        String url = "https://csv-android-app-f0353-default-rtdb.firebaseio.com/libraries/" + libraryID + "/languages.json?auth=" + authHelper.getIDToken();

        fireBaseRequests.patchData(url, jsonBody, MediaType.parse("application/json"));

        languageCodeTextField.setText("");
        languageNameTextField.setText("");

        languageMap.put(languageCode, languageName);
        languagesDropDown.addItem(languageName);
    }

    private void saveTranslation() {
        String translationName = translationNameTextField.getText();
        String translationDescription = translationDescriptionTextField.getText();
        String androidKey = androidKeyTextField.getText();
        String iosKey = iosKeyTextField.getText();
        String webAdminKey = webAdminTextField.getText();
        String webMainKey = webMainTextField.getText();
        String webWigdetKey = webWidgetTextField.getText();
        String language = languagesDropDown.getItemAt(languagesDropDown.getSelectedIndex());
        String translation = translationTextArea.getText();
        System.out.println("save pressed");
        if (translationID.equals("")) {
            translationID = UUID.randomUUID().toString();
        }
        System.out.println(libraryID);
        String languageKey = "";

        for (String key : languageMap.keySet()) {
            if (languageMap.get(key).equals(language)) {
                languageKey = key;
                System.out.println(key);
            }
        }
        String jsonBody = "{\n" +
                "  \"android_key\":\"" + androidKey + "\",\n" +
                "  \"ios_key\":\"" + iosKey + "\",\n" +
                "  \"web_key\":\"" + webMainKey + "\",\n" +
                "  \"web_admin_key\":\"" + webAdminKey + "\",\n" +
                "  \"web_widget_key\":\"" + webWigdetKey + "\",\n" +
                "  \"name\":\"" + translationName + "\",\n" +
                "  \"description\":\"" + translationDescription + "\"\n" +
                "}\n";

        String url = "https://csv-android-app-f0353-default-rtdb.firebaseio.com/libraries/" + libraryID + "/texts/" + translationID + ".json?auth=" + authHelper.getIDToken();

        System.out.println(jsonBody);
        System.out.println(url);
        fireBaseRequests.patchData(url, jsonBody, MediaType.parse("application/json"));

        addTranslationToALanguage(languageKey, translation);

        translationIDList.add(new Pair<>(translationID, translationName));
        translationID = "";

        if (!libraryContentJListListModel.contains(translationName)) {
            libraryContentJListListModel.addElement(translationName);
            libraryContentJList.setModel(libraryContentJListListModel);
        }
        libraryContentJList.clearSelection();
        clearTranslationTextFields();
    }

    private void addTranslationToALanguage(String languageCode, String translation) {
        //todo change language to language code

        String jsonBody2 = "{\n" +
                "  \"" + languageCode + "\":\"" + translation + "\"\n" +
                "}";

        String url2 = "https://csv-android-app-f0353-default-rtdb.firebaseio.com/libraries/" + libraryID + "/texts/" + translationID + "/translations.json?auth=" + authHelper.getIDToken();

        fireBaseRequests.patchData(url2, jsonBody2, MediaType.parse("application/json"));
    }

    private void makeJMenu() {
        JMenu jMenu = new JMenu("Options");
        JMenuBar jMenuBar = new JMenuBar();
        JMenuItem importFileItem = new JMenuItem("Import");
        JMenuItem profileItem = new JMenuItem("Profile");
        JMenuItem newLibraryItem = new JMenuItem("Add new library");

        ActionListener buttonListener = actionEvent -> {
            Object obj = actionEvent.getSource();

            if (obj == importFileItem) {
                ImportViewClass importViewClass = new ImportViewClass(mainPanel);
                SwingUtilities.invokeLater(importViewClass.runUI());
            } else if (obj == profileItem) {
                System.out.println("profile pressed");
                Profile profile = new Profile();
                SwingUtilities.invokeLater(profile.runUI());
            } else if (obj == newLibraryItem) {
                addNewLibrary();
            }
        };

        importFileItem.addActionListener(buttonListener);
        profileItem.addActionListener(buttonListener);
        newLibraryItem.addActionListener(buttonListener);

        jMenu.add(newLibraryItem);
        jMenu.add(profileItem);
        jMenu.add(importFileItem);
        jMenuBar.add(jMenu);
        frame.setJMenuBar(jMenuBar);
    }

    private void clearTranslationTextFields() {
        translationNameTextField.setText("");
        translationDescriptionTextField.setText("");
        androidKeyTextField.setText("");
        iosKeyTextField.setText("");
        webAdminTextField.setText("");
        webWidgetTextField.setText("");
        webMainTextField.setText("");
        translationTextArea.setText("");
    }

    public void dispose() { frame.dispose(); }

//    public static void main(String[] args) {
//        JFrame frame = new JFrame("Main view");
//        frame.setContentPane(new MainView().mainPanel);
//        makeJMenu(frame);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setResizable(false);
//        frame.setVisible(true);
//    }
}