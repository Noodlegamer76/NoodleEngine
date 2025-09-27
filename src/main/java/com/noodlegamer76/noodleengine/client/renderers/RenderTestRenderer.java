package com.noodlegamer76.noodleengine.client.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.noodlegamer76.noodleengine.NoodleEngine;
import com.noodlegamer76.noodleengine.client.glitf.McGltf;
import com.noodlegamer76.noodleengine.client.glitf.animation.AnimationPlayer;
import com.noodlegamer76.noodleengine.client.glitf.animation.GltfAnimation;
import com.noodlegamer76.noodleengine.client.glitf.skin.SkinUbo;
import com.noodlegamer76.noodleengine.client.glitf.util.GltfLoader;
import com.noodlegamer76.noodleengine.tile.RenderTestTile;
import de.javagl.jgltf.model.NodeModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RenderTestRenderer implements BlockEntityRenderer<RenderTestTile> {
    McGltf model;
    McGltf master;
    AnimationPlayer animationPlayer;
    AnimationPlayer masterAnimationPlayer;

    public RenderTestRenderer(BlockEntityRendererProvider.Context ctx) {
        model = GltfLoader.loadModel(ResourceLocation.fromNamespaceAndPath(NoodleEngine.MODID, "goku.glb"));
        master = GltfLoader.loadModel(ResourceLocation.fromNamespaceAndPath(NoodleEngine.MODID, "master.glb"));

        if (model != null && !model.animations.isEmpty()) {
            GltfAnimation animation = model.animations.get(0);
            if (animation != null) {
                animationPlayer = new AnimationPlayer(animation);
                animationPlayer.setLooping(true);
            }
        }
        if (master != null && !master.animations.isEmpty()) {
            GltfAnimation animation = master.animations.get(0);
            if (animation != null) {
                masterAnimationPlayer = new AnimationPlayer(animation);
                masterAnimationPlayer.setLooping(true);
            }
        }
    }

    @Override
    public void render(RenderTestTile tile, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();

        if (model != null) {
            poseStack.pushPose();
            // global scale for both models
            poseStack.scale(0.075f / 100, 0.075f / 100, 0.075f / 100);
            //poseStack.scale(3, 3, 3);
            //poseStack.mulPose(Axis.XP.rotationDegrees(-90));

            renderSingleModel(model, animationPlayer, poseStack, packedLight, -1.5f);

            poseStack.popPose();
        }

        if (master != null) {
            poseStack.pushPose();
            poseStack.scale(0.075f / 5, 0.075f / 5, 0.075f / 5);

            renderSingleModel(master, masterAnimationPlayer, poseStack, packedLight, 100);

            poseStack.popPose();
        }

        RenderSystem.disableBlend();
    }

    /**
     * Helper to render one McGltf instance. Translates the poseStack by xOffset (in model-space)
     * before rendering so multiple models don't overlap.
     */
    private void renderSingleModel(McGltf gltfModel, AnimationPlayer player, PoseStack poseStack, int packedLight, float xOffset) {
        poseStack.pushPose();
        poseStack.translate(xOffset, 0.0f, 0.0f);

        int nodeCount = gltfModel.nodes.size();

        int[] nodeParents = new int[nodeCount];

        Arrays.fill(nodeParents, -1);

        List<NodeModel> nodes = gltfModel.nodes;

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
        if (player != null) {
            float deltaTime = Minecraft.getInstance().getDeltaFrameTime() / 20f;
            player.update(deltaTime);
            animationTransforms = player.sample();
        }

        if (!gltfModel.skins.isEmpty()) {
            SkinUbo ubo = gltfModel.skins.get(0);

            int skinIndex = gltfModel.skins.indexOf(ubo);
            for (int nodeIndex = 0; nodeIndex < nodeCount; nodeIndex++) {
                NodeModel node = gltfModel.nodes.get(nodeIndex);
                if (node.getMeshModels() != null && !node.getMeshModels().isEmpty() && node.getSkinModel() == ubo.getSkin()) {
                    ubo.uploadAnimated(gltfModel.bindLocalPose, animationTransforms, nodeParents, nodeIndex);

                    ubo.bindToShader(0);

                    gltfModel.renderMeshNode(poseStack, packedLight, ubo, nodeIndex);
                }
            }
        } else {
            gltfModel.render(poseStack, packedLight, null);
        }

        poseStack.popPose();
    }

    @Override
    public boolean shouldRender(RenderTestTile tile, Vec3 pos) {
        return true;
    }
}
