package CSVTranslator;

import CSVTranslator.auth.AuthHelper;

import javax.swing.*;

public class CSVTranslatorMain {

    public static void main(String[] args) {
        LogInAndSignIn logInAndSignIn = new LogInAndSignIn();
        SwingUtilities.invokeLater(logInAndSignIn.runUI());

        AuthHelper authHelper = AuthHelper.getInstance();

        authHelper.setOnSignedInListener(() -> {
            logInAndSignIn.dispose();

            startMainView();
        });
    }

    private static void startMainView() {
        MainView mainView = new MainView();
        SwingUtilities.invokeLater(mainView.runUI());
    }
}
