package com.noodlegamer76.noodleengine.client.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.noodlegamer76.noodleengine.NoodleEngine;
import com.noodlegamer76.noodleengine.client.glitf.McGltf;
import com.noodlegamer76.noodleengine.client.glitf.access.ModelStorage;
import com.noodlegamer76.noodleengine.client.glitf.animation.AnimationPlayer;
import com.noodlegamer76.noodleengine.client.glitf.animation.GltfAnimation;
import com.noodlegamer76.noodleengine.client.glitf.rendering.RenderableModel;
import com.noodlegamer76.noodleengine.client.glitf.skin.SkinUbo;
import com.noodlegamer76.noodleengine.client.glitf.util.GltfLoader;
import com.noodlegamer76.noodleengine.engine.components.MeshRenderer;
import com.noodlegamer76.noodleengine.tile.RenderTestTile;
import de.javagl.jgltf.model.NodeModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RenderTestRenderer implements BlockEntityRenderer<RenderTestTile> {
    RenderableModel model;
    RenderableModel master;

    public RenderTestRenderer(BlockEntityRendererProvider.Context ctx) {
        model = new RenderableModel(ModelStorage.getModel(ResourceLocation.fromNamespaceAndPath(NoodleEngine.MODID, "gltf/goku.glb")));
        master = new RenderableModel(ModelStorage.getModel(ResourceLocation.fromNamespaceAndPath(NoodleEngine.MODID, "gltf/master.glb")));

        model.setActiveAnimation(0);
        AnimationPlayer animationPlayer = model.getActiveAnimation();
        if (animationPlayer != null) {
            animationPlayer.setLooping(true);
        }

        master.setActiveAnimation(0);
        AnimationPlayer masterAnimationPlayer = master.getActiveAnimation();
        if (masterAnimationPlayer != null) {
            masterAnimationPlayer.setLooping(true);
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
            //poseStack.scale(1000, 1000, 1000);
            //poseStack.scale(3, 3, 3);
            //poseStack.mulPose(Axis.XP.rotationDegrees(-90));

            model.renderSingleModel(poseStack, partialTick, packedLight);

            poseStack.popPose();
        }

        if (master != null) {
            poseStack.pushPose();
            poseStack.scale(0.075f / 5, 0.075f / 5, 0.075f / 5);

            master.renderSingleModel(poseStack, partialTick, packedLight);

            poseStack.popPose();
        }

        RenderSystem.disableBlend();
    }

    @Override
    public boolean shouldRender(RenderTestTile tile, Vec3 pos) {
        return true;
    }
}
