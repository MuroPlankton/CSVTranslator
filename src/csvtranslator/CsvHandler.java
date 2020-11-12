/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csvtranslator;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CsvHandler {

    private int linesHandled = 0;
    private String lineToModify = "";
    private String modifiedLine = "";
    private int value = 0;
    public int key = 0;
    private List<CsvWriter> writerList = new ArrayList<>();

    public String fileName;

    public CsvHandler(String fileName) {
        this.fileName = fileName;
    }

    public void dataHandler(String line) throws IOException {
        List<String> cellList = Arrays.asList(line.split(","));
        System.out.println(cellList + String.format(" has %s values", cellList.size()));

        if (linesHandled == 0) {
            for (int listIndex = 0; listIndex < (cellList.size() - 2) * 2; listIndex++) {
                if (listIndex < writerList.size() / 2) {
                    String dir = "\\values_" + cellList.get(listIndex / 2);
                    writerList.add(new CsvWriter(dir, "strings.xml"));
                    writerList.get(listIndex).writeOneRow("<resources>");
                } else {
                    String dir = String.format("\\%s.lproi", cellList.get(listIndex / 2));
                    writerList.add(new CsvWriter(dir, "Localizable.strings"));
                }
            }
            for (int osIndex = 0; osIndex < 2; osIndex++) {
                for (int langIndex = 0; langIndex < cellList.size() - 2; langIndex++) {
                    if (osIndex == 0) {
                        String dir = "\\values_" + cellList.get(langIndex + 2);
                        writerList.add(new CsvWriter(dir, "strings.xml"));
                        writerList.get(langIndex).writeOneRow("<resources>");
                    } else {
                        String dir = String.format("\\%s.lproi", cellList.get(langIndex + 2));
                        writerList.add(new CsvWriter(dir, "Localizable.strings"));
                    }
//                    writerList.get(langIndex * (osIndex + 1)).beginWriting();
                }
            }

        } else if (!cellList.isEmpty()) {
            String osKey = null;
            String langValue = null;

            for (int listIndex = 0; listIndex < writerList.size(); listIndex++) {
                int key = ((listIndex < writerList.size() / 2) ? 0 : 1);
                if (cellList.size() > key) {
                    osKey = cellList.get(key);
                }
                if(cellList.size() > listIndex / 2 + 2) {
                    langValue = cellList.get(listIndex / 2 + 2);
                }

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

    public void csvReader(String fileName) throws IOException {
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

    private void finishWriterWriting() throws IOException {
        for (int listIndex = 0; listIndex < writerList.size(); listIndex++) {
            if (listIndex < writerList.size() / 2) {
                writerList.get(listIndex).writeOneRow("</resources>");
            }
            writerList.get(listIndex).stopWriting();
        }
    }
}
