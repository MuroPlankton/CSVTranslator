package auth;

import okhttp3.OkHttp;
import okhttp3.RequestBody;

public class AuthHelper {
    public void signNewUserIn(String email, String displayName, String password) {
        String signInJson = String.format("{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}", email, displayName);

        OkHttp okHttp;
        RequestBody body;
    }

    public void logExistingUserIn(String email, String password) {

    }
}
