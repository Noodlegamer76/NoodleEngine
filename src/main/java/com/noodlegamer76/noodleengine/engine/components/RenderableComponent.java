package com.noodlegamer76.noodleengine.engine.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.client.event.RenderLevelStageEvent;

public interface RenderableComponent {
    void render(RenderLevelStageEvent event, PoseStack poseStack, float partialTicks);
}
