package CSVTranslator.importexport;

import CSVTranslator.importexport.OutPutFileWriter;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CsvHandler {
    private static final String DELIMITER = "^\\s*\"?\\s*(.*?)\\s*\"?\\s*$";
    private static final Pattern CSV_PATTERN = Pattern.compile("\\s*(?:\"[^\"]*\"|(?:^|(?<=,))[^,]*)");

    private int linesHandled = 0;

    private List<String> firstLineAsList;

    private JsonObject languagesObject;
    private String fileName;

//    public void handleTranslateData(String line) {
//        List<String> cellList = splitCSVLine(line);
//
//        if (linesHandled == 0) {
//            if (selectedLang.equals(DEFAULT_SELECTED_LANG_VALUE)) {
//                for (int langIndex = 5; langIndex < cellList.size(); langIndex++) {
//                    //in this for loop we go trough every language
//                    String lang = cellList.get(langIndex);
//                    addWritersToList(langIndex, lang);
//                }
//            } else {
//                int langIndex = cellList.indexOf(selectedLang);
//                addWritersToList(langIndex, selectedLang);
//            }
//
//        } else if (!cellList.isEmpty()) {
//            for (Pair<Integer, Integer> pair : writerMap.keySet()) {
//                int osIndex = pair.getKey();
//                int langIndex = pair.getValue();
//                String translation = null;
//                String osKey = null;
//
//                if (cellList.size() > osIndex) {
//                    osKey = cellList.get(osIndex);
//                }
//
//                if (cellList.size() > langIndex) {
//                    translation = cellList.get(langIndex);
//                }
//
//                OutPutFileWriter writer = writerMap.get(pair);
//                if (writer != null && isNotEmpty(osKey) && isNotEmpty(translation)) {
//                    switch (osIndex) {
//                        case ANDROID_INDEX:
//                            writer.writeOneRow(String.format("\t<string name=\"%s\">%s</string>", osKey, translation), null, true);
//                            break;
//                        case IOS_INDEX:
//                            writer.writeOneRow(String.format("\"%s\" = \"%s\";", osKey, translation), null, true);
//                            break;
//                        case WEB_ADMIN_INDEX:
//                        case WEB_MAIN_INDEX:
//                        case WEB_WIDGET_INDEX:
//                            String comma = ",";
//                            if (!writer.isFirstLineWritten()) {
//                                comma = null;
//
//                                writer.writeOneRow(String.format("\"%s\" : \"%s\"", osKey, translation), comma, true);
//                            }
//                            break;
//                    }
//                }
//            }
//        }
//        linesHandled++;
//    }





    public void readCsvAndCreateTranslateFiles(String fileName, JsonObject languagesObject, String mainLang) {
        this.fileName = fileName;
        this.languagesObject = languagesObject;
        linesHandled = 0;
//        readCSV(this::handleTranslateData);
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
            } catch (Exception e) {
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



    private boolean isNotEmpty(String text) {
        return text != null && text.length() > 0;
    }

    private List<String> splitCSVLine(String text) {
        final List<String> cells = new ArrayList<>();
        final Matcher matcher = CSV_PATTERN.matcher(text);
        while (matcher.find()) {
            cells.add(matcher.group().replaceAll(DELIMITER, "$1"));
        }
        return cells;
    }

    interface CsvLineHandlerInterface {
        void handleCsvLine(String line);
    }
}
