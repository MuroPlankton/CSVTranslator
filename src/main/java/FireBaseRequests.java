import okhttp3.*;

import java.io.IOException;

/**
 * @author s1800870
 */

public class FireBaseRequests {



    public String put(String url, String jsonBody) {

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, jsonBody);

        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println(response.body().toString());
            return response.body().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}