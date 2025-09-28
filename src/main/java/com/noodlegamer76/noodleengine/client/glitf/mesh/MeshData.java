package com.noodlegamer76.noodleengine.client.glitf.mesh;

import com.mojang.blaze3d.vertex.PoseStack;
import com.noodlegamer76.noodleengine.client.glitf.McGltf;
import com.noodlegamer76.noodleengine.client.glitf.rendering.GltfRenderer;
import com.noodlegamer76.noodleengine.client.glitf.skin.SkinUbo;
import de.javagl.jgltf.impl.v2.Mesh;
import de.javagl.jgltf.model.MeshModel;
import de.javagl.jgltf.model.NodeModel;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class MeshData {
    private final List<PrimitiveData> primitives;
    private final ResourceLocation meshLocation;
    private final McGltf model;
    private final MeshModel meshModel;
    private MeshNodeHierarchy hierarchy;
    public final List<SkinUbo> availableSkins = new ArrayList<>();

    public MeshData(McGltf model, MeshModel meshModel, List<PrimitiveData> primitives, ResourceLocation meshLocation) {
        this.primitives = primitives;
        this.meshLocation = meshLocation;
        this.model = model;
        this.meshModel = meshModel;
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

    public MeshModel getMeshModel() {
        return meshModel;
    }

    public MeshNodeHierarchy getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(MeshNodeHierarchy hierarchy) {
        this.hierarchy = hierarchy;
    }
}
