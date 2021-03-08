package CSVTranslator;

import CSVTranslator.auth.AuthHelper;
import CSVTranslator.util.Pair;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.MediaType;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainView {
    FireBaseRequests fireBaseRequests = new FireBaseRequests();
    AuthHelper authHelper = AuthHelper.getInstance();

    private JFrame frame;
    private JPanel mainPanel;
    private JPanel mainView;
    private JPanel secondPanel;
    private JPanel editTranslationsPane;
    private JLabel librariesLabel;
    private JPanel libraryActionsPane;
    private JSplitPane splitPane;

    private JList<String> libraryList;
    private JList<String> libraryContentJList;
    private JLabel libraryNameLabel;
    private JButton newTranslation;
    private JTextField languageNameTextField;
    private JTextField languageCodeTextField;
    private JButton addButton;
    private JTextField translationNameTextField;
    private JTextField translationDescriptionTextField;
    private JTextField androidKeyTextField;
    private JTextField iosKeyTextField;
    private JTextField webKeyTextField;
    private JTextField translationNameTextField2;
    private JButton saveButton;
    private JLabel languageCountLabel;
    private JLabel languageCodeLabel;
    private JLabel LanguageNameLabel;
    private JLabel translationNameLabel;
    private JLabel translationDescriptionLabel;
    private JLabel iosKeyLabel;
    private JLabel webKeyLabel;
    private JLabel languageSpinnerLabel;
    private JLabel translationsLabel;
    private JLabel androidKeyLabel;
    private JComboBox<String> languagesDropDown;
    private JTextField libraryNameTextField;
    private JButton exportButton;

    private int languageCount;
    private String libraryID = "";
    private String libraryName = "";

    private List<Pair<String, String>> responseList = new ArrayList<>();

    private final Runnable runUI = this::createUI;

    public final Runnable runUI() {
        return runUI;
    }

    private void saveLibrary() {
//        String url = "https://csv-android-app-f0353-default-rtdb.firebaseio.com/user_libraries/" + authHelper.getUserID() + "/" + libraryID + ".json?auth=" + authHelper.getIDToken();
//        String libraryName = libraryNameTextField.getText();
//
//        String jsonBody = "{\n" +
//                "  \"" + libraryID + "\":\"" + libraryName + "\"\n" +
//                "}";
//
//        fireBaseRequests.patchData(url, jsonBody, MediaType.parse("application/json"));
    }

    public void createUI() {
        frame = new JFrame("Main view");
        frame.setContentPane(new MainView().mainPanel);
        makeJMenu();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public MainView() {
        addButton.addActionListener(e -> addNewLanguage());
//        newTranslation.addActionListener(e -> addNewTranslation());

        saveButton.addActionListener(e -> {
            addTranslationToLibraries();
        });

        addLanguageToDropDown("suomi");
        addLanguageToDropDown("english");
        addLanguageToDropDown("svenska");

        getAllLibraries();

        libraryList.addListSelectionListener(e -> {
            System.out.println(libraryList.getSelectedValue());
            libraryName = libraryList.getSelectedValue();
            loadSingleLibraryContent(libraryName);
        });

        exportButton.addActionListener(e -> {
            ExportDialog exportDialog = new ExportDialog(libraryID, mainPanel);
            SwingUtilities.invokeLater(exportDialog.runUI());
        });
    }

    private void loadSingleLibraryContent(String library) {
        libraryNameTextField.setText(libraryName);

        for (Pair<String, String> stringStringPair : responseList) {
            if (stringStringPair.getValue().equals(library)) {
                libraryID = stringStringPair.getKey();
                String url = "https://csv-android-app-f0353-default-rtdb.firebaseio.com/libraries/" + libraryID + ".json?auth=" + authHelper.getIDToken();
                parseLibraryData(fireBaseRequests.getData(url));
            }
        }
    }

    private void parseLibraryData(Pair<String, Boolean> response) {
        if (response != null) {

            DefaultListModel<String> defaultListModel = new DefaultListModel<>();
            defaultListModel.removeAllElements();

            System.out.println(defaultListModel);

            JsonObject responseObject = JsonParser.parseString(response.getKey()).getAsJsonObject();
            if (responseObject.has("texts")) {
                JsonObject singleTranslationObject = responseObject.getAsJsonObject("texts");

                for (String translationID : singleTranslationObject.keySet()) {
                    System.out.println("Translation id: " + translationID);

                    String translationName = responseObject.get("texts")
                            .getAsJsonObject().get(translationID)
                            .getAsJsonObject().get("name")
                            .getAsString();

                    System.out.println(translationName);

                    defaultListModel.addElement(translationName);
                }
                System.out.println(defaultListModel);
            } else {
                System.out.println("no texts found");
            }
            libraryContentJList.setModel(defaultListModel);
        }
    }

    private void getAllLibraries() {
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

        for (Pair<String, String> stringStringPair : responseList) {
            defaultListModel.addElement(stringStringPair.getValue());
        }
        libraryList.setModel(defaultListModel);
    }

    private void addNewLibrary() {
        System.out.println("new library");
        String userLibrariesUrl = "https://csv-android-app-f0353-default-rtdb.firebaseio.com/user_libraries/" + authHelper.getUserID() + ".json?auth=" + authHelper.getIDToken();

        String libraryID = UUID.randomUUID().toString();
        String libraryName = JOptionPane.showInputDialog(frame, "Enter a name for the created library");

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


        //todo when the user creates a new library, that library should be shown in the libraryList by calling addAllLibrariesToList
        // , but for some reason calling it again after initial launch doesn't work properly

        addAllLibrariesToList();
    }

    private void addNewTranslation() {
//        String translationID = UUID.randomUUID().toString();
//
//        String jsonBody = "";
//
//        String url = "https://csv-android-app-f0353-default-rtdb.firebaseio.com/libraries/" + libraryID + "/texts/" + translationID + ".json?auth=" + authHelper.getIDToken();
//        fireBaseRequests.patchData(url, jsonBody, MediaType.parse("application/json"));
//        languageCountLabel.setText(String.format("amount of languages: $d", languageCount));

//        clearTranslationTextFields();
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
        String translationName = translationNameTextField.getText();
        String translationDescription = translationDescriptionTextField.getText();
        String androidKey = androidKeyTextField.getText();
        String iosKey = iosKeyTextField.getText();
        String webKey = webKeyTextField.getText();
        String language = languagesDropDown.getItemAt(languagesDropDown.getSelectedIndex());
        String translation = translationNameTextField2.getText();
        System.out.println("save pressed");
        String translationID = UUID.randomUUID().toString();

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

        System.out.println(libraryID);

        String url = "https://csv-android-app-f0353-default-rtdb.firebaseio.com/libraries/" + libraryID + "/texts/" + translationID + ".json?auth=" + authHelper.getIDToken();
        System.out.println(jsonBody);
        System.out.println(url);
        fireBaseRequests.patchData(url, jsonBody, MediaType.parse("application/json"));

        loadSingleLibraryContent(libraryName);
        clearTranslationTextFields();
    }

    private void makeJMenu() {
        JMenu jMenu = new JMenu("Options");
        JMenuBar jMenuBar = new JMenuBar();
        JMenuItem importFile = new JMenuItem("Import");
        JMenuItem profile = new JMenuItem("Profile");
        JMenuItem addNewFile = new JMenuItem("Add new library");

        importFile.addActionListener(e -> System.out.println("import pressed"));
        profile.addActionListener(e -> System.out.println("profile pressed"));

        addNewFile.addActionListener(e -> {
            System.out.println("pressed");
            addNewLibrary();
        });

        jMenu.add(addNewFile);
        jMenu.add(profile);
        jMenu.add(importFile);
        jMenuBar.add(jMenu);
        frame.setJMenuBar(jMenuBar);
    }

    public void addLanguageToDropDown(String language) {
        languagesDropDown.addItem(language);
    }

    private void clearTranslationTextFields() {
        translationNameTextField.setText("");
        translationDescriptionTextField.setText("");
        androidKeyTextField.setText("");
        iosKeyTextField.setText("");
        webKeyTextField.setText("");
        translationNameTextField2.setText("");
    }

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