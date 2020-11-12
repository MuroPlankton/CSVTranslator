/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csvtranslator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class CsvHandler {

    private int linesHandled = 0;
    private String lineToModify = "";
    private String modifiedLine = "";
    private int value = 0;
    public int key = 0;
    private List<CSVWriter> writerList;


    public String fileName;
    private FileWriter writer;

    public CsvHandler(String fileName) {
        this.fileName = fileName;
    }

    public void dataHandler(String line) throws IOException {
        List<String> cellList = Arrays.asList(line.split(","));
        if (linesHandled == 0) {
            for (int osIndex = 0; osIndex < 2; osIndex++) {
                for (int langIndex = 0; langIndex < cellList.size() - 2; langIndex++) {
                    if (osIndex == 0) {
                        String dir = "\\values_" + cellList.get(langIndex + 2);
                        writerList.add(new CSVWriter(dir, "strings.xml"));
                        writerList.get(langIndex).writeOneRow("<resources>");
                    } else {
                        String dir = String.format("\\%s.lproi", cellList.get(langIndex + 2));
                        writerList.add(new CSVWriter(dir, "Localizable.strings"));
                    }
                    writerList.get(langIndex * (osIndex + 1)).beginWriting();
                }
            }

        } else if (!cellList.isEmpty()) {
            String osKey = null;
            String langValue = null;
            if (cellList.size() > key) {
                osKey = cellList.get(key);
            }
            if(cellList.size() > value) {
                langValue = cellList.get(value);
            }
            for (int listIndex = 0; listIndex <= writerList.size(); listIndex++) {
                osKey = ((listIndex < writerList.size() / 2) ? "android" : "ios");
                langValue = cellList.get(listIndex / 2 + 2);

                if(isNotEmpty(osKey) && isNotEmpty(langValue)) {
                    switch (key) {
                        case 0:
                            writerList.get(listIndex).writeOneRow(String.format("\t<string name=\"%s\">%s</string>", osKey, langValue));
                            break;
                        case 1:
                            writerList.get(listIndex).writeOneRow(String.format("\"%s\" = \"%s\";", osKey, langValue));
                    }
                }
            }

        }
        linesHandled++;
    }

    private boolean isNotEmpty(String text) {
        return text != null && text.length() > 0;
    }

    public void csvReader(String fileName) {
        BufferedReader br = null;
        String line = "";

        try {
            br = new BufferedReader(new FileReader(fileName));

            while ((line = br.readLine()) != null) {

                dataHandler(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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

        finishWriterWriting();
    }

    private void finishWriterWriting() {
        for (int listIndex = 0; listIndex <= writerList.size(); listIndex++) {
            if (listIndex < writerList.size() / 2) {
                writerList.get(listIndex).writeOneRow("</resources>");
            }
            writerList.get(listIndex).stopWriting();
        }
    }

    public void beginWriting() {
        File file = new File("strings.xml");

        try {
            //If the true is added here, the writer doesn't overwrite the existing text
            writer = new FileWriter(file);
            System.out.println("Tiedostoon on kirjoitettu");
        } catch (IOException e) {
            System.out.println("Virhe");
        }
    }

    public void writeOneRow(String row) throws IOException {
        writer.write(row + System.lineSeparator());
    }

    public void stopWriting() {
        try {
            writer.close();
            System.out.println("Lopetus");
        } catch (IOException e) {
            System.out.println("Virhe");
        }
    }

}
