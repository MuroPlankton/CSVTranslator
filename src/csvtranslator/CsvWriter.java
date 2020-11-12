package csvtranslator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CsvWriter {

    private FileWriter writer;
    private String directory;
    private String filename;

    public CsvWriter(String directory, String filename){
        this.directory = directory;
        this.filename = filename;
    }

    public void beginWriting() {
        File file = new File("strings.xml");

        try {
            //If true is added here, the writer doesn't overwrite the existing text
            writer = new FileWriter(file);
            System.out.println("Writing...");
        } catch (IOException e) {
            System.out.println("Error!");
        }
    }

    public void writeOneRow(String row) throws IOException {
        writer.write(row + System.lineSeparator());
    }

    public void stopWriting() {
        try {
            writer.close();
            System.out.println("Writing complete");
        } catch (IOException e) {
            System.out.println("Error!");
        }
    }
}
