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
    private Map<Pair<Integer, Integer>, CsvWriter> writerMap = new HashMap<>();

    private final int ANDROID_INDEX = 0;
    private final int IOS_INDEX = 1;
    private final int WEB_INDEX = 2;

    private String fileName;

    public CsvHandler() {

    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void handleTranslateData(String line) {
        List<String> cellList = Arrays.asList(line.split(","));
        System.out.println(cellList + String.format(" has %s values", cellList.size()));

        if (linesHandled == 0) {
            for (int langIndex = 3; langIndex < cellList.size(); langIndex++) {
                //in this for loop we go trough every language
                String lang = cellList.get(langIndex);

                writerMap.put(new Pair<>(ANDROID_INDEX, langIndex), new CsvWriter("android", lang));
                writerMap.put(new Pair<>(IOS_INDEX, langIndex), new CsvWriter("ios", lang));
                writerMap.put(new Pair<>(WEB_INDEX, langIndex), new CsvWriter("web", lang));

            }
        } else if (!cellList.isEmpty()) {
            for (Pair<Integer, Integer> pair : writerMap.keySet()) {
                int osIndex = pair.getKey();
                int langIndex = pair.getValue();
                String translation = null;
                String osKey = null;
                if (cellList.size() > osIndex) {
                    osKey = cellList.get(osIndex);
                }
                if (cellList.size() > langIndex) {
                    translation = cellList.get(langIndex);
                }


                CsvWriter writer = writerMap.get(pair);
                if (writer != null && isNotEmpty(osKey) && isNotEmpty(translation)) {
                    switch (pair.getKey()) {
                        case ANDROID_INDEX:
                            writer.writeOneRow(String.format("\t<string name=\"%s\">%s</string>", osKey, translation));
                            break;
                        case IOS_INDEX:
                            writer.writeOneRow(String.format("\"%s\" = \"%s\";", osKey, translation));
                            break;
                        case WEB_INDEX:
                            writer.writeOneRow("WEB STUFF");
                    }
                }
            }
        }
        linesHandled++;
    }

    private boolean isNotEmpty(String text) {
        return text != null && text.length() > 0;
    }

<<<<<<< HEAD
    public void csvReader(String filePath) throws IOException {
=======
    interface CsvLineHandlerInterface {
        void handleCsvLine(String line);
    }

    public void readCsvAndCreateTranslateFiles() {
        readCSV(line -> {
            handleTranslateData(line);
        });
    }

    public void readCSV(CsvLineHandlerInterface lineHandler){
>>>>>>> 0138db3a47bb525d10f7a43e5f2fce17487e61b6
        BufferedReader br = null;
        String line = "";

        try {
            br = new BufferedReader(new FileReader(filePath));
            while ((line = br.readLine()) != null) {
                lineHandler.handleCsvLine(line);
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


    public void matchSentence(String sentence1) {

        readCSV(line -> {
            // TODO get langs from this CSV line

            // TODO for loop languages, get sentence2 from each langage

            String sentence2 = "Koirat ovat parempia kuin kissat";

            List<String> wordList = Arrays.asList(sentence1.split(" "));
            List<String> wordList2 = Arrays.asList(sentence2.split(" "));

            System.out.println(wordList);
            System.out.println(wordList2);
            System.out.println();
            LevenshteinAlgorithm algorithm = new LevenshteinAlgorithm();

            for (int i = 0; i < wordList.size(); i++) {
                for (int j = 0; j < wordList2.size(); j++) {

                    System.out.println(String.format("Word 1: %s, Word 2: %s", wordList.get(i), wordList2.get(j)));
                    algorithm.similarity(wordList.get(i), wordList2.get (j));
                    System.out.println();

                }

            }

        });
    }

}
