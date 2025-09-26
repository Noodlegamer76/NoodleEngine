package com.noodlegamer76.noodleengine.client.glitf.mesh;

import com.noodlegamer76.noodleengine.client.glitf.McGltf;
import com.noodlegamer76.noodleengine.client.glitf.material.McMaterial;
import com.noodlegamer76.noodleengine.client.glitf.material.Materials;
import com.noodlegamer76.noodleengine.client.glitf.rendering.GltfVbo;
import de.javagl.jgltf.model.MeshPrimitiveModel;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class PrimitiveData {
    private final List<Vertex> vertices;
    private final int[] indices;
    private final ResourceLocation location;
    private McMaterial material = Materials.DEFAULT;
    private final McGltf model;
    private final MeshPrimitiveModel primitiveModel;
    public MeshData meshData;
    private GltfVbo vbo = null;

    public PrimitiveData(List<Vertex> vertices, int[] indices, ResourceLocation location, McGltf model, MeshPrimitiveModel primitiveModel) {
        this.vertices = vertices;
        this.indices = indices;
        this.location = location;
        this.model = model;
        this.primitiveModel = primitiveModel;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public int[] getIndices() {
        return indices;
    }

    public void setMaterial(McMaterial material) {
        this.material = material;
    }

    public McMaterial getMaterial() {
        return material;
    }

    public ResourceLocation getLocation() {
        return location;
    }

    public McGltf getModel() {
        return model;
    }

    public MeshPrimitiveModel getPrimitiveModel() {
        return primitiveModel;
    }

    public void setVbo(GltfVbo vbo) {
        this.vbo = vbo;
    }

    public GltfVbo getVbo() {
        return vbo;
    }
}
