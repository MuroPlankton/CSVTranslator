package CSVTranslator.importexport;

import CSVTranslator.FireBaseRequests;
import CSVTranslator.auth.AuthHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.MediaType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSVImporter {
    private static final String DELIMITER = "^\\s*\"?\\s*(.*?)\\s*\"?\\s*$";
    private static final Pattern CSV_PATTERN = Pattern.compile("\\s*(?:\"[^\"]*\"|(?:^|(?<=,))[^,]*)");

    private List<String> firstLineAsList;

    private String filePath;
    private JsonObject libraryObject;
    private String mainLang;
    private JsonObject textsObject = new JsonObject();

    public void readCsvAndImportToFirebase(String filePath, String fileName, JsonObject languagesObject, String mainLang) {
        this.filePath = filePath;
        this.mainLang = mainLang;
        libraryObject = new JsonObject();
        libraryObject.addProperty("library_name", fileName);
        libraryObject.add("languages", new Gson().toJsonTree(languagesObject));

        JsonObject usersObject = new JsonObject();
        AuthHelper authHelper = AuthHelper.getInstance();
        usersObject.addProperty(authHelper.getUserID(), authHelper.getDisplayName());
        libraryObject.add("users", new Gson().toJsonTree(usersObject));

        readCSV(this::parseCSVLinesToJSON);
    }

    public void readCSV(CsvLineHandlerInterface lineHandler) {
        BufferedReader br = null;
        String line = "";

        try {
            br = new BufferedReader(new FileReader(filePath));

            while ((line = br.readLine()) != null) {
                lineHandler.handleCsvLine(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            libraryObject.add("texts", new Gson().toJsonTree(textsObject));

            FireBaseRequests fireBaseRequests = new FireBaseRequests();
            fireBaseRequests.putData(
                    String.format("https://csv-android-app-f0353-default-rtdb.firebaseio.com/libraries/%s.json",
                            UUID.randomUUID().toString()),
                    libraryObject.getAsString(), //TODO: bug with this line
                    MediaType.parse("application/json"));
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void parseCSVLinesToJSON(String line) {
        JsonObject textObject = new JsonObject();
        List<String> cellList = splitCSVLine(line);

        if (!cellList.get(0).isEmpty()) {
            textObject.addProperty("android_key", cellList.get(0));
        }
        if (!cellList.get(1).isEmpty()) {
            textObject.addProperty("ios_key", cellList.get(1));
        }
        if (!cellList.get(2).isEmpty()) {
            textObject.addProperty("web_admin_key", cellList.get(2));
        }
        if (!cellList.get(3).isEmpty()) {
            textObject.addProperty("web_key", cellList.get(3));
        }
        if (!cellList.get(4).isEmpty()) {
            textObject.addProperty("web_widget_key", cellList.get(4));
        }

        JsonObject translationsObject = new JsonObject();
        if (textObject.keySet().size() > 0) {
            for (int index = 5; index < cellList.size(); index++) {
                if (!cellList.get(index).isEmpty()) {
                    translationsObject.addProperty(firstLineAsList.get(index), cellList.get(index));
                }
            }
        }

        if (translationsObject.keySet().size() > 0) {
            textObject.add("translations", translationsObject);
            try {
                textObject.addProperty("name", translationsObject.get(mainLang).getAsString());
            } catch (NullPointerException e) {
                for (String translationLangKey : translationsObject.keySet()) {
                    textObject.addProperty("name", translationsObject.get(translationLangKey).getAsString());
                    if (!textObject.get("name").getAsString().isEmpty()) {
                        break;
                    }
                }
            }
            textObject.addProperty("description", "Imported library. No description set.");
            textsObject.add(UUID.randomUUID().toString(), new Gson().toJsonTree(textObject));
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
