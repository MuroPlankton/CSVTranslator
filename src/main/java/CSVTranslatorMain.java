import auth.AuthHelper;

import javax.swing.*;

public class CSVTranslatorMain {
    public static void main(String[] args) {
        TranslatorUI translatorUI = new TranslatorUI();
        SwingUtilities.invokeLater(translatorUI.RunUI());

        AuthHelper authHelper = AuthHelper.getInstance();

    }
}
