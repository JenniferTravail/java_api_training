package fr.lernejo.navy_battle;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import fr.lernejo.navy_battle.game.ApiGame;
import fr.lernejo.navy_battle.game.BaseGame;
import fr.lernejo.navy_battle.game.CoordinatesGame;
import fr.lernejo.navy_battle.game.MapGame;
import fr.lernejo.navy_battle.utilEnum.CellStatus;
import fr.lernejo.navy_battle.utilEnum.Consequence;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Api extends Server{
    private final BaseGame<ApiGame> api = new BaseGame<>();
    private final BaseGame<ApiGame> client = new BaseGame<>();
    private final BaseGame<MapGame> serverMap = new BaseGame<>();
    private final BaseGame<MapGame> clientMap = new BaseGame<>();

    public void create(int port, String url) throws IOException {
        api.set(new ApiGame(
            "http://localhost:" + port,
            "OK"
        ));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newSingleThreadExecutor());
        server.createContext("/ping", this::ping);
        server.createContext("/api/game/start", s -> handleStart(new RequestHandler(s)));
        server.start();
        if (url != null)
            this.clientStart(url);
    }
    private void ping(HttpExchange exchange) throws IOException {
        String body = "OK";
        exchange.sendResponseHeaders(200, body.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(body.getBytes());
        }
    }

    public void clientStart(String url) {
        try {
            serverMap.set(new MapGame(true));
            System.out.println(this.api.get().toJSON().toString());
            clientMap.set(new MapGame(false));
            var response = post(url + "/api/game/start", this.api.get().toJSON());
            this.client.set(ApiGame.fromJSON(response).withURL(url));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to start the game");
        }
    }

    public void handleStart(RequestHandler handler) throws IOException {
        try {
            client.set(ApiGame.fromJSON(handler.getJSON()));
            serverMap.set(new MapGame(true));
            clientMap.set(new MapGame(false));
            System.out.println("Server will fight against the following client: " + client.get().getUrl());
            handler.response(202, api.get().toJSON());
        } catch (Exception e) {
            e.printStackTrace();
            handler.responseString(400, e.getMessage());
        }
    }



}
