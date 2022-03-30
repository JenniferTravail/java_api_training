package fr.lernejo.navy_battle.game;

import java.util.ArrayList;
import java.util.List;

public class BaseGame<Entity> {
    private final List<Entity> list = new ArrayList<>();

    public void set(Entity obj) {
        list.clear();
        list.add(obj);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public Entity get() {
        if (list.isEmpty())
            throw new RuntimeException("Entity is null");

        return list.get(0);
    }
}
