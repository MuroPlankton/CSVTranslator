/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 *
 * @author s1800870
 */
public class CSVTranslator {

    /**
     * @param args the command line arguments
     */
    static FileWriter writer;

    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        beginWriting();
        int i = 0;

        while (i < 5) {
            writeOneRow();
            i++;
        }
        stopWriting();
    }

    public static void beginWriting() {
        File file = new File("strings.xml");

        try {
            //If the true is added here, the writer doesn't overwrite the existing text
            writer = new FileWriter(file, true);
            System.out.println("Tiedostoon on kirjoitettu");
        } catch (IOException e) {
            System.out.println("Virhe");
        }
    }

    public static void writeOneRow() throws IOException {
        writer.write("hei" + System.lineSeparator());
    }

    public static void stopWriting() {
        try {
            writer.close();
            System.out.println("Lopetus");
        } catch (IOException e) {
            System.out.println("Virhe");
        }
    }
}
