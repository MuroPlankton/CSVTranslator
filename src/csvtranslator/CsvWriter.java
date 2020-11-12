package csvtranslator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CsvWriter {

    private FileWriter writer;

    private String os;
    private String lang;

    public CsvWriter(String os, String lang) {
        this.os = os;
        this.lang = lang;

        String dir;
        String fileName;
        switch (os) {
            default:
            case "android":
                writeOneRow("<resources>");
                dir = "\\values-" + lang;
                fileName = "string.xml";
                break;
            case "ios":
                dir = String.format("\\%s.lproi", lang);
                fileName = "Localizable.strings";
                break;
        }

        File file = new File(dir, fileName);
        try {
            file.mkdirs();
            writer = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeOneRow(String row) {
        try {
            writer.write(row + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopWriting() {
        writeOneRow("</resources>");
        try {
            writer.close();
            System.out.println("Writing complete");
        } catch (IOException e) {
            System.out.println("Error!");
        }
    }
}
