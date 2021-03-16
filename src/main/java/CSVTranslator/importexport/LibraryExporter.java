package CSVTranslator.importexport;

import CSVTranslator.FireBaseRequests;
import CSVTranslator.auth.AuthHelper;
import CSVTranslator.util.Pair;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class LibraryExporter {

    private List<OutPutFileWriter> writerList = new ArrayList<>();
    private String outputDir;

    private final String ANDROID = "android";
    private final String IOS = "ios";
    private final String WEB_ADMIN = "web-admin";
    private final String WEB_MAIN = "web-main";
    private final String WEB_WIDGET = "web-widget";

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

    private void addWritersToList(String lang) {
        writerList.add(new OutPutFileWriter("android", lang, outputDir));
        writerList.add(new OutPutFileWriter("ios", lang, outputDir));
        writerList.add(new OutPutFileWriter("web-admin", lang, outputDir));
        writerList.add(new OutPutFileWriter("web-main", lang, outputDir));
        writerList.add(new OutPutFileWriter("web-widget", lang, outputDir));
    }

    private void writeLinesToFiles(JsonObject libraryObject) {
        JsonObject textsObject = libraryObject.getAsJsonObject("texts");

        for (String textKey : textsObject.keySet()) {
            JsonObject textObject = textsObject.getAsJsonObject(textKey);

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
                            e.printStackTrace();
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

    private boolean isNotEmpty(String text) {
        return text != null && text.isEmpty();
    }
}
