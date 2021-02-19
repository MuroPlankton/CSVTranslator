package auth;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

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
        JsonObject signInObject = null;
        try {
            JsonElement signInElement = JsonParser.parseString(client.newCall(signInRequest).execute().body().string());
            signInObject = signInElement.getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        tokenExpiryTime = System.currentTimeMillis() + 3600;
        idToken = signInObject.get("idToken").getAsString();
        refreshToken = signInObject.get("refreshToken").getAsString();
        userID = signInObject.get("localId").getAsString();

        setNewDisplayName(displayName);

        String addUserToRealtimeJson = String.format("{\"%s\":\"%s\"}", userID, displayName);
        RequestBody addRealtimeUserRB = RequestBody.create(addUserToRealtimeJson, MediaType.parse("application/json"));
        Request addRealtimeUserRequest = new Request.Builder()
                .url(String.format("https://%s.firebaseio.com/users.json?auth=%s", PROJECT_ID, idToken))
                .patch(addRealtimeUserRB).build();
        try {
            client.newCall(addRealtimeUserRequest).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logExistingUserIn(String email, String password) {
        String signInJson = String.format("{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}",
                email, password);
        RequestBody signInBody = RequestBody.create(signInJson, MediaType.parse("application/json"));
        Request signInRequest = new Request.Builder()
                .url(String.format("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=%s", PROJECT_API_KEY))
                .post(signInBody).build();
        JsonObject signInObject = null;
        try {
            JsonElement signInElement = JsonParser.parseString(client.newCall(signInRequest).execute().body().string());
            signInObject = signInElement.getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        tokenExpiryTime = System.currentTimeMillis() + 3600;
        idToken = signInObject.get("idToken").getAsString();
        refreshToken = signInObject.get("refreshToken").getAsString();
        userID = signInObject.get("localId").getAsString();
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
                String responseBodyString = JsonParser.parseString(
                        client.newCall(tokenRefreshRequest).execute().body().string())
                        .getAsJsonObject().get("id_token").getAsString();
                return responseBodyString;
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
        System.out.println(setNameJson);
        RequestBody setNameBody = RequestBody.create(setNameJson, MediaType.parse("application/json"));
        Request setNameRequest = new Request.Builder()
                .url(String.format("https://identitytoolkit.googleapis.com/v1/accounts:update?key=%s", PROJECT_API_KEY))
                .post(setNameBody).build();
        try {
            client.newCall(setNameRequest).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
