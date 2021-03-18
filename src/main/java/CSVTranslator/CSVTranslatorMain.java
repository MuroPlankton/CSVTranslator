package CSVTranslator;

import CSVTranslator.auth.AuthHelper;
import CSVTranslator.mainView.MainView;

import javax.swing.*;
import java.awt.*;

public class CSVTranslatorMain {

    private static LogInAndSignIn logInAndSignIn;
    private static MainView mainView;

    public static void main(String[] args) {
        appStart();
    }

    public static void appStart() {
        AuthHelper authHelper = AuthHelper.getInstance();
        if (authHelper.isUserAlreadyLoggedIn()) {
            startMainView();
        } else {
            logInAndSignIn = new LogInAndSignIn();
            SwingUtilities.invokeLater(logInAndSignIn.runUI());
        }

        authHelper.setOnLoggedInListener(() -> {
            logInAndSignIn.dispose();

            startMainView();
        });

        authHelper.setOnSignedInListener(() -> {
            logInAndSignIn.dispose();

            startMainView();
        });
    }

    public static Component getLogInAndSignInPanel() {
        if (logInAndSignIn != null) {
            return logInAndSignIn.mainPanel;
        } else {
            System.out.println("getLogInAndSignInPanel: Panel instance is null");
            return null;
        }
    }

    private static void startMainView() {
        mainView = new MainView();
        SwingUtilities.invokeLater(mainView.runUI());
    }

    public static void disposeMainView() {
        if (mainView != null) {
            mainView.dispose();
        }
    }
}
