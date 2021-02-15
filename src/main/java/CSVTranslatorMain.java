import auth.AuthHelper;

import javax.swing.*;

public class CSVTranslatorMain {
    public static void main(String[] args) {
        TranslatorUI translatorUI = new TranslatorUI();
        SwingUtilities.invokeLater(translatorUI.RunUI());

        AuthHelper.signNewUserIn("mirovaino@gmail.com", "Muro_Plankton", "kauppakoti");

        FireBaseRequests fireBaseRequests = new FireBaseRequests();
        fireBaseRequests.put("https://csv-android-app-f0353-default-rtdb.firebaseio.com/user_libraries/bGUmNjaWswcjMsnDDS9dAnGmiys1.json?auth=eyJhbGciOiJSUzI1NiIsImtpZCI6IjYxMDgzMDRiYWRmNDc1MWIyMWUwNDQwNTQyMDZhNDFkOGZmMWNiYTgiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vY3N2LWFuZHJvaWQtYXBwLWYwMzUzIiwiYXVkIjoiY3N2LWFuZHJvaWQtYXBwLWYwMzUzIiwiYXV0aF90aW1lIjoxNjEzMzg2NzAzLCJ1c2VyX2lkIjoiYkdVbU5qYVdzd2NqTXNuRERTOWRBbkdtaXlzMSIsInN1YiI6ImJHVW1OamFXc3djak1zbkREUzlkQW5HbWl5czEiLCJpYXQiOjE2MTMzODY3MDMsImV4cCI6MTYxMzM5MDMwMywiZW1haWwiOiJtaXJvdmFpbm9AZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJmaXJlYmFzZSI6eyJpZGVudGl0aWVzIjp7ImVtYWlsIjpbIm1pcm92YWlub0BnbWFpbC5jb20iXX0sInNpZ25faW5fcHJvdmlkZXIiOiJwYXNzd29yZCJ9fQ.JcAVxSqPw_dWJErJh9L0cquk8Vbmqpe5jI68Mqz3jPQg8G-bq9cb59ZdDWrCj1mMf2YZaFvkhn0s8PBBPpltPxYkVClO8z8OOuAB5HgwCgQuA0VSoFMpsytJUPEBF25f7BwUN0XS9v_mweaJ9EueFEEoyj75Q2r--Esk4qRvyrG9wD2FC9mIHB0ggAcffOnZ-4ThPBDUul_cLAKv0QJtatvHCM3RL0LdSEp8uYhS_lZ17dMJQ-eK9RRp19Ka_-z3cUxQ12omacMwkSlUKeGdtQ8_0sTBT1FtJ14_9MOd7uH3T59FyRdvrfCVqGu0Nl-StzJpSCTEcMOno_botYfJxw", "{\n" +
                "  \"testi\":\"tää tuli restistä\",\n" +
                "  \"fjldskjfsdjgfjsd\": \"sgfsdkjfhkds\",\n" +
                "  \"sfasdfdsfsdf\":\"sfdadsfsfds\"\n" +
                "}");
    }
}
