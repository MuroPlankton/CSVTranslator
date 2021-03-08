package CSVTranslator;

import CSVTranslator.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CsvHandler {
    private static final String DELIMITER = "^\\s*\"?\\s*(.*?)\\s*\"?\\s*$";
    private static final Pattern CSV_PATTERN = Pattern.compile("\\s*(?:\"[^\"]*\"|(?:^|(?<=,))[^,]*)");

    private int linesHandled = 0;
    private final Map<Pair<Integer, Integer>, OutPutFileWriter> writerMap = new HashMap<>();

    private final int ANDROID_INDEX = 0;
    private final int IOS_INDEX = 1;
    private final int WEB_ADMIN_INDEX = 2;
    private final int WEB_MAIN_INDEX = 3;
    private final int WEB_WIDGET_INDEX = 4;

    private List<String> firstLineAsList;

    private String fileName;
    private String selectedLang;
    private static final String DEFAULT_SELECTED_LANG_VALUE = "(all)";

    private String outputDir;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setOutputDir(String chosenPath) {
        this.outputDir = chosenPath;
    }

    public void handleTranslateData(String line) {
        List<String> cellList = splitCSVLine(line);

        if (linesHandled == 0) {
            if (selectedLang.equals(DEFAULT_SELECTED_LANG_VALUE)) {
                for (int langIndex = 5; langIndex < cellList.size(); langIndex++) {
                    //in this for loop we go trough every language
                    String lang = cellList.get(langIndex);
                    addWritersToWriterMap(langIndex, lang);
                }
            } else {
                int langIndex = cellList.indexOf(selectedLang);
                addWritersToWriterMap(langIndex, selectedLang);
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

                OutPutFileWriter writer = writerMap.get(pair);
                if (writer != null && isNotEmpty(osKey) && isNotEmpty(translation)) {
                    switch (pair.getKey()) {
                        case ANDROID_INDEX:
                            writer.writeOneRow(String.format("\t<string name=\"%s\">%s</string>", osKey, translation), null, true);
                            break;
                        case IOS_INDEX:
                            writer.writeOneRow(String.format("\"%s\" = \"%s\";", osKey, translation), null, true);
                            break;
                        case WEB_ADMIN_INDEX:
                        case WEB_MAIN_INDEX:
                        case WEB_WIDGET_INDEX:
                            String comma = ",";
                            if (!writer.isFirstLineWritten()) {
                                comma = null;

                                writer.writeOneRow(String.format("\"%s\" : \"%s\"", osKey, translation), comma, true);
                            }
                            break;
                    }
                }
            }
        }
        linesHandled++;
    }

    private void addWritersToWriterMap(int langIndex, String lang) {
        writerMap.put(new Pair<>(ANDROID_INDEX, langIndex), new OutPutFileWriter("android", lang, outputDir));
        writerMap.put(new Pair<>(IOS_INDEX, langIndex), new OutPutFileWriter("ios", lang, outputDir));
        writerMap.put(new Pair<>(WEB_ADMIN_INDEX, langIndex), new OutPutFileWriter("web-admin", lang, outputDir));
        writerMap.put(new Pair<>(WEB_MAIN_INDEX, langIndex), new OutPutFileWriter("web-main", lang, outputDir));
        writerMap.put(new Pair<>(WEB_WIDGET_INDEX, langIndex), new OutPutFileWriter("web-widget", lang, outputDir));
    }

    private boolean isNotEmpty(String text) {
        return text != null && text.length() > 0;
    }

    public void exportLibrary(String libraryID, String outputFileType, String selectedLang) {
        //TODO: export logic
    }

    interface CsvLineHandlerInterface {
        void handleCsvLine(String line);
    }

    public void readCsvAndCreateTranslateFiles(String selectedLang) {
        writerMap.clear();
        linesHandled = 0;
        this.selectedLang = selectedLang;

        readCSV(this::handleTranslateData);
        finishWriterWriting();
    }

    public void readCSV(CsvLineHandlerInterface lineHandler) {
        BufferedReader br = null;
        String line = "";

        try {
            br = new BufferedReader(new FileReader(fileName));

            while ((line = br.readLine()) != null) {
                lineHandler.handleCsvLine(line);
            }

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

    private void finishWriterWriting() {
        for (Pair<Integer, Integer> pair : writerMap.keySet()) {
            OutPutFileWriter writer = writerMap.get(pair);
            writer.stopWriting();
        }
    }

    public List<String> findLanguages(String pathToFile) {
        BufferedReader firstLineReader = null;
        String firstLineText = "";

        try {
            firstLineReader = new BufferedReader(new FileReader(pathToFile));
            firstLineText = firstLineReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                firstLineReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        firstLineAsList = splitCSVLine(firstLineText);

        List<String> languages = new ArrayList<>();
        for (String columnOfLine : firstLineAsList) {
            if (columnOfLine.length() < 3) {
                languages.add(columnOfLine);
            }
        }

        return languages;
    }

    private List<String> splitCSVLine(String text) {
        final List<String> cells = new ArrayList<>();
        final Matcher matcher = CSV_PATTERN.matcher(text);
        while (matcher.find()) {
            cells.add(matcher.group().replaceAll(DELIMITER, "$1"));
        }
        return cells;
    }
}
