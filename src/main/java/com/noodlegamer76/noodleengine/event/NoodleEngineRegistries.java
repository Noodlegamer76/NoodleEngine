package com.noodlegamer76.noodleengine.event;

import com.noodlegamer76.noodleengine.NoodleEngine;
import com.noodlegamer76.noodleengine.engine.components.ComponentType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(modid = NoodleEngine.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NoodleEngineRegistries {
    public static final ResourceKey<Registry<ComponentType>> COMPONENT_TYPE = createRegistryKey("component_type");

    private static <T> ResourceKey<Registry<T>> createRegistryKey(String name) {
        return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(NoodleEngine.MODID, name));
    }

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.create(new RegistryBuilder<ComponentType>()
                .setName(ResourceLocation.fromNamespaceAndPath(NoodleEngine.MODID, "component_type")));
    }
}
