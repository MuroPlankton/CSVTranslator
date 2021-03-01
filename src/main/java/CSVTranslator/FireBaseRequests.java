package CSVTranslator;

import okhttp3.*;

import java.io.IOException;

/**
 * @author s1800885
 */

public class FireBaseRequests {

    private final OkHttpClient client = new OkHttpClient();

    public String patchData(String url, String jsonBody, MediaType mediaType) {

        RequestBody body = RequestBody.create(jsonBody, mediaType);

        Request request = new Request.Builder()
                .url(url)
                .patch(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
//            System.out.println(response.toString());
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String deleteData(String url) {

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
//            System.out.println(response.body().string());
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getData(String url) {

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
//            System.out.println(response.body().string());
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String postData(String url, String jsonBody, MediaType mediaType) {

        RequestBody body = RequestBody.create(jsonBody, mediaType);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
//            System.out.println(response.body().string());
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}