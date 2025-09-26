package com.noodlegamer76.noodleengine.event;

import com.noodlegamer76.noodleengine.NoodleEngine;
import com.noodlegamer76.noodleengine.client.ShaderReference;
import com.noodlegamer76.noodleengine.client.glitf.mesh.ModVertexFormats;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = NoodleEngine.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ShaderRegistry {
    public static ShaderReference pbr = new ShaderReference();

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {

        event.registerShader(new ShaderInstance(event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath(NoodleEngine.MODID, "pbr"),
                        ModVertexFormats.GLB_PBR),
                (e) -> pbr.shader = e);

    }
}
