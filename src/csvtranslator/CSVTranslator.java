package csvtranslator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author s1800870
 */
public class CSVTranslator {

    /**
     * @param args the command line arguments
     */
    private static int linesHandled = 0;
    static String lineToModify = "", modifiedLine = "", outputPlattform = "", language = "";
    static List<String> lineHandler;
    static int key = 0, value = 0;

    public static void main(String[] args) {
        // TODO code application logic here
        String fileName = args[0];
        outputPlattform = args[1];
        language = args[2];
        System.out.println("<resources>");
        csvReader(fileName);
        System.out.println("<resources>");
    }

    private static void dataHandler(String line) {
        lineHandler = Arrays.asList(line.split(","));
        if (linesHandled == 0) {
            key = lineHandler.indexOf(outputPlattform);
            value = lineHandler.indexOf(language);
        } else {
            System.out.println("<string name=\"" + lineHandler.get(key) + "\">" + lineHandler.get(value) + "</String>");
        }
    }

    private static void csvReader(String fileName) {
        BufferedReader br = null;
        String line = "";
        String splitter = ",";

        try {
            br = new BufferedReader(new FileReader(fileName));

            while ((line = br.readLine()) != null) {

                String[] data = line.split(splitter);

                //       System.out.println("<String name=“”" + data[0] + ">" + data[2] + "</string>");
                dataHandler(data[0] + "," + data[1] + "," + data[2] + "," + data[3]);
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

    }
}
