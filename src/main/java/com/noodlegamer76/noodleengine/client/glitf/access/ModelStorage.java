package com.noodlegamer76.noodleengine.client.glitf.access;

import com.noodlegamer76.noodleengine.client.glitf.McGltf;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ModelStorage {
    public static final Map<ResourceLocation, McGltf> MODELS = new HashMap<>();

    public static McGltf getModel(ResourceLocation location) {
        return MODELS.get(location);
    }

    public static void addModel(ResourceLocation location, McGltf model) {
        MODELS.put(location, model);
    }

    public static void clear() {
        MODELS.clear();
    }

    public static boolean containsModel(ResourceLocation location) {
        return MODELS.containsKey(location);
    }
}
