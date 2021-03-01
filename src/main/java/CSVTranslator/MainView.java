package CSVTranslator;

import okhttp3.MediaType;

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
    private JComboBox languagesDropDown;

    private int languageCount;
    private final String userID = "";
    private final String userIDToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImM0ZWFhZjkxM2VlNWY0MDY0YmE2NjUzN2M0Njk3YzY5OGE3NGYwODIiLCJ0eXAiOiJKV1QifQ.eyJuYW1lIjoiamFybm8iLCJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vY3N2LWFuZHJvaWQtYXBwLWYwMzUzIiwiYXVkIjoiY3N2LWFuZHJvaWQtYXBwLWYwMzUzIiwiYXV0aF90aW1lIjoxNjE0NjAwNTA0LCJ1c2VyX2lkIjoiZmVIdmZHSjNJd2M4RDU2NXdRVTdHSG5INWh1MiIsInN1YiI6ImZlSHZmR0ozSXdjOEQ1NjV3UVU3R0huSDVodTIiLCJpYXQiOjE2MTQ2MDA1MDQsImV4cCI6MTYxNDYwNDEwNCwiZW1haWwiOiJyeWhhbmVuamFybm9AZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJmaXJlYmFzZSI6eyJpZGVudGl0aWVzIjp7ImVtYWlsIjpbInJ5aGFuZW5qYXJub0BnbWFpbC5jb20iXX0sInNpZ25faW5fcHJvdmlkZXIiOiJwYXNzd29yZCJ9fQ.RYKe5k8Gn4PB3ZIn45RdeTtnE0rZ6tWv1Rl6Ytu23AYf0qfSyypNXsb-6h1U_oHK_EnY90VU6hEik1gv6hQ7pWq786ruMTlcIdXXOxJTNl88X5iT4pZtUzoFXSoKfy6MI6QWM2bSBzjzoNd_hGCgNNvSd7tO4F36mzEmpYJoQlaNkewL2mVKu2_Fx8uGo4Iy3qYKG8eIWocVlW-MEVKzUR9rSillvIdSwwI4KeCrthNXiN9EcaTE5Dwpb7slHuR_NaFAYuA7IQ7BXPvZruyLihTFDakUlhAxdL18ph-hX7aYvxlsVTzpebk65s81OSTfQS7x6732SRgH4OwM0WM4IA";
    private final String libraryID = "044fe665-821d-4e7e-be96-678125a40527";

    private final Runnable runUI = this::createUI;

    public Runnable runUI(){
        return runUI;
    }

    public void createUI(){
        JFrame frame = new JFrame("Main view");
        frame.setContentPane(new MainView().mainPanel);
        makeJMenu(frame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public MainView() {
        addButton.addActionListener(e -> addNewLanguage());
        newTranslation.addActionListener(e -> System.out.println("new translation pressed"));

        saveButton.addActionListener(e -> {
            addTranslationToLibraries();
        });

        addLanguageToDropDown("suomi");
        addLanguageToDropDown("english");
        addLanguageToDropDown("svenska");

        languageCountLabel.setText(String.format("amount of languages: $d", languageCount));
    }

    private void addNewLanguage() {
        String languageCode = languageCodeTextField.getText();
        String languageName = languageNameTextField.getText();

        String jsonBody = "{\n" +
                "  \" " + languageCode + "\":\"" + languageName + "\"\n" +
                "}";

        String url = "https://csv-android-app-f0353-default-rtdb.firebaseio.com/libraries/" + libraryID + "/languages.json?auth=" + userIDToken;

        fireBaseRequests.patchData(url, jsonBody, MediaType.parse("application/json"));
    }

    private void addTranslationToLibraries() {
        String translationName = translationDescriptionTextField.getText();
        String translationDescription = translationDescriptionTextField.getText();
        String androidKey = androidKeyTextField.getText();
        String iosKey = iosKeyTextField.getText();
        String webKey = webKeyTextField.getText();
        String language = languagesDropDown.getItemAt(languagesDropDown.getSelectedIndex()).toString();
        String translation = translationNameTextField2.getText();
        System.out.println("save pressed");
        String translationID = "abc";

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

        String url = "https://csv-android-app-f0353-default-rtdb.firebaseio.com/libraries/" + libraryID + "/texts/" + translationID + ".json?auth=" + userIDToken;
        System.out.println(jsonBody);
        System.out.println(url);
        fireBaseRequests.patchData(url, jsonBody, MediaType.parse("application/json"));
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
}