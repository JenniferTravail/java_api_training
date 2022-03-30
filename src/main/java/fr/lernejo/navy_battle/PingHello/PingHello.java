package fr.lernejo.navy_battle.PingHello;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class PingHello implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String body = "Hello !";
        exchange.sendResponseHeaders(200, body.getBytes().length); //body code and length
        OutputStream os = exchange.getResponseBody();
        os.write(body.getBytes());
        os.close();
    }
}
