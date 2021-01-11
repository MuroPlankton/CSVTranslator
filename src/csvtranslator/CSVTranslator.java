package csvtranslator;

import javax.swing.*;

/**
 * @author s1800870
 */
public class CSVTranslator {

    public static void main(String[] args) {
        TranslatorUI translatorUI = new TranslatorUI();
        SwingUtilities.invokeLater(translatorUI.RunUI());
    }
}
