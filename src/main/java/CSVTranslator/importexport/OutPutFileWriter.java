package CSVTranslator.importexport;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class OutPutFileWriter {
    private FileWriter writer;

    private final String fileType;
    private final String lang;

    private final String ANDROID = "android";
    private final String IOS = "ios";
    private final String WEB_ADMIN = "web-admin";
    private final String WEB_MAIN = "web-main";
    private final String WEB_WIDGET = "web-widget";
    private final String CSV = "csv";

    private boolean isFirstLineWritten = false;

    public OutPutFileWriter(String fileType, String lang, String translationFolder) {
        this.fileType = fileType;
        this.lang = lang;

        String upperDir;
        String dir;
        String fileName;

        // if (user puts upperDir) {it takes that directory} else {upperDir is translations}
        if (translationFolder != null) {
            upperDir = translationFolder;
        } else {
            upperDir = "./translations";
        }

        switch (fileType) {
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
            case CSV:
                dir = "./csv";
                fileName = "library name...";
                break;
        }

        File file = new File(upperDir, dir);

        try {
            file.mkdirs();
            if (file.exists()) {
                File fileDir = new File(file, fileName);
                writer = new FileWriter(fileDir);
            } else {
                System.out.println("Directory couldn't be created.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (ANDROID.equals(fileType)) {
            internalWriteOneRow("<resources>", null, false);
        }
        if (WEB_ADMIN.equals(fileType) || WEB_MAIN.equals(fileType) || WEB_WIDGET.equals(fileType)) {
            internalWriteOneRow("{", null, false);
        }
    }

    public String getFileType() {
        return fileType;
    }

    public String getLang() {
        return lang;
    }

    // previousLineAppendic: what character do you want to add to the previous line
    // newLine: should there be a line before this row
    public void writeOneRow(String row, String previousLineAppendix, boolean newLine) {
        isFirstLineWritten = true;
        internalWriteOneRow(row, previousLineAppendix, newLine);
    }

    private void internalWriteOneRow(String row, String previousLineAppendix, boolean newLine) {
        try {
            if (previousLineAppendix != null) {
                writer.append(previousLineAppendix);
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
        if (ANDROID.equals(fileType)) {
            writeOneRow("</resources>", null, true);
        }

        if (WEB_ADMIN.equals(fileType) || WEB_MAIN.equals(fileType) || WEB_WIDGET.equals(fileType)) {
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
