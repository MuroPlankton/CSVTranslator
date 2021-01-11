package csvtranslator;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * @author s1800870
 */
public class CSVTranslator {

    private static TranslatorUI translatorUI;


    private static CsvHandler csvHandler;

    public static void main(String[] args) {

        translatorUI = new TranslatorUI();
        SwingUtilities.invokeLater(translatorUI.getRunUI());
    }


}
