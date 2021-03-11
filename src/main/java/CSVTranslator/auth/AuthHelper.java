package CSVTranslator.auth;

import CSVTranslator.CSVTranslatorMain;
import CSVTranslator.FireBaseRequests;
import CSVTranslator.util.Pair;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class AuthHelper {

    private final static String PROJECT_API_KEY = "AIzaSyBxx--8gER1rh7py1o6jYLWb_FiN5kvom4";
    private final static String PROJECT_ID = "csv-android-app-f0353-default-rtdb";
    private static final File refreshTokenFile = new File(System.getProperty("user.dir") + "\\refreshTokenFile.txt");

    private static AuthHelper instance;

    FireBaseRequests fireBaseRequests = new FireBaseRequests();

    private OnLoggedInListener onLoggedInListener;
    private OnSignedInListener onSignedInListener;

    private String refreshToken;
    private String idToken;
    private long tokenExpiryTime;

    private String userID;

    private final OkHttpClient client = new OkHttpClient();

    private AuthHelper() {
    }

    public static AuthHelper getInstance() {
        if (instance == null) {
            instance = new AuthHelper();
        }
        return instance;
    }

    public void signNewUserIn(String email, String displayName, String password) {
        String signInJson = String.format("{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}",
                email, password);

        String url = String.format("https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=%s", PROJECT_API_KEY);

        Pair<String, Boolean> signInResponseInfo =
                fireBaseRequests.postData(url, signInJson, MediaType.parse("application/json"));

        System.out.println("SIGN IN \n" + "key \n" + signInResponseInfo.getKey() + "value \n" + signInResponseInfo.getValue());

        if (signInResponseInfo.getValue()) {
            setTokenInfoAndUID(JsonParser.parseString(signInResponseInfo.getKey()).getAsJsonObject());
            setNewDisplayName(displayName);
            saveRefreshTokenToFile();

            if (onSignedInListener != null) {
                onSignedInListener.onSignedIn();
            }
            //TODO: might not be the best way to do this. Nothing else came to mind
        } else if (signInResponseInfo.getKey().contains("EMAIL_EXISTS") && CSVTranslatorMain.getLogInAndSignInPanel() != null) {
            JOptionPane.showMessageDialog(CSVTranslatorMain.getLogInAndSignInPanel(), "Email already exists");
        } else if (signInResponseInfo.getKey().contains("WEAK_PASSWORD") && CSVTranslatorMain.getLogInAndSignInPanel() != null) {
            JOptionPane.showMessageDialog(CSVTranslatorMain.getLogInAndSignInPanel(), "Password should be at least 6 characters");
        } else {
            System.out.println("Registering failed");
        }
    }

    public void logExistingUserIn(String email, String password) {
        String logInJson = String.format("{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}",
                email, password);
        String logInURL = String.format("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=%s",
                PROJECT_API_KEY);

        Pair<String, Boolean> logInResponseInfo =
                fireBaseRequests.postData(logInURL, logInJson, MediaType.parse("application/json"));
        if (logInResponseInfo.getValue()) {
            setTokenInfoAndUID(JsonParser.parseString(logInResponseInfo.getKey()).getAsJsonObject());
            saveRefreshTokenToFile();

            if (onLoggedInListener != null) {
                onLoggedInListener.onLoggedIn();
            }
        } else {
            //TODO: act accordingly to an unsuccessful response
            System.out.println("Response for log in wasn't successful");
        }
    }

    public String getIDToken() {
        if (tokenExpiryTime < System.currentTimeMillis()) {
            return idToken;
        } else {
            String tokenRefreshRBString = String.format("grant_type=refresh_token&refresh_token=%s", refreshToken);
            String url = String.format("https://securetoken.googleapis.com/v1/token?key=%s", PROJECT_API_KEY);

            Pair<String, Boolean> tokenRefreshResponseInfo = fireBaseRequests.postData(
                    url, tokenRefreshRBString, MediaType.parse("application/x-www-form-urlencoded"));
            if (tokenRefreshResponseInfo.getValue()) {
                return JsonParser.parseString(tokenRefreshResponseInfo.getKey())
                        .getAsJsonObject().get("id_token").getAsString();
            } else {
                //TODO: act accordingly to an unsuccessful response
                return null;
            }
        }
    }

    public String getDisplayName() {
        String userInfoRequestURL = String.format("https://identitytoolkit.googleapis.com/v1/accounts:lookup?key=%s", PROJECT_API_KEY);
        String userInfoRequestJson = String.format("{\"idToken\":\"%s\"}", getIDToken());

        Pair<String, Boolean> userInfoResponseInfo =
                fireBaseRequests.postData(userInfoRequestURL, userInfoRequestJson, MediaType.parse("application/json"));
        if (userInfoResponseInfo.getValue()) {
            return JsonParser.parseString(userInfoResponseInfo.getKey())
                    .getAsJsonObject().get("users").getAsJsonArray()
                    .get(0).getAsJsonObject()
                    .get("providerUserInfo").getAsJsonArray()
                    .get(0).getAsJsonObject()
                    .get("displayName").getAsString();
        } else {
            //TODO: act accordingly to an unsuccessful response
            return null;
        }
    }

    public void setNewDisplayName(String displayName) {
        String nameJson = String.format("{\"idToken\":\"%s\",\"displayName\":\"%s\",\"returnSecureToken\":false}",
                getIDToken(), displayName);
        String nameURL = String.format("https://identitytoolkit.googleapis.com/v1/accounts:update?key=%s",
                PROJECT_API_KEY);
        fireBaseRequests.postData(nameURL, nameJson, MediaType.parse("application/json"));

        String addToRealtimeUsersJson = String.format("{\"%s\":\"%s\"}", userID, displayName);
        String addToRealtimeUserURL = String.format("https://%s.firebaseio.com/users.json?auth=%s", PROJECT_ID, getIDToken());
        fireBaseRequests.patchData(addToRealtimeUserURL, addToRealtimeUsersJson, MediaType.parse("application/json"));

        Pair<String, Boolean> userLibsResponseInfo =
                fireBaseRequests.getData(String.format("https://%s.firebaseio.com/user_libraries/%s.json?auth=%s",
                        PROJECT_ID, userID, getIDToken()));

//        System.out.println("DISPLAY \n" + "key: \n " + userLibsResponseInfo.getKey() +
//                "\n value: \n" + userLibsResponseInfo.getValue());

        if (userLibsResponseInfo != null) {
            if (userLibsResponseInfo.getValue()) {
                JsonObject userLibrariesObject = JsonParser.parseString(userLibsResponseInfo.getKey()).getAsJsonObject();
                int amountOfUserLibraries = userLibrariesObject.size();
                int index = 0;

                StringBuilder changeUserInLibrariesJson = new StringBuilder("{\n");
                for (String userLibraryKey : userLibrariesObject.keySet()) {
                    changeUserInLibrariesJson.append(String.format("\"%s/users/%s\":\"%s\"%s", userLibraryKey, userID,
                            displayName, (index < amountOfUserLibraries - 1) ? ",\n" : "\n"));
                    index++;
                }
                changeUserInLibrariesJson.append("}");

                String changeUserInLibsURL = String.format("https://%s.firebaseio.com/libraries.json?auth=%s", PROJECT_ID, getIDToken());
                fireBaseRequests.patchData(changeUserInLibsURL, changeUserInLibrariesJson.toString(), MediaType.parse("application/json"));
            }
        }
    }

    private void setTokenInfoAndUID(JsonObject object) {
        tokenExpiryTime = System.currentTimeMillis() + 3_600_000;
        try {
            idToken = object.get("idToken").getAsString();
            refreshToken = object.get("refreshToken").getAsString();
            userID = object.get("localId").getAsString();
        } catch (NullPointerException e) {
            idToken = object.get("id_token").getAsString();
            refreshToken = object.get("refresh_token").getAsString();
            userID = object.get("user_id").getAsString();
        }
    }

    private void saveRefreshTokenToFile() {
        try {
            FileWriter tokenWriter = new FileWriter(System.getProperty("user.dir") + "\\refreshTokenFile.txt");
            tokenWriter.write(refreshToken);
            tokenWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isUserAlreadyLoggedIn() {
        try {
            Scanner refreshTokenScanner = new Scanner(refreshTokenFile);
            String scannerLine = refreshTokenScanner.nextLine();
            refreshTokenScanner.close();

            if (scannerLine == null || scannerLine.equals("")) {
                return false;
            }

            String tokenTestString = String.format("grant_type=refresh_token&refresh_token=%s", scannerLine);
            String tokenTrialURL = String.format("https://securetoken.googleapis.com/v1/token?key=%s", PROJECT_API_KEY);
            Pair<String, Boolean> tokenTrialResponseInfo = fireBaseRequests.postData(tokenTrialURL, tokenTestString, MediaType.parse("application/x-www-form-urlencoded"));

            if (tokenTrialResponseInfo.getValue()) {
                setTokenInfoAndUID(JsonParser.parseString(tokenTrialResponseInfo.getKey()).getAsJsonObject());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public String getUserID() {
        return userID;
    }

    public void setOnLoggedInListener(OnLoggedInListener listener) {
        this.onLoggedInListener = listener;
    }

    public interface OnLoggedInListener {
        void onLoggedIn();
    }

    public void setOnSignedInListener(OnSignedInListener listener) {
        this.onSignedInListener = listener;
    }

    public interface OnSignedInListener {
        void onSignedIn();
    }
}
