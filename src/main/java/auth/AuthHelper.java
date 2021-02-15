package auth;

import okhttp3.*;

public class AuthHelper {
    public static void signNewUserIn(String email, String displayName, String password) {
        String signInJson = String.format("{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}", email, displayName);

        RequestBody body = RequestBody.create(signInJson, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url("https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=AIzaSyBxx--8gER1rh7py1o6jYLWb_FiN5kvom4")
                .post(body).build();

    }

    public void logExistingUserIn(String email, String password) {

    }
}
