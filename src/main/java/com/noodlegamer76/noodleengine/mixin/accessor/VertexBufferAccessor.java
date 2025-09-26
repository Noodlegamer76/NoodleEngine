package com.noodlegamer76.noodleengine.mixin.accessor;

import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(VertexBuffer.class)
public interface VertexBufferAccessor {

    @Accessor(value = "mode")
    VertexFormat.Mode getVBOMode();

    @Invoker(value = "getIndexType")
    VertexFormat.IndexType getVBOIndexType();
}
