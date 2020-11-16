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
import java.util.*;


public class CsvHandler {

    private int linesHandled = 0;
    private Map<Pair<Integer, Integer>, CsvWriter> writerMap = new HashMap<>();

    private final int ANDROID_INDEX = 0;
    private final int IOS_INDEX = 1;
    private final int WEB_INDEX = 2;

    private List<String> firstLineAsList;

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
                            writer.writeOneRow(String.format("\"%s\" : \"%s\"", osKey, translation));
                    }
                }
            }
        }
        linesHandled++;
    }

    private boolean isNotEmpty(String text) {
        return text != null && text.length() > 0;
    }

    interface CsvLineHandlerInterface {
        void handleCsvLine(String line);
    }

    public void readCsvAndCreateTranslateFiles() {
        readCSV(line -> {
            handleTranslateData(line);
        });
    }

    public void readCSV(CsvLineHandlerInterface lineHandler) {
        BufferedReader br = null;
        String line = "";

        try {
            br = new BufferedReader(new FileReader(fileName));

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

    private int matchSentenceLineNumber = 0;

    private double highestMatch = 0;
    private int highestMatchLine = 0;

    public void matchSentence(String sentence1, String language) {
        matchSentenceLineNumber = 0;
        readCSV(line -> {
            matchSentenceLineNumber += 1;

            // TODO for loop languages, get sentence2 from each language

            String sentence2 = line;
            System.out.println();
            System.out.println("line: " + line);

            List<String> wordList = Arrays.asList(sentence1.split(" "));
            List<String> wordList2 = Arrays.asList(sentence2.split(","));

            System.out.println(wordList);
            System.out.println(wordList2);

            // TODO get langs from this CSV line

            int languageIndex = firstLineAsList.indexOf(language);

            //int fiIndex = 4;
            String fiSentence = null;

            if (wordList2.size() > languageIndex) {
                fiSentence = wordList2.get(languageIndex);
                List<String> fiWordList = Arrays.asList(fiSentence.split(","));

                double rowSimilarity = 0;
                double wordComparisonCount = 0;
                LevenshteinAlgorithm algorithm = new LevenshteinAlgorithm();

                for (int i = 0; i < wordList.size(); i++) {
                    for (int j = 0; j < fiWordList.size()/*wordList2.size()*/; j++) {

                        double wordSimilarity = algorithm.similarity(wordList.get(i), fiWordList.get(j)/*wordList2.get (j)*/);
                        if (wordSimilarity > 0.25d) {
                            rowSimilarity += wordSimilarity;
                        }
                        wordComparisonCount += 1;
                    }
                }
                double match = rowSimilarity / wordComparisonCount;
                if (match > highestMatch) {
                    highestMatch = match;
                    highestMatchLine = matchSentenceLineNumber;
                }
                System.out.println(String.format("Line %d Similarity: %.4f", matchSentenceLineNumber, match));
                // todo best match:
                System.out.println(String.format("Highest match: %d", highestMatchLine));
            } else {
                System.out.println("Similarity couldn't be found!");
            }
        });
    }

    public List<String> findLanguages(String pathToFile) {
        BufferedReader firstLineReader = null;
        String firstLineText = "";

        try {
            firstLineReader = new BufferedReader(new FileReader(pathToFile));
            firstLineText = firstLineReader.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                firstLineReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        firstLineAsList = Arrays.asList(firstLineText.split(","));

        List<String> languages = new ArrayList<>();
        for (String columnOfLine : firstLineAsList) {
            if (columnOfLine.length() < 3) {
                languages.add(columnOfLine);
            }
        }

        return languages;
    }

    public String getBestMatch() {
        return String.format("Highest match: %d", highestMatchLine);
    }
}
