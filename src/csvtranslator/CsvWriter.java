package csvtranslator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CsvWriter {

    private FileWriter writer;

    private String os;
    private String lang;

    private final String ANDROID = "android";
    private final String IOS = "ios";
    private final String WEB_ADMIN = "web-admin";
    private final String WEB_MAIN = "web-main";
    private final String WEB_WIDGET = "web-widget";

    private boolean isFirstLineWritten = false;

    public CsvWriter(String os, String lang) {
        this.os = os;
        this.lang = lang;

        String upperDir = "./translations";
        String dir;
        String fileName;

        switch (os) {
            default:
            case ANDROID:
                dir = "./values-" + lang;
                fileName = "strings.xml";
                break;
            case IOS:
                dir = String.format("./%s.lproj", lang);
                fileName = "Localizable.strings";
                break;
            case WEB_ADMIN:
                dir = "./admin-web";
                fileName = String.format("./%s.json", lang);
                break;
            case WEB_MAIN:
                dir = "./main-web";
                fileName = String.format("./%s.json", lang);
                break;
            case WEB_WIDGET:
                dir = "./widget-web";
                fileName = String.format("./%s.json", lang);
                break;
        }

        File file = new File(upperDir, dir);

        try {
            file.mkdirs();
            if(file.exists()){
                File langDir = new File(file, fileName);
                writer = new FileWriter(langDir);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (ANDROID.equals(os)) {
            internalWriteOneRow("<resources>", null, false);
        }
        if (WEB_ADMIN.equals(os) || WEB_MAIN.equals(os) || WEB_WIDGET.equals(os)) {
            internalWriteOneRow("{", null, false);
        }
    }

    //previousLineAppendic: what character do you want to add to the previous line
    // newLine: should there be a line before this row
    public void writeOneRow(String row, String previousLineAppendic, boolean newLine) {
        isFirstLineWritten = true;
        internalWriteOneRow(row, previousLineAppendic, newLine);
    }

    private void internalWriteOneRow(String row, String previousLineAppendic, boolean newLine) {
        try {
            if (previousLineAppendic != null) {
                writer.append(previousLineAppendic);
            }
            if (newLine) {
                writer.write(System.lineSeparator());
            }
            writer.write(row);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopWriting() {
        if (ANDROID.equals(os)) {
            writeOneRow("</resources>", null, true);
        }

        if (WEB_ADMIN.equals(os) || WEB_MAIN.equals(os) || WEB_WIDGET.equals(os)) {
            writeOneRow("}", null, true);
        }
        try {
            writer.close();
            System.out.println("Writing complete");
        } catch (IOException e) {
            System.out.println("Error!");
        }
    }

    public boolean isFirstLineWritten() {
        return isFirstLineWritten;
    }
}
