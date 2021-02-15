import auth.AuthHelper;

import javax.swing.*;

public class CSVTranslatorMain {
    public static void main(String[] args) {
        TranslatorUI translatorUI = new TranslatorUI();
        SwingUtilities.invokeLater(translatorUI.RunUI());
//        FireBaseRequests fireBaseRequests = new FireBaseRequests();
//        fireBaseRequests.put("https://csv-android-app-f0353-default-rtdb.firebaseio.com/user_libraries/feHvfGJ3Iwc8D565wQU7GHnH5hu2/testi/hauskaa.json", "{\n" +
//                "  \"testi\":\"tää tuli restistä\",\n" +
//                "  \"fjldskjfsdjgfjsd\": \"sgfsdkjfhkds\",\n" +
//                "  \"sfasdfdsfsdf\":\"sfdadsfsfds\"\n" +
//                "}");
    }
}
