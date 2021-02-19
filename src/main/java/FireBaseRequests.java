import okhttp3.*;

import java.io.IOException;

/**
 * @author s1800885
 */

public class FireBaseRequests {

    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    public String putDataToUserLibraries(String url, String jsonBody) {

        RequestBody body = RequestBody.create(JSON, jsonBody);

        Request request = new Request.Builder()
                .url(url)
                .patch(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println(response.toString());
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}