package auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;

public class AuthHelper {

    private final static String PROJECT_API_KEY = "AIzaSyBxx--8gER1rh7py1o6jYLWb_FiN5kvom4";
    private final static String PROJECT_ID = "csv-android-app-f0353-default-rtdb";

    private static AuthHelper instance;

    private String refreshToken;
    private String idToken;
    private long tokenExpiryTime;

    private String userID;

    private OkHttpClient client = new OkHttpClient();

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
        RequestBody signInBody = RequestBody.create(signInJson, MediaType.parse("application/json"));

        Request signInRequest = new Request.Builder()
                .url(String.format("https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=%s", PROJECT_API_KEY))
                .post(signInBody).build();
        try {
            setTokenInfoAndUID(JsonParser.parseString(client.newCall(signInRequest).execute().body().string()).getAsJsonObject());
        } catch (IOException e) {
            e.printStackTrace();
        }

        setNewDisplayName(displayName);
    }

    public void logExistingUserIn(String email, String password) {
        String signInJson = String.format("{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}",
                email, password);
        RequestBody signInBody = RequestBody.create(signInJson, MediaType.parse("application/json"));

        Request signInRequest = new Request.Builder()
                .url(String.format("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=%s",
                        PROJECT_API_KEY)).post(signInBody).build();
        try {
            setTokenInfoAndUID(JsonParser.parseString(client.newCall(signInRequest).execute()
                    .body().string()).getAsJsonObject());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getIDToken() {
        if (tokenExpiryTime < System.currentTimeMillis()) {
            return idToken;
        } else {
            String tokenRefreshRBString = String.format("grant_type=refresh_token&refresh_token=%s", refreshToken);
            RequestBody tokenRefreshRB = RequestBody.create(tokenRefreshRBString,
                    MediaType.parse("application/x-www-form-urlencoded"));

            Request tokenRefreshRequest = new Request.Builder()
                    .url(String.format("https://securetoken.googleapis.com/v1/token?key=%s", PROJECT_API_KEY))
                    .post(tokenRefreshRB).build();
            try {
                return JsonParser.parseString(
                        client.newCall(tokenRefreshRequest).execute().body().string())
                        .getAsJsonObject().get("id_token").getAsString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public String getDisplayName() {
        return null;
    }

    public void setNewDisplayName(String displayName) {
        String setNameJson = String.format("{\"idToken\":\"%s\",\"displayName\":\"%s\",\"returnSecureToken\":false}",
                getIDToken(), displayName);
        RequestBody setNameBody = RequestBody.create(setNameJson, MediaType.parse("application/json"));

        Request setNameRequest = new Request.Builder()
                .url(String.format("https://identitytoolkit.googleapis.com/v1/accounts:update?key=%s", PROJECT_API_KEY))
                .post(setNameBody).build();
        try {
            client.newCall(setNameRequest).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String addToRealtimeUsersJson = String.format("{\"%s\":\"%s\"}", userID, displayName);
        RequestBody addRealtimeUserRB = RequestBody.create(addToRealtimeUsersJson, MediaType.parse("application/json"));

        Request addRealtimeUserRequest = new Request.Builder()
                .url(String.format("https://%s.firebaseio.com/users.json?auth=%s", PROJECT_ID, getIDToken()))
                .patch(addRealtimeUserRB).build();
        try {
            client.newCall(addRealtimeUserRequest).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

                RequestBody userInLibrariesRB = RequestBody.create(changeUserInLibrariesJson, MediaType.parse("application/json"));
                
                Request userInLibrariesRequest = new Request.Builder()
                        .url(String.format("https://%s.firebaseio.com/libraries.json?auth=%s", PROJECT_ID, getIDToken()))
                        .patch(userInLibrariesRB).build();
                System.out.println(client.newCall(userInLibrariesRequest).execute().body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTokenInfoAndUID(JsonObject object) {
        tokenExpiryTime = System.currentTimeMillis() + 3600;
        idToken = object.get("idToken").getAsString();
        refreshToken = object.get("refreshToken").getAsString();
        userID = object.get("localId").getAsString();
    }
}
