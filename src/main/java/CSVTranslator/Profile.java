package CSVTranslator;

import CSVTranslator.auth.AuthHelper;

import javax.swing.*;
import java.awt.event.*;

public class Profile extends JDialog {

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton signOutBtn;

    private final Runnable runUI = this::createUI;

    public final Runnable runUI() {
        return runUI;
    }

    private void createUI() {
        Profile dialog = new Profile();
        dialog.pack();
        dialog.setSize(500, 500);
        dialog.setVisible(true);
    }

    public Profile() {
        setDialog();

        signOutBtn.addActionListener(actionEvent -> {
            signOut();
        });
    }

    public void signOut() {
        dispose();

        CSVTranslatorMain.disposeMainView();

        AuthHelper authHelper = AuthHelper.getInstance();
        authHelper.emptyInformationOnSignOut();

        CSVTranslatorMain.appStart();
    }

    private void setDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
