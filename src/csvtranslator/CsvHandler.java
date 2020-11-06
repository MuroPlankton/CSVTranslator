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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author s1800885
 */
public class CsvHandler {

    private int linesHandled = 0;
    private String lineToModify = "";
    private String modifiedLine = "";
    private List<String> lineHandler;
    private int key = 0, value = 0;

    public String fileName;
    private String os;
    private String language;

    public CsvHandler(String fileName, String os, String language) {
        this.fileName = fileName;
        this.os = os;
        this.language = language;
    }

    public void dataHandler(String line) {
        lineHandler = Arrays.asList(line.split(","));
        if (linesHandled == 0) {
            key = lineHandler.indexOf(os);
            value = lineHandler.indexOf(language);
        } else {
            System.out.println("<string name=\"" + lineHandler.get(key) + "\">" + lineHandler.get(value) + "</String>");
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

}
