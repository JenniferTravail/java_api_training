package fr.lernejo.navy_battle;

import org.json.JSONObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class Server {
    private final HttpClient client = HttpClient.newHttpClient();

    public JSONObject post(String url, JSONObject obj) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .setHeader("Accept", "application/json")
            .setHeader("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(obj.toString()))
            .build();

        var response = client.send(req, HttpResponse.BodyHandlers.ofString());
        return new JSONObject(response.body());
    }


    public JSONObject get(String url) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .setHeader("Accept", "application/json")
            .GET()
            .build();

        var response = client.send(req, HttpResponse.BodyHandlers.ofString());
        return new JSONObject(response.body());
    }
}
