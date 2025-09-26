package com.noodlegamer76.noodleengine.client.glitf.access;

import com.noodlegamer76.noodleengine.client.glitf.material.McMaterial;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class MaterialStorage {
    private static final Map<ResourceLocation, McMaterial> MATERIALS = new HashMap<>();

    public static McMaterial getMaterial(ResourceLocation location) {
        return MATERIALS.get(location);
    }

    public static void addMaterial(ResourceLocation location, McMaterial material) {
        MATERIALS.put(location, material);
    }

    public static void clear() {
        MATERIALS.clear();
    }

    public static boolean containsMaterial(ResourceLocation location) {
        return MATERIALS.containsKey(location);
    }

    public static int getMaterialCount() {
        return MATERIALS.size();
    }

    public static Map<ResourceLocation, McMaterial> getMaterials() {
        return MATERIALS;
    }
}
