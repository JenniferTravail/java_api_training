package fr.lernejo.navy_battle;

import com.sun.net.httpserver.HttpServer;
import fr.lernejo.navy_battle.PingHello.PingHello;
import fr.lernejo.navy_battle.game.ApiGame;
import fr.lernejo.navy_battle.game.BaseGame;
import fr.lernejo.navy_battle.game.CoordinatesGame;
import fr.lernejo.navy_battle.game.MapGame;
import fr.lernejo.navy_battle.utilEnum.CellStatus;
import fr.lernejo.navy_battle.utilEnum.Consequence;
import org.json.JSONObject;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Api extends Server{
    private final BaseGame<ApiGame> api = new BaseGame<>();
    private final BaseGame<ApiGame> client = new BaseGame<>();
    private final BaseGame<MapGame> serverMap = new BaseGame<>();
    private final BaseGame<MapGame> clientMap = new BaseGame<>();

    public void create(int port, String url) throws IOException {
        api.set(new ApiGame("http://localhost:" + port, "OK"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newSingleThreadExecutor());
        server.createContext("/ping", new PingHello());
        server.createContext("/api/game/start", s -> handleStart(new RequestHandler(s)));
        server.createContext("/api/game/fire", s -> handleFire(new RequestHandler(s)));
        server.start();
        if (url != null)
            this.clientStart(url);
    }
    public void clientStart(String url) {
        try {
            serverMap.set(new MapGame(true));
            System.out.println(this.api.get().toJSON().toString());
            clientMap.set(new MapGame(false));
            var res = post(url + "/api/game/start", this.api.get().toJSON());
            this.client.set(ApiGame.fromJSON(res).withURL(url));
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
            System.out.println("Server fight " + client.get().getUrl());
            handler.response(202, api.get().toJSON());
            fire();
        } catch (Exception e) {
            e.printStackTrace();
            handler.responseString(400, e.getMessage());
        }
    }
    public void fire() throws IOException, InterruptedException {
        CoordinatesGame coordinates = clientMap.get().getNextPlaceToHit();
        var resJson =
            get(client.get().getUrl() + "/api/game/fire?cell=" + coordinates.toString());
        if (!resJson.getBoolean("shipLeft")) {
            return;
        }
        var res = Consequence.fromAPI(resJson.getString("consequence"));
        if (res == Consequence.MISS)
            clientMap.get().setCell(coordinates, CellStatus.MISSED_FIRE);
        else
            clientMap.get().setCell(coordinates, CellStatus.SUCCESSFUL_FIRE);
    }
    public void handleFire(RequestHandler handler) throws IOException {
        try {
            String cell = handler.getQueryParameter("cell");
            var coordinate = new CoordinatesGame(cell);
            var fireRes = serverMap.get().hit(coordinate);
            var res = new JSONObject();
            res.put("consequence", fireRes.toAPI());
            res.put("shipLeft", serverMap.get().hasShipLeft());
            handler.response(200, res);
            if (serverMap.get().hasShipLeft()) {
                fire();
            }
        } catch (Exception e) {
            e.printStackTrace();
            handler.responseString(400, e.getMessage());
        }
    }
}
