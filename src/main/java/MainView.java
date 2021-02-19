import javax.swing.*;
import javax.swing.text.View;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainView {
    private JPanel mainPanel;
    private JPanel secondPanel;
    private JSplitPane splitPane;
    private JPanel mainView;
    private JLabel librariesLabel;
    private JList libraryList;
    private JPanel thirdPane;
    private JLabel libraryNameLabel;
    private JList libraryAdapter;
    private JPanel libraryActionsPane;
    private JButton newTranslation;
    private JTextField languageName;
    private JTextField languageCode;
    private JButton addButton;
    private JButton importButton;
    private JButton profileButton;
    private JTextField translationNameTextField;
    private JTextField translationDescrpitionTextField;
    private JTextField androidKeyTextField;
    private JTextField iosKeyTextField;
    private JTextField webKeyTextField;
    private JSpinner languageSpinner;
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

    private int buttonTracker;

    public MainView() {

        importButton.addActionListener(e -> {
            System.out.println("import pressed");
        });

        addButton.addActionListener(e -> {
            System.out.println("add pressed");
        });

        profileButton.addActionListener(e -> {
            System.out.println("profile pressed");
        });
        newTranslation.addActionListener(e -> {
            System.out.println("new translation pressed");
        });

        saveButton.addActionListener(e -> {
            System.out.println("save pressed");
        });

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Main view");
        frame.setContentPane(new MainView().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }


}
