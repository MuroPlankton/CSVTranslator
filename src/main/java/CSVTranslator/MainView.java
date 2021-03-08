package CSVTranslator;

import CSVTranslator.auth.AuthHelper;
import CSVTranslator.util.Pair;
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
    private JList<String> libraryAdapter;
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

//        languageCountLabel.setText(String.format("amount of languages: $d", languageCount));
        if (getAllLibraries() != null) {
            addAllLibrariesToList();
        }

        libraryList.addListSelectionListener(e -> {
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

        //todo this loop is extremely slow for some reason. This needs to be fixed. Might be because of getAllLibraries. Have to figure out a better way to get all users libraries
        for (String id : getAllLibraries().keySet()) {
            if (library.equals(getAllLibraries().getString(id))) {
                System.out.println(library + ", " + id);
                libraryID = id;
                String url = "https://csv-android-app-f0353-default-rtdb.firebaseio.com/libraries/" + libraryID + ".json?auth=" + authHelper.getIDToken();
                parseLibraryData(fireBaseRequests.getData(url).getKey());
            }
        }
    }

    private void parseLibraryData(String response) {
        JSONObject allTranslationsJsonObject = new JSONObject(response);
        JSONObject singleTranslationJsonObject = allTranslationsJsonObject.getJSONObject("texts");

        DefaultListModel<String> defaultListModel = new DefaultListModel<>();
        defaultListModel.removeAllElements();

        for (String translationID : singleTranslationJsonObject.keySet()) {
            System.out.println("Translation id: " + translationID);
            String name = allTranslationsJsonObject.getJSONObject("texts").getJSONObject(translationID).getString("name");
//            String description = allTranslationsJsonObject.getJSONObject("texts").getJSONObject(translationID).getString("description");
            defaultListModel.addElement(name);
        }
        libraryAdapter.setModel(defaultListModel);
    }

    private List<Pair<String, String>> responseList = new ArrayList<>();


    private JSONObject getAllLibraries() {
        String url = "https://csv-android-app-f0353-default-rtdb.firebaseio.com/user_libraries/" + authHelper.getUserID() + ".json?auth=" + authHelper.getIDToken();
        Pair<String, Boolean> myResponse = fireBaseRequests.getData(url);

        System.out.println(myResponse.getKey().length());
//        for (int i = 0; i < myResponse.getKey().length(); i++){
//
//        }

        if (myResponse.getKey().equals("null")) {
            System.out.println("No libraries exist");
            return null;
        } else {
            return new JSONObject(myResponse.getKey());
        }
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

    private void addAllLibrariesToList() {
        DefaultListModel<String> defaultListModel = new DefaultListModel<>();
        defaultListModel.removeAllElements();

        for (String key : getAllLibraries().keySet()) {
            System.out.println("individual key: " + key);
            defaultListModel.addElement(getAllLibraries().getString(key));
        }
        libraryList.setModel(defaultListModel);
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
        String language = languagesDropDown.getItemAt(languagesDropDown.getSelectedIndex()).toString();
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
        addNewFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("pressed");
                addNewLibrary();
            }
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