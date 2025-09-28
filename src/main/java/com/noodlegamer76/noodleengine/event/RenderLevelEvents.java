package com.noodlegamer76.noodleengine.event;

import com.jme3.math.Quaternion;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.noodlegamer76.noodleengine.NoodleEngine;
import com.noodlegamer76.noodleengine.engine.GameObject;
import com.noodlegamer76.noodleengine.engine.GameObjects;
import com.noodlegamer76.noodleengine.engine.components.Component;
import com.noodlegamer76.noodleengine.engine.components.RenderableComponent;
import com.noodlegamer76.noodleengine.engine.components.TickableComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber(modid = NoodleEngine.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RenderLevelEvents {

    @SubscribeEvent
    public static void renderLevel(RenderLevelStageEvent event) {
        for (GameObject object : GameObjects.getGameObjects().values()) {
            Vector3f position = object.getPosition().getValue();
            Quaternionf rotation = object.getRotation().getValue();
            Vector3f scale = object.getScale().getValue();

            PoseStack poseStack = event.getPoseStack();
            Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

            poseStack.pushPose();
            poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            poseStack.translate(position.x, position.y, position.z);
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
            poseStack.scale(scale.x, scale.y, scale.z);

            for (List<Component> components : object.getComponents().values()) {
                for (Component component : components) {
                    if (component instanceof RenderableComponent renderableComponent) {
                        renderableComponent.render(event, event.getPoseStack(), event.getPartialTick());
                    }
                }
            }

            poseStack.popPose();
        }
    }
}
