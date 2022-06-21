package managers;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    URI url;
    private String API_TOKEN;
    private final HttpClient httpClient;
    HttpRequest request;

    public KVTaskClient(String path) {
        this.url = URI.create(path);
        httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        request = HttpRequest.newBuilder().uri(URI.create(path + "/register/")).GET().build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            API_TOKEN = response.body();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void put(String key, String json) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/save/" + key + "?API_TOKEN=" + API_TOKEN))
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException();
            }
        } catch (NullPointerException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    String load(String key) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/load/" + key + "?API_TOKEN=" + API_TOKEN))
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (NullPointerException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
