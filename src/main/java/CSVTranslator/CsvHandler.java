package CSVTranslator;

import CSVTranslator.auth.AuthHelper;
import CSVTranslator.util.Pair;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
    private List<OutPutFileWriter> writerList = new ArrayList<>();

    private final String ANDROID = "android";
    private final String IOS = "ios";
    private final String WEB_ADMIN = "web-admin";
    private final String WEB_MAIN = "web-main";
    private final String WEB_WIDGET = "web-widget";

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
//                                //TODO: comma seems to always be null
//                            }
//                            break;
//                    }
//                }
//            }
//        }
//        linesHandled++;
//    }

    private void addWritersToList(String lang) {
        writerList.add(new OutPutFileWriter("android", lang, outputDir));
        writerList.add(new OutPutFileWriter("ios", lang, outputDir));
        writerList.add(new OutPutFileWriter("web-admin", lang, outputDir));
        writerList.add(new OutPutFileWriter("web-main", lang, outputDir));
        writerList.add(new OutPutFileWriter("web-widget", lang, outputDir));
    }

    private boolean isNotEmpty(String text) {
        return text != null && text.length() > 0;
    }

    public void exportLibrary(String libraryID, String outputFileType, String selectedLang, String outputDir) {
        writerList.clear();
        this.outputDir = outputDir;
        AuthHelper authHelper = AuthHelper.getInstance();
        FireBaseRequests fireBaseRequests = new FireBaseRequests();
        Pair<String, Boolean> libraryResponseInfo = fireBaseRequests.getData(String.format(
                "https://csv-android-app-f0353-default-rtdb.firebaseio.com/libraries/%s.json?auth=%s",
                libraryID, authHelper.getIDToken()));

        JsonObject libraryObject = null;
        if (libraryResponseInfo.getValue()) {
            libraryObject = JsonParser.parseString(libraryResponseInfo.getKey()).getAsJsonObject();

            if (outputFileType.equals(".csv")) {
                writerList.add(new OutPutFileWriter("csv", selectedLang, outputDir));
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

                writerList.get(0).writeOneRow(line, null, true);
            } else {
                if (selectedLang.equals("Every language")) {
                    JsonObject langObject = libraryObject.getAsJsonObject("languages");

                    for (String langKey : langObject.keySet()) {
                        addWritersToList(langKey);
                    }
                } else {
                    addWritersToList(selectedLang);
                }
            }

            writeLinesToFiles(libraryObject);
            stopWriters();
        }
    }

    private void writeLinesToFiles(JsonObject libraryObject) {
        JsonObject textsObject = libraryObject.getAsJsonObject("texts");

        for (String textKey : textsObject.keySet()) {
            JsonObject textObject = textsObject.getAsJsonObject(textKey);
            System.out.println(textObject.get("name").getAsString());

            for (OutPutFileWriter writer : writerList) {
                String osKey = null;

                try {
                    switch (writer.getFileType()) {
                        case ANDROID:
                            osKey = textObject.get("android_key").getAsString();
                            break;

                        case IOS:
                            osKey = textObject.get("ios_key").getAsString();
                            break;

                        case WEB_ADMIN:
                            osKey = textObject.get("web_admin_key").getAsString();
                            break;

                        case WEB_MAIN:
                            osKey = textObject.get("web_key").getAsString();
                            break;

                        case WEB_WIDGET:
                            osKey = textObject.get("web_widget_key").getAsString();
                            break;

                        case "csv":
                            osKey = "csv";
                            break;
                    }
                } catch (NullPointerException e) {
                    System.out.println("platform key not found");
                }


                JsonObject translationsObject = textObject.getAsJsonObject("translations");
                String writerLang = writer.getLang();

                String translation = null;
                try {
                    translation = (writerLang.equals("Every language")) ? "Every language" :
                            translationsObject.get(writerLang).getAsString();
                } catch (NullPointerException e) {
                    System.out.println("Translation not found");
                }


                if (writer.getFileType().equals("csv")) {
                    List<String> platformKeyList = new ArrayList<>();
                    platformKeyList.add("android_key");
                    platformKeyList.add("ios_key");
                    platformKeyList.add("web_admin_key");
                    platformKeyList.add("web_key");
                    platformKeyList.add("web_widget_key");

                    String line = textObject.get(platformKeyList.get(0)).getAsString();
                    for (int index = 1; index < platformKeyList.size(); index++) {
                        line += ",";
                        try {
                            line += String.format("%s", textObject.get(platformKeyList.get(index)).getAsString());
                        } catch (NullPointerException e) {
                            line += ",";
                        }
                    }

                    if (translation.equals("Every language")) {
                        for (String langKey : libraryObject.getAsJsonObject("languages").keySet()) {
                            line += ",";
                            try {
                                line += String.format("%s", translationsObject.get(langKey).getAsString());
                            } catch (NullPointerException e) {
                                System.out.println("Translation not found");
                            }
                        }
                    } else {
                        line += ",";
                        try {
                            line += String.format("%s", translation);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }

                    writer.writeOneRow(line, null, true);
                } else {
                    if (isNotEmpty(osKey) && isNotEmpty(translation)) {
                        switch (writer.getFileType()) {
                            case ANDROID:
                                writer.writeOneRow(String.format("\t<string name=\"%s\">%s</string>", osKey, translation), null, true);
                                break;

                            case IOS:
                                writer.writeOneRow(String.format("\"%s\" = \"%s\";", osKey, translation), null, true);
                                break;

                            case WEB_ADMIN:
                            case WEB_MAIN:
                            case WEB_WIDGET:
                                writer.writeOneRow(String.format("\"%s\" : \"%s\"", osKey, translation),
                                        (writer.isFirstLineWritten()) ? "," : null, true);
                                break;
                        }
                    }
                }
            }
        }
    }

    private void stopWriters() {
        for (OutPutFileWriter writer : writerList) {
            writer.stopWriting();
        }
    }

    private void exportLibraryToCSV(JsonObject libraryObject, String selectedLang) {
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

//    public void readCsvAndCreateTranslateFiles(String selectedLang) {
//        writerMap.clear();
//        linesHandled = 0;
//        this.selectedLang = selectedLang;
//
//        readCSV(this::handleTranslateData);
//        finishWriterWriting();
//    }

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
//        for (Pair<Integer, Integer> pair : writerMap.keySet()) {
//            OutPutFileWriter writer = writerMap.get(pair);
//            writer.stopWriting();
//        }
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
