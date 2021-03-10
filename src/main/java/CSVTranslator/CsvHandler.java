package CSVTranslator;

import CSVTranslator.auth.AuthHelper;
import CSVTranslator.util.Pair;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
                    switch (osIndex) {
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
                                //TODO: comma seems to always be null
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

    public void exportLibrary(String libraryID, String outputFileType, String selectedLang, String outputDir) {
        AuthHelper authHelper = AuthHelper.getInstance();
        FireBaseRequests fireBaseRequests = new FireBaseRequests();
        Pair<String, Boolean> libraryResponseInfo = fireBaseRequests.getData(String.format(
                "https://csv-android-app-f0353-default-rtdb.firebaseio.com/libraries/%s.json?auth=%s",
                libraryID, authHelper.getIDToken()));

        JsonObject jsonObject = null;
        if (libraryResponseInfo.getValue()) {
            jsonObject = JsonParser.parseString(libraryResponseInfo.getKey()).getAsJsonObject();
            if (outputFileType.equals(".csv")) {
                exportLibraryToCSV(jsonObject, selectedLang, outputDir);
            }
        }
        //TODO: export logic
    }

    private void exportLibraryToCSV(JsonObject libraryObject, String selectedLang, String outputDir) {
        OutPutFileWriter csvWriter = new OutPutFileWriter("csv", selectedLang, outputDir);
        String line = "android,ios,web-admin,web-main,web-widget";
        List<String> langKeyList = new ArrayList<>();
        if (selectedLang.equals("Every language")) {
            JsonObject langObject = libraryObject.getAsJsonObject("languages");
            for (String langKey : langObject.keySet()) {
                langKeyList.add(langKey);
                line += String.format(",%s", langKey);
            }
        } else {
            line += String.format(",%s", selectedLang);
            langKeyList.add(selectedLang);
        }
        csvWriter.writeOneRow(line, null, true);

        List<String> platformKeyList = new ArrayList<>();
        platformKeyList.add("android_key");
        platformKeyList.add("ios_key");
        platformKeyList.add("web_admin_key");
        platformKeyList.add("web_key");
        platformKeyList.add("web_widget_key");

        JsonObject textsObject = libraryObject.getAsJsonObject("texts");
        for (String textKey : textsObject.keySet()) {
            JsonObject textObject = textsObject.getAsJsonObject(textKey);

            line = textObject.get(platformKeyList.get(0)).getAsString();
            for (int index = 1; index < platformKeyList.size(); index++) {
                try {
                    line += String.format(",%s", textObject.get(platformKeyList.get(index)).getAsString());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            JsonObject translationsObject = textObject.getAsJsonObject("translations");
            for (String langKey : langKeyList) {
                line += ",";
                try {
                    line += String.format("%s", translationsObject.get(langKey).getAsString());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            csvWriter.writeOneRow(line, null, true);
        }

        csvWriter.stopWriting();
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
