package CSVTranslator;

import CSVTranslator.util.Pair;
import okhttp3.*;

import java.io.IOException;

/**
 * @author s1800885
 */

public class FireBaseRequests {

    private final OkHttpClient client = new OkHttpClient();

    public Pair<String, Boolean> patchData(String url, String jsonBody, MediaType mediaType) {
        RequestBody body = RequestBody.create(jsonBody, mediaType);

        Request request = new Request.Builder()
                .url(url)
                .patch(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            if(responseBody != null) {
                String responseBodyString = responseBody.string();
                if (!("null").equals(responseBodyString)) {
                    return new Pair<>(responseBodyString, (response.isSuccessful()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Pair<String, Boolean> deleteData(String url) {
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            return new Pair<>(response.body().string(), (response.isSuccessful()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Pair<String, Boolean> getData(String url) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();

            if(responseBody != null) {
                String responseBodyString = responseBody.string();
                if (!("null").equals(responseBodyString)) {
                    return new Pair<>(responseBodyString, (response.isSuccessful()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Pair<String, Boolean> postData(String url, String jsonBody, MediaType mediaType) {
        RequestBody body = RequestBody.create(jsonBody, mediaType);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                String responseBodyString = responseBody.string();
                if (!("null").equals(responseBodyString)) {
                    return new Pair<>(responseBodyString, (response.isSuccessful()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Pair<String, Boolean> putData(String url, String jsonBody, MediaType mediaType) {
        RequestBody body = RequestBody.create(jsonBody, mediaType);

        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                String responseBodyString = responseBody.string();
                if (!("null").equals(responseBodyString)) {
                    return new Pair<>(responseBodyString, (response.isSuccessful()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}