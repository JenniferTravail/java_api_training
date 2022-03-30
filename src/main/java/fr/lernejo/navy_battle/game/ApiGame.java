package fr.lernejo.navy_battle.game;

import org.json.JSONException;
import org.json.JSONObject;

public class ApiGame {
    private final String url;
    private final String message;

    public ApiGame(String url, String message) {
        this.url = url;
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public String getMessage() {
        return message;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("url", url);
        obj.put("message", message);
        return obj;
    }

    public static ApiGame fromJSON(JSONObject object) throws JSONException {
        return new ApiGame(
            object.getString("url"),
            object.getString("message")
        );
    }

    public ApiGame withURL(String url) {
        return new ApiGame(
            url,
            this.message
        );
    }
}
