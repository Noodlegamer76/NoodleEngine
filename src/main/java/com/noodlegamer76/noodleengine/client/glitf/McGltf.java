package com.noodlegamer76.noodleengine.client.glitf;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.noodlegamer76.noodleengine.client.glitf.animation.GltfAnimation;
import com.noodlegamer76.noodleengine.client.glitf.material.McMaterial;
import com.noodlegamer76.noodleengine.client.glitf.mesh.MeshData;
import com.noodlegamer76.noodleengine.client.glitf.mesh.MeshNodeHierarchy;
import com.noodlegamer76.noodleengine.client.glitf.mesh.PrimitiveData;
import com.noodlegamer76.noodleengine.client.glitf.rendering.GltfVbo;
import com.noodlegamer76.noodleengine.client.glitf.skin.SkinUbo;
import de.javagl.jgltf.impl.v2.GlTF;
import de.javagl.jgltf.impl.v2.Node;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.MeshModel;
import de.javagl.jgltf.model.NodeModel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class McGltf {
    public final GltfModel model;
    public final ResourceLocation location;
    public final List<MeshData> meshes = new ArrayList<>();
    public final Map<McMaterial, GltfVbo> vboMap = new HashMap<>();
    public final Map<McMaterial, List<PrimitiveData>> materialToPrimitives = new HashMap<>();
    public final Map<MeshData, List<SkinUbo>> skinsFromMesh = new HashMap<>();
    public final List<SkinUbo> skins = new ArrayList<>();
    public final List<GltfAnimation> animations = new ArrayList<>();
    public final List<Matrix4f> bindGlobalPose = new ArrayList<>();
    public final List<Matrix4f> bindLocalPose = new ArrayList<>();
    public final List<NodeModel> nodes = new ArrayList<>();
    public final Map<MeshModel, MeshData> meshModelToMeshData = new HashMap<>();
    public final Map<MeshData, NodeModel> meshToNode = new HashMap<>();
    public final Map<MeshData, MeshNodeHierarchy> meshNodeHierarchy = new HashMap<>();

    public McGltf(GltfModel model, ResourceLocation location) {
        this.model = model;
        this.location = location;
    }

    public void render(PoseStack poseStack, int packedLight, @Nullable SkinUbo skinUbo) {
        for (GltfVbo vbo : vboMap.values()) {
            McMaterial material = vbo.getMaterial();
            material.bind();
            vbo.bind();

            vbo.drawAllPrimitives(RenderSystem.getProjectionMatrix(), poseStack.last().pose(), packedLight, skinUbo);
        }
    }

    public void renderMeshNode(PoseStack poseStack, int packedLight, @Nullable SkinUbo skinUbo, int nodeIndex) {
        NodeModel nodeModel = model.getNodeModels().get(nodeIndex);
        if (nodeModel.getMeshModels().isEmpty()) return;
        MeshModel meshModel = nodeModel.getMeshModels().get(0);

        MeshData mesh = meshModelToMeshData.get(meshModel);
        for (PrimitiveData primitive : mesh.getPrimitives()) {
            McMaterial material = primitive.getMaterial();
            if (material == null) continue;

            GltfVbo vbo = vboMap.get(material);
            if (vbo == null) continue;

            material.bind();
            vbo.bind();

            vbo.drawPrimitives(RenderSystem.getProjectionMatrix(), poseStack.last().pose(), packedLight, skinUbo, primitive);
        }
    }
}
