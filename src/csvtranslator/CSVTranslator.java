package csvtranslator;

import javax.swing.*;
import java.io.IOException;

/**
 * @author s1800870
 */
public class CSVTranslator {

    private static TranslatorUI translatorUI;


    private static CsvHandler csvHandler;

    public static void main(String[] args) throws IOException {



        csvHandler = new CsvHandler(args[0], args[1], args[2]);
        csvHandler.beginWriting();
        csvHandler.csvReader(csvHandler.fileName);
        csvHandler.writeOneRow((csvHandler.key == 0) ? "</resources>" : "");
        csvHandler.stopWriting();

        translatorUI = new TranslatorUI();
        SwingUtilities.invokeLater(translatorUI.getRunUI());



    }

}
