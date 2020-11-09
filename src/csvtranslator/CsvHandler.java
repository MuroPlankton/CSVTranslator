/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csvtranslator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;


public class CsvHandler {

    private int linesHandled = 0;
    private String lineToModify = "";
    private String modifiedLine = "";
    private int value = 0;
    public int key = 0;

    public String fileName;
    private String os;
    private String language;
    private FileWriter writer;

    public CsvHandler(String fileName, String os, String language) {
        this.fileName = fileName;
        this.os = os;
        this.language = language;
    }

    public void dataHandler(String line) throws IOException {
        List<String> lineHandler = Arrays.asList(line.split(","));
        if (linesHandled == 0) {
            key = lineHandler.indexOf("android");
            value = lineHandler.indexOf(language);
            writeOneRow((key == 0) ? "<resources>" : "");

        } else {
            String osKey = lineHandler.get(key);
            String langValue = lineHandler.get(value);
            switch (key) {
                case 0:
                    writeOneRow(String.format("<string name=\"%s\">%s</String>", osKey, langValue));
                    break;
                case 1:
                    writeOneRow(String.format("\"%s\" = \"%s\"", osKey, langValue));
            }

        }

        linesHandled++;
    }

    public void csvReader(String fileName) {
        BufferedReader br = null;
        String line = "";
        String splitter = ",";

        try {


            InputStreamReader inputReader = new InputStreamReader(getClass().getResourceAsStream(fileName));

            br = new BufferedReader(inputReader);

            while ((line = br.readLine()) != null) {

                String[] data = line.split(splitter);
                dataHandler(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void beginWriting() {
        File file = new File("strings.xml");

        try {
            //If the true is added here, the writer doesn't overwrite the existing text
            writer = new FileWriter(file, true);
            System.out.println("Tiedostoon on kirjoitettu");
        } catch (IOException e) {
            System.out.println("Virhe");
        }
    }

    public void writeOneRow(String row) throws IOException {
        writer.write(row + System.lineSeparator());
    }

    public void stopWriting() {
        try {
            writer.close();
            System.out.println("Lopetus");
        } catch (IOException e) {
            System.out.println("Virhe");
        }
    }

}
