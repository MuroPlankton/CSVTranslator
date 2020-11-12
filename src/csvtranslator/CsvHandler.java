/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csvtranslator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class CsvHandler {

    private int linesHandled = 0;
    private String lineToModify = "";
    private String modifiedLine = "";
    private int value = 0;
    public int key = 0;

    public String fileName;
    public String directory;
    private String os;
    private String language;
    private CsvWriter csvWriter;

    public CsvHandler(String fileName, String os, String language) {
        this.fileName = fileName;
        this.os = os;
        this.language = language;
    }

    public void dataHandler(String line) throws IOException {
        csvWriter = new CsvWriter(directory,fileName);

        System.out.println("the received line: " + line);
        List<String> cellList = Arrays.asList(line.split(","));
        System.out.println("the list from that line: " + cellList + " and the amount of items it has: " + cellList.size());

        if (linesHandled == 0) {
            key = cellList.indexOf(os);
            if(key < 0){
                key = 0;
            }
            value = cellList.indexOf(language);
            csvWriter.writeOneRow((key == 0) ? "<resources>" : "");

        } else if (!cellList.isEmpty()) {
            String osKey = null;
            String langValue = null;
            if (cellList.size() > key) {
                osKey = cellList.get(key);
            }
            if(cellList.size() > value) {
                langValue = cellList.get(value);
            }
            if(isNotEmpty(osKey) && isNotEmpty(langValue)) {
                switch (key) {
                    case 0:
                        csvWriter.writeOneRow(String.format("\t<string name=\"%s\">%s</string>", osKey, langValue));
                        break;
                    case 1:
                        csvWriter.writeOneRow(String.format("\"%s\" = \"%s\";", osKey, langValue));
                }
            }
        }
        linesHandled++;
    }

    private boolean isNotEmpty(String text) {
        return text != null && text.length() > 0;
    }

    public void csvReader(String fileName) {
        BufferedReader br = null;
        String line = "";

        try {
            br = new BufferedReader(new FileReader(fileName));

            while ((line = br.readLine()) != null) {

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





}
