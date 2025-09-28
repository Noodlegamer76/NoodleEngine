package com.noodlegamer76.noodleengine.event;

import com.noodlegamer76.noodleengine.NoodleEngine;
import com.noodlegamer76.noodleengine.client.glitf.access.ModelStorage;
import com.noodlegamer76.noodleengine.client.glitf.util.GltfLoader;
import com.noodlegamer76.noodleengine.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = NoodleEngine.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SetupEvents {

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(PacketHandler::register);

        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        String modelPath = "gltf";
        GltfLoader.loadAllGlbModels(resourceManager, modelPath);
    }
}
