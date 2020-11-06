package csvtranslator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author s1800870
 */
public class CSVTranslator {

    /**
     * @param args the command line arguments
     */
    private int linesHandled = 0;
    static String lineToModify = "", modifiedLine = "";
    static String[] lineHandler;
    static int key = 0, value = 0;

    public static void main(String[] args) {
        // TODO code application logic here
        String fileName = args[0], outputPlattform = args[1], language = args[2];
        csvReader();
    }

    private static void dataHandler(String line) {

    }

    private static void csvReader() {
        String csvFile = "C:\\Users\\s1800885\\Documents\\CSVTranslator\\src\\csvtranslator\\CSVdata.csv";
        BufferedReader br = null;
        String line = "";
        String splitter = ",";

        try {
            br = new BufferedReader(new FileReader(csvFile));

            while ((line = br.readLine()) != null) {

                String[] data = line.split(splitter);

                //       System.out.println("<String name=“”" + data[0] + ">" + data[2] + "</string>");
                System.out.println(data[0] + ", " + data[1] + ", " + data[2] + ", " + data[3]);
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
