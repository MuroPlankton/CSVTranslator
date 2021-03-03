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
//            System.out.println(response.toString());
            return new Pair<>(response.body().string(), (response.isSuccessful()));
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
//            System.out.println(response.body().string());
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
            return new Pair<>(response.body().string(), (response.isSuccessful()));
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
//            System.out.println(response.body().string());
            System.out.println("Response is successful");
            return new Pair<>(response.body().string(), (response.isSuccessful()));
        } catch (IOException e) {
            e.printStackTrace();
//            return new Pair<>("", false);
        }
        return null;
    }
}