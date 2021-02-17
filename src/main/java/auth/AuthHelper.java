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
    public static void signNewUserIn(String email, String displayName, String password) {
        OkHttpClient client = new OkHttpClient();

        String signInJson = String.format("{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}", email, password);
        RequestBody signInBody = RequestBody.create(signInJson, MediaType.parse("application/json"));
        Request signInRequest = new Request.Builder()
                .url("https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=AIzaSyBxx--8gER1rh7py1o6jYLWb_FiN5kvom4")
                .post(signInBody).build();
        JsonObject signInObject = null;
        try {
            JsonElement signInElement = JsonParser.parseString(client.newCall(signInRequest).execute().body().string());
            signInObject = signInElement.getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String idToken = signInObject.get("idToken").getAsString();
        System.out.println(idToken);
        String refreshToken = signInObject.get("refreshToken").getAsString();
        String UID = signInObject.get("localId").getAsString();

        String setNameJson = String.format("{\n" +
                " \"idToken\":\"%s\",\n" +
                " \"displayName\":\"%s\",\n" +
                " \"returnSecureToken\":false\n" +
                "}", idToken, displayName);
        System.out.println(setNameJson);
        RequestBody setNameBody = RequestBody.create(setNameJson, MediaType.parse("application/json"));
        Request setNameRequest = new Request.Builder()
                .url("https://identitytoolkit.googleapis.com/v1/accounts:update?key=AIzaSyBxx--8gER1rh7py1o6jYLWb_FiN5kvom4")
                .post(setNameBody).build();
        try {
            System.out.println(client.newCall(setNameRequest).execute().body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logExistingUserIn(String email, String password) {

    }
}
