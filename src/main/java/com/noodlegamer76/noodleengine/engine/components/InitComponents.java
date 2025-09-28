package com.noodlegamer76.noodleengine.engine.components;

import com.noodlegamer76.noodleengine.NoodleEngine;
import com.noodlegamer76.noodleengine.event.NoodleEngineRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

public class InitComponents {
    public static final DeferredRegister<ComponentType> COMPONENT_TYPES = DeferredRegister.create(NoodleEngineRegistries.COMPONENT_TYPE, NoodleEngine.MODID);
    public static Map<ResourceLocation, ComponentType> componentTypes;

    public static final RegistryObject<ComponentType> MESH_RENDERER =
            COMPONENT_TYPES.register("mesh_renderer", () -> new ComponentType(MeshRenderer::new));

    public static Map<ResourceLocation, ComponentType> getComponentTypes() {
        if (componentTypes == null) {
            componentTypes = new HashMap<>();
            COMPONENT_TYPES.getEntries().forEach(entry -> componentTypes.put(entry.getId(), entry.get()));
        }

        return componentTypes;
    }
}
