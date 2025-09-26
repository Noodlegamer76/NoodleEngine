package com.noodlegamer76.noodleengine.client.glitf.access;

import com.noodlegamer76.noodleengine.client.glitf.mesh.MeshData;
import com.noodlegamer76.noodleengine.client.glitf.mesh.PrimitiveData;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class MeshStorage {
    private static final Map<ResourceLocation, PrimitiveData> PRIMITIVES = new HashMap<>();
    private static final Map<ResourceLocation, MeshData> MESHES = new HashMap<>();

    public static MeshData getMesh(ResourceLocation location) {
        return MESHES.get(location);
    }

    public static void addMesh(ResourceLocation location, MeshData mesh) {
        MESHES.put(location, mesh);
    }

    public static boolean containsMesh(ResourceLocation location) {
        return MESHES.containsKey(location);
    }

    public static int getMeshCount() {
        return MESHES.size();
    }

    public static Map<ResourceLocation, MeshData> getMeshes() {
        return MESHES;
    }

    public static void clearMeshes() {
        MESHES.clear();
    }

    public static PrimitiveData getPrimitive(ResourceLocation location) {
        return PRIMITIVES.get(location);
    }

    public static void addPrimitive(ResourceLocation location, PrimitiveData primitive) {
        PRIMITIVES.put(location, primitive);
    }

    public static boolean containsPrimitive(ResourceLocation location) {
        return PRIMITIVES.containsKey(location);
    }

    public static int getPrimitiveCount() {
        return PRIMITIVES.size();
    }

    public static Map<ResourceLocation, PrimitiveData> getPrimitives() {
        return PRIMITIVES;
    }

    public static void clear() {
        PRIMITIVES.clear();
    }
}
