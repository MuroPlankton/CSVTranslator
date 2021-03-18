package CSVTranslator;

import CSVTranslator.auth.AuthHelper;
import CSVTranslator.mainView.MainView;

import javax.swing.*;
import java.awt.*;

public class CSVTranslatorMain {

    private static LogInAndSignIn logInAndSignIn;

    public static void main(String[] args) {
        AuthHelper authHelper = AuthHelper.getInstance();
        if (authHelper.isUserAlreadyLoggedIn()) {
            MainView mainView = new MainView();
            SwingUtilities.invokeLater(mainView.runUI());
        } else {
            logInAndSignIn = new LogInAndSignIn();
            SwingUtilities.invokeLater(logInAndSignIn.runUI());
        }

        authHelper.setOnLoggedInListener(CSVTranslatorMain::disposeOldPanelAndStartMainView);

        authHelper.setOnSignedInListener(CSVTranslatorMain::disposeOldPanelAndStartMainView);
    }

    public static Component getLogInAndSignInPanel() {
        if(logInAndSignIn != null) {
            return logInAndSignIn.mainPanel;
        } else {
            System.out.println("CSVTranslatorMain: Panel instance is null");
            return null;
        }
    }

    private static void disposeOldPanelAndStartMainView() {
        logInAndSignIn.dispose();

        MainView mainView = new MainView();
        SwingUtilities.invokeLater(mainView.runUI());
    }
}
