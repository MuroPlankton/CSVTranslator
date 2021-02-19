import auth.AuthHelper;

import javax.swing.*;

public class CSVTranslatorMain {
    public static void main(String[] args) {
        TranslatorUI translatorUI = new TranslatorUI();
        SwingUtilities.invokeLater(translatorUI.RunUI());

        AuthHelper authHelper = AuthHelper.getInstance();

//        FireBaseRequests fireBaseRequests = new FireBaseRequests();
//        fireBaseRequests.putDataToUserLibraries("https://csv-android-app-f0353-default-rtdb.firebaseio.com/user_libraries/P8qkrPy2wWPAMdpvnIGSeuuWKzi2.json?auth=eyJhbGciOiJSUzI1NiIsImtpZCI6IjYxMDgzMDRiYWRmNDc1MWIyMWUwNDQwNTQyMDZhNDFkOGZmMWNiYTgiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vY3N2LWFuZHJvaWQtYXBwLWYwMzUzIiwiYXVkIjoiY3N2LWFuZHJvaWQtYXBwLWYwMzUzIiwiYXV0aF90aW1lIjoxNjEzMzk2OTEyLCJ1c2VyX2lkIjoiUDhxa3JQeTJ3V1BBTWRwdm5JR1NldXVXS3ppMiIsInN1YiI6IlA4cWtyUHkyd1dQQU1kcHZuSUdTZXV1V0t6aTIiLCJpYXQiOjE2MTMzOTY5MTIsImV4cCI6MTYxMzQwMDUxMiwiZW1haWwiOiJtaXJvdmFpbm9AZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJmaXJlYmFzZSI6eyJpZGVudGl0aWVzIjp7ImVtYWlsIjpbIm1pcm92YWlub0BnbWFpbC5jb20iXX0sInNpZ25faW5fcHJvdmlkZXIiOiJwYXNzd29yZCJ9fQ.P57Pp0PeuQkJDjAtPZklE_Us6pz4zU-e9LSUc8ATG7jrVXnGsrAQ00ly4Fxgta23I5isHgSvrWsruOrfegHzX03CAivg8SYgNoEOQaV0dvioUT8bSjbpFwDBPvju6TUYE7yPhUrC3wBqlhO3noJXuCTurLhjxOuNp53YDos1IOW16qWOeatJjFrLviOpMa1c8r3fASLRA39XwlKsWQqjbcvfd1HCv2vS0Z4Etan3iHx2CYaM843PJPh1IIOcYoTGkDT9DJbFAOQiqjVEESsAMFm4yE3I3INdhhRp9GRtqlNWYjygO2KPl5PAPey39YN9J11nXG0m_u3lLQigCTLN7Q", "{\n" +
//                "  \"testi\":\"tää tuli restistä\",\n" +
//                "  \"fjldskjfsdjgfjsd\": \"sgfsdkjfhkds\",\n" +
//                "  \"sfasdfdsfsdf\":\"sfdadsfsfdssdfdsfasfdsfadsfasfdfzfsdfrsfasdfasdf\"\n" +
//                "}");
    }
}
