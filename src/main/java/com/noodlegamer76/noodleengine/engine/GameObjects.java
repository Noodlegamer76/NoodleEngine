package com.noodlegamer76.noodleengine.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameObjects {
    private static final Map<UUID, GameObject> GAME_OBJECTS = new ConcurrentHashMap<>();

    public static Map<UUID, GameObject> getGameObjects() {
        return GAME_OBJECTS;
    }

    public static void addGameObject(GameObject gameObject) {
        GAME_OBJECTS.put(gameObject.getId(), gameObject);
    }

    public static void removeGameObject(GameObject gameObject) {
        GAME_OBJECTS.remove(gameObject.getId());
    }

    public static void clear() {
        GAME_OBJECTS.clear();
    }
}
