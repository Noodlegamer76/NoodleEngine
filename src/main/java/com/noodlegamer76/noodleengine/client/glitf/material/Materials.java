package com.noodlegamer76.noodleengine.client.glitf.material;

import com.noodlegamer76.noodleengine.event.ShaderRegistry;
import net.minecraft.resources.ResourceLocation;

public class Materials {
    public static final McMaterial DEFAULT = new MaterialBuilder(ShaderRegistry.pbr, "temp")
            .texture(ResourceLocation.withDefaultNamespace("textures/block/dirt.png"))
            .build();
}
