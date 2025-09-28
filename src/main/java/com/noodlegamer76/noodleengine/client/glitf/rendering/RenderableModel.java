package com.noodlegamer76.noodleengine.client.glitf.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.noodlegamer76.noodleengine.client.glitf.McGltf;
import com.noodlegamer76.noodleengine.client.glitf.access.ModelStorage;
import com.noodlegamer76.noodleengine.client.glitf.animation.AnimationPlayer;
import com.noodlegamer76.noodleengine.client.glitf.animation.GltfAnimation;
import com.noodlegamer76.noodleengine.client.glitf.skin.SkinUbo;
import de.javagl.jgltf.model.NodeModel;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RenderableModel {
    public final McGltf model;
    public List<AnimationPlayer> animationPlayers = new ArrayList<>();
    public int activeAnimation = -1;

    public RenderableModel(McGltf model) {
        this.model = model;

        for (GltfAnimation animation : model.animations) {
            animationPlayers.add(new AnimationPlayer(animation));
        }
    }

    public RenderableModel(ResourceLocation modelLocal) {
        this(ModelStorage.getModel(modelLocal));
    }

    /**
     * Helper to render one McGltf instance. Translates the poseStack by xOffset (in model-space)
     * before rendering so multiple models don't overlap.
     */
    public void renderSingleModel(PoseStack poseStack, float partialTick, int packedLight) {
        int nodeCount = model.nodes.size();

        int[] nodeParents = new int[nodeCount];

        Arrays.fill(nodeParents, -1);

        List<NodeModel> nodes = model.nodes;

        for (int i = 0; i < nodeCount; i++) {
            NodeModel node = nodes.get(i);
            List<NodeModel> children = node.getChildren();
            if (children != null) {
                for (NodeModel child : children) {
                    int childIndex = nodes.indexOf(child);
                    if (childIndex >= 0) {
                        nodeParents[childIndex] = i;
                    }
                }
            }
        }

        Map<Integer, AnimationPlayer.Transform> animationTransforms = null;
        if (getActiveAnimation() != null) {
            float deltaTime = Minecraft.getInstance().getDeltaFrameTime() / 20f;
            updateAnimation(deltaTime);
            animationTransforms = getActiveAnimation().sample();
        }

        if (!model.skins.isEmpty()) {
            SkinUbo ubo = model.skins.get(0);

            for (int nodeIndex = 0; nodeIndex < nodeCount; nodeIndex++) {
                NodeModel node = model.nodes.get(nodeIndex);
                if (node.getMeshModels() != null && !node.getMeshModels().isEmpty() && node.getSkinModel() == ubo.getSkin()) {
                    ubo.uploadAnimated(model.bindLocalPose, animationTransforms, nodeParents, nodeIndex);

                    ubo.bindToShader(0);

                    model.renderMeshNode(poseStack, packedLight, ubo, nodeIndex);
                }
            }
        } else {
            model.render(poseStack, packedLight, null);
        }
    }

    public void setActiveAnimation(int index) {
        activeAnimation = index;
    }

    public void disableAnimations() {
        activeAnimation = -1;
    }

    public AnimationPlayer getActiveAnimation() {
        if (activeAnimation < 0) {
            return null;
        }
        return animationPlayers.get(activeAnimation);
    }

    public List<AnimationPlayer> getAnimationPlayers() {
        return animationPlayers;
    }

    public void updateAnimation(float partialTicks) {
        if (activeAnimation < 0 || activeAnimation >= animationPlayers.size()) {
            return;
        }

        AnimationPlayer animation = animationPlayers.get(activeAnimation);

        animation.update(partialTicks);
    }
}
