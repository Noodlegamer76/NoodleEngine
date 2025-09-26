package com.noodlegamer76.noodleengine.client.glitf.mesh;

import com.mojang.blaze3d.vertex.PoseStack;
import com.noodlegamer76.noodleengine.client.glitf.McGltf;
import com.noodlegamer76.noodleengine.client.glitf.rendering.GltfRenderer;
import com.noodlegamer76.noodleengine.client.glitf.skin.SkinUbo;
import de.javagl.jgltf.impl.v2.Mesh;
import de.javagl.jgltf.model.MeshModel;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class MeshData {
    private final List<PrimitiveData> primitives;
    private final ResourceLocation meshLocation;
    private final McGltf model;
    private final Mesh mesh;
    public final List<SkinUbo> availableSkins = new ArrayList<>();

    public MeshData(McGltf model, Mesh mesh, List<PrimitiveData> primitives, ResourceLocation meshLocation) {
        this.primitives = primitives;
        this.meshLocation = meshLocation;
        this.model = model;
        this.mesh = mesh;
    }

    public McGltf getModel() {
        return model;
    }

    public List<PrimitiveData> getPrimitives() {
        return primitives;
    }

    public ResourceLocation getMeshLocation() {
        return meshLocation;
    }

    public Mesh getMesh() {
        return mesh;
    }
}
