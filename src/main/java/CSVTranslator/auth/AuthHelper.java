package CSVTranslator.auth;

import CSVTranslator.FireBaseRequests;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class AuthHelper {

    private final static String PROJECT_API_KEY = "AIzaSyBxx--8gER1rh7py1o6jYLWb_FiN5kvom4";
    private final static String PROJECT_ID = "csv-android-app-f0353-default-rtdb";
    private static final File refreshTokenFile = new File(System.getProperty("user.dir") + "refreshTokenFile.txt");

    private static AuthHelper instance;

    FireBaseRequests fireBaseRequests = new FireBaseRequests();

    private OnLoggedInListener listener;
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
        setTokenInfoAndUID(JsonParser.parseString(
                fireBaseRequests.postData(url, signInJson, MediaType.parse("application/json")))
                .getAsJsonObject());;

        setNewDisplayName(displayName);
        saveRefreshTokenToFile();

//        SwingUtilities.invokeLater();
    }

    public void logExistingUserIn(String email, String password) {
        String logInJson = String.format("{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}",
                email, password);

        RequestBody logInBody = RequestBody.create(logInJson, MediaType.parse("application/json"));

        Request logInRequest = new Request.Builder()
                .url(String.format("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=%s",
                        PROJECT_API_KEY)).post(logInBody).build();
        try {
            Response logInResponse = client.newCall(logInRequest).execute();

            if (logInResponse.isSuccessful()) {
                setTokenInfoAndUID(JsonParser.parseString(logInResponse.body().string()).getAsJsonObject());

                if (listener != null) {
                    listener.onLoggedIn();
                }

                saveRefreshTokenToFile();
            } else {
                System.out.println("Response for log in wasn't successful");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getIDToken() {
        if (tokenExpiryTime < System.currentTimeMillis()) {
            return idToken;
        } else {
            String tokenRefreshRBString = String.format("grant_type=refresh_token&refresh_token=%s", refreshToken);
            String url = String.format("https://securetoken.googleapis.com/v1/token?key=%s", PROJECT_API_KEY);

            return JsonParser.parseString(fireBaseRequests.postData(
                    url, tokenRefreshRBString, MediaType.parse("application/x-www-form-urlencoded")))
                    .getAsJsonObject().get("id_token").getAsString();
        }
    }

    public String getDisplayName() {
        String userInfoRequestURL = String.format("https://identitytoolkit.googleapis.com/v1/accounts:lookup?key=%s", PROJECT_API_KEY);
        String userInfoRequestJson = String.format("{\"idToken\":\"%s\"}", getIDToken());
        return JsonParser.parseString(
                fireBaseRequests.postData(userInfoRequestURL, userInfoRequestJson, MediaType.parse("application/json")))
                .getAsJsonObject().get("users").getAsJsonArray()
                .get(0).getAsJsonObject()
                .get("providerUserInfo").getAsJsonArray()
                .get(0).getAsJsonObject()
                .get("displayName").getAsString();
    }

    public void setNewDisplayName(String displayName) {
        String setNameJson = String.format("{\"idToken\":\"%s\",\"displayName\":\"%s\",\"returnSecureToken\":false}",
                getIDToken(), displayName);
        String setNameURL = String.format("ttps://identitytoolkit.googleapis.com/v1/accounts:update?key=%s",
                PROJECT_API_KEY);
        fireBaseRequests.postData(setNameURL, setNameJson, MediaType.parse("application/json"));

        String addToRealtimeUsersJson = String.format("{\"%s\":\"%s\"}", userID, displayName);
        String addToRealtimeUserURL = String.format("https://%s.firebaseio.com/users.json?auth=%s", PROJECT_ID, getIDToken());
        fireBaseRequests.patchData(addToRealtimeUserURL, addToRealtimeUsersJson, MediaType.parse("application/json"));

        Request userLibrariesRequest = new Request.Builder()
                .url(String.format("https://%s.firebaseio.com/user_libraries/%s.json?auth=%s", PROJECT_ID, userID, getIDToken()))
                .get().build();

        try {
            Response userLibrariesResponse = client.newCall(userLibrariesRequest).execute();

            if (userLibrariesResponse.isSuccessful()) {
                JsonObject userLibrariesObject = JsonParser.parseString(userLibrariesResponse.body().string()).getAsJsonObject();
                int amountOfUserLibraries = userLibrariesObject.size();
                int index = 0;

                String changeUserInLibrariesJson = "{\n";
                for (String userLibraryKey : userLibrariesObject.keySet()) {
                    changeUserInLibrariesJson += String.format("\"%s/users/%s\":\"%s\"%s", userLibraryKey, userID,
                            displayName, (index < amountOfUserLibraries - 1) ? ",\n" : "\n");
                    index++;
                }
                changeUserInLibrariesJson += "}";

                String changeUserInLibsURL = String.format("https://%s.firebaseio.com/libraries.json?auth=%s", PROJECT_ID, getIDToken());
                fireBaseRequests.patchData(changeUserInLibsURL, changeUserInLibrariesJson, MediaType.parse("application/json"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTokenInfoAndUID(JsonObject object) {
        System.out.println(object.toString());
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
            RequestBody tokenTestRB = RequestBody.create(tokenTestString,
                    MediaType.parse("application/x-www-form-urlencoded"));

            Request tokenTestRequest = new Request.Builder()
                    .url(String.format("https://securetoken.googleapis.com/v1/token?key=%s", PROJECT_API_KEY))
                    .post(tokenTestRB).build();

            Response tokenTrialResponse = client.newCall(tokenTestRequest).execute();
            if (tokenTrialResponse.isSuccessful()) {
                setTokenInfoAndUID(JsonParser.parseString(tokenTrialResponse.body().string()).getAsJsonObject());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public void setOnLoggedInListener(OnLoggedInListener listener) {
        this.listener = listener;
    }

    public interface OnLoggedInListener {
        void onLoggedIn();
    }

    public String getUserID() {
        return userID;
    }
}
