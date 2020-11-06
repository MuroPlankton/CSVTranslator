package csvtranslator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * @author s1800870
 */
public class CSVTranslator {

    private static CsvHandler csvHandler;

    public static void main(String[] args) {
        // TODO code application logic here
        
        csvHandler = new CsvHandler(args[0], args[1], args[2]);
        csvHandler.beginWriting();
        
        csvHandler.csvReader(csvHandler.fileName);
        csvHandler.stopWriting();
        
    }

}
