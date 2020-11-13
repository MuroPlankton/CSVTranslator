/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csvtranslator;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CsvHandler {

    private int linesHandled = 0;
    private String lineToModify = "";
    private String modifiedLine = "";
    private int value = 0;
    public int key = 0;
    private Map<Pair<Integer, Integer>, CsvWriter> writerMap = new HashMap<>();

    private final int ANDROID_INDEX = 0;
    private final int IOS_INDEX = 1;

    public String fileName;

    public CsvHandler(String fileName) {
        this.fileName = fileName;
    }

    public void dataHandler(String line) {
        List<String> cellList = Arrays.asList(line.split(","));
        System.out.println(cellList + String.format(" has %s values", cellList.size()));

        if (linesHandled == 0) {
            for (int langIndex = 2; langIndex < cellList.size(); langIndex++) {
                //in this for loop we go trough every language
                String lang = cellList.get(langIndex);

                writerMap.put(new Pair<>(ANDROID_INDEX, langIndex), new CsvWriter("android", lang));
                writerMap.put(new Pair<>(IOS_INDEX, langIndex), new CsvWriter("ios", lang));
            }
        } else if (!cellList.isEmpty()) {
            for (Pair<Integer, Integer> pair : writerMap.keySet()) {
                int osIndex = pair.getKey();
                int langIndex = pair.getValue();
                String translation = null;
                String osKey = null;
                if(cellList.size() > osIndex) {
                    osKey = cellList.get(osIndex);
                }
                if(cellList.size() > langIndex) {
                    translation = cellList.get(langIndex);
                }

                CsvWriter writer = writerMap.get(pair);
                if(writer != null && isNotEmpty(osKey) && isNotEmpty(translation)) {
                    switch (pair.getKey()) {
                        case ANDROID_INDEX:
                            writer.writeOneRow(String.format("\t<string name=\"%s\">%s</string>", osKey, translation));
                            break;
                        case IOS_INDEX:
                            writer.writeOneRow(String.format("\"%s\" = \"%s\";", osKey, translation));
                    }
                }
            }
        }
        linesHandled++;
    }

    private boolean isNotEmpty(String text) {
        return text != null && text.length() > 0;
    }

    public void csvReader(String fileName) throws IOException {
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

        finishWriterWriting();
    }

    private void finishWriterWriting() {
        for (Pair<Integer, Integer> pair : writerMap.keySet()) {
            CsvWriter writer = writerMap.get(pair);
            writer.stopWriting();
        }
    }
}
