package CSVTranslator;

import CSVTranslator.auth.AuthHelper;
import javax.swing.*;
import java.awt.*;

public class CSVTranslatorMain {

    private static final LogInAndSignIn logInAndSignIn = new LogInAndSignIn();

    public static void main(String[] args) {

        SwingUtilities.invokeLater(logInAndSignIn.runUI());

        AuthHelper authHelper = AuthHelper.getInstance();

        authHelper.setOnLoggedInListener(CSVTranslatorMain::disposeOldPanelAndStartMainView);

        authHelper.setOnSignedInListener(CSVTranslatorMain::disposeOldPanelAndStartMainView);
    }

    public static Component getLogInAndSignInPanel(){
        return logInAndSignIn.mainPanel;
    }

    private static void disposeOldPanelAndStartMainView() {
        logInAndSignIn.dispose();

        MainView mainView = new MainView();
        SwingUtilities.invokeLater(mainView.runUI());
    }
}
