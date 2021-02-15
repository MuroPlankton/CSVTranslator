package auth;

import okhttp3.*;

import java.io.IOException;

public class AuthHelper {
    public static void signNewUserIn(String email, String displayName, String password) {
        String signInJson = String.format("{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}", email, password);
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(signInJson, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url("https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=AIzaSyBxx--8gER1rh7py1o6jYLWb_FiN5kvom4")
                .post(body).build();

        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void logExistingUserIn(String email, String password) {

    }
}
