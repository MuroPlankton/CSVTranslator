import okhttp3.*;

import java.io.IOException;

/**
 * @author s1800870
 */

public class FireBaseRequests {

    private OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public String put(String url, String jsonBody) {

        RequestBody body = RequestBody.create(JSON, jsonBody);
        
//        RequestBody requestBody = body.create(JSON, content);
        
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println(response.body().string());
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}