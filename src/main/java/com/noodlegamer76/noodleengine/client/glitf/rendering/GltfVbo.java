package com.noodlegamer76.noodleengine.client.glitf.rendering;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.noodlegamer76.noodleengine.client.glitf.material.McMaterial;
import com.noodlegamer76.noodleengine.client.glitf.mesh.PrimitiveData;
import com.noodlegamer76.noodleengine.client.glitf.skin.SkinUbo;
import com.noodlegamer76.noodleengine.mixin.accessor.LightTextureAccessor;
import com.noodlegamer76.noodleengine.mixin.accessor.VertexBufferAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class GltfVbo extends VertexBuffer {
    private final McMaterial material;
    private int primitiveIndexCount = 0;
    private final Map<PrimitiveData, Integer> primitiveIndexes = new HashMap<>();

    public GltfVbo(Usage pUsage, McMaterial material) {
        super(pUsage);
        this.material = material;
    }

    public void addPrimitive(PrimitiveData primitive) {
        int index = primitiveIndexCount;
        primitiveIndexCount += primitive.getIndices().length;
        primitiveIndexes.put(primitive, index);
    }

    public void drawAllPrimitives(Matrix4f projectionMatrix, Matrix4f modelViewMatrix, int packedLight, @Nullable SkinUbo skinUbo) {
        drawPrimitives(projectionMatrix, modelViewMatrix, packedLight, skinUbo, primitiveIndexes.keySet().toArray(new PrimitiveData[0]));
    }

    public void drawPrimitives(Matrix4f projectionMatrix, Matrix4f modelViewMatrix, int packedLight, @Nullable SkinUbo skinUbo, PrimitiveData... primitives) {
        if (primitives.length == 0) return;

        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                drawPrimitives(projectionMatrix, modelViewMatrix, packedLight, skinUbo, primitives);
            });
        }

        PrimitiveData[] sorted = primitives.clone();
        java.util.Arrays.sort(sorted, (a, b) -> {
            int aStart = primitiveIndexes.getOrDefault(a, Integer.MAX_VALUE);
            int bStart = primitiveIndexes.getOrDefault(b, Integer.MAX_VALUE);
            return Integer.compare(aStart, bStart);
        });

        int batchStart = -1;
        int batchCount = 0;
        int lastEnd = -1;

        for (PrimitiveData primitive : sorted) {
            Integer indexStart = primitiveIndexes.get(primitive);
            if (indexStart == null) continue;

            int primitiveCount = primitive.getIndices().length;
            int primitiveEnd = indexStart + primitiveCount;

            if (batchStart == -1) {
                batchStart = indexStart;
                batchCount = primitiveCount;
                lastEnd = primitiveEnd;
            } else if (indexStart == lastEnd) {
                batchCount += primitiveCount;
                lastEnd = primitiveEnd;
            } else {
                drawRange(batchStart, batchCount, modelViewMatrix, projectionMatrix, packedLight, skinUbo);
                batchStart = indexStart;
                batchCount = primitiveCount;
                lastEnd = primitiveEnd;
            }
        }

        if (batchStart != -1 && batchCount > 0) {
            drawRange(batchStart, batchCount, modelViewMatrix, projectionMatrix, packedLight, skinUbo);
        }
    }

    public void drawRange(int startIndex, int count, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, int packedLight, @Nullable SkinUbo skinUbo) {
        ResourceLocation lightTexture = ((LightTextureAccessor) Minecraft.getInstance().gameRenderer.lightTexture()).getLightmapId();
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        int lightTextureId = textureManager.getTexture(lightTexture).getId();
        material.getShaderRef().shader.setSampler("lightTex", lightTextureId);

        VertexFormat.IndexType indexType = ((VertexBufferAccessor) this).getVBOIndexType();
        long byteOffset = (long) startIndex * indexType.bytes;
        RenderSystem.assertOnRenderThread();
        ShaderInstance shader = getMaterial().getShaderRef().shader;

        if (shader.MODEL_VIEW_MATRIX != null) {
            shader.MODEL_VIEW_MATRIX.set(modelViewMatrix);
        }

        if (shader.PROJECTION_MATRIX != null) {
            shader.PROJECTION_MATRIX.set(projectionMatrix);
        }

        if (shader.INVERSE_VIEW_ROTATION_MATRIX != null) {
            shader.INVERSE_VIEW_ROTATION_MATRIX.set(RenderSystem.getInverseViewRotationMatrix());
        }

        if (shader.COLOR_MODULATOR != null) {
            shader.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
        }

        if (shader.GLINT_ALPHA != null) {
            shader.GLINT_ALPHA.set(RenderSystem.getShaderGlintAlpha());
        }

        if (shader.FOG_START != null) {
            shader.FOG_START.set(RenderSystem.getShaderFogStart());
        }

        if (shader.FOG_END != null) {
            shader.FOG_END.set(RenderSystem.getShaderFogEnd());
        }

        if (shader.FOG_COLOR != null) {
            shader.FOG_COLOR.set(RenderSystem.getShaderFogColor());
        }

        if (shader.FOG_SHAPE != null) {
            shader.FOG_SHAPE.set(RenderSystem.getShaderFogShape().getIndex());
        }

        if (shader.TEXTURE_MATRIX != null) {
            shader.TEXTURE_MATRIX.set(RenderSystem.getTextureMatrix());
        }

        if (shader.GAME_TIME != null) {
            shader.GAME_TIME.set(RenderSystem.getShaderGameTime());
        }

        if (shader.SCREEN_SIZE != null) {
            Window window = Minecraft.getInstance().getWindow();
            shader.SCREEN_SIZE.set((float)window.getWidth(), (float)window.getHeight());
        }

        setLightUv(packedLight);

        RenderSystem.setupShaderLights(shader);
        shader.apply();

        GlStateManager._drawElements(((VertexBufferAccessor) this).getVBOMode().asGLMode, count, indexType.asGLType, byteOffset);
        shader.clear();
    }

    private void setLightUv(int packedLight) {
        Uniform lightUv = material.getShaderRef().shader.getUniform("lightUv");
        if (lightUv != null) {
            int blockLight = packedLight & 0xFFFF;
            int skyLight   = (packedLight >> 16) & 0xFFFF;
            lightUv.set(blockLight, skyLight);
        }

        Uniform packedLightUniform = material.getShaderRef().shader.getUniform("packedLight");
        if (packedLightUniform != null) {
            packedLightUniform.set(packedLight);
        }
    }


    public McMaterial getMaterial() {
        return material;
    }

    public Map<PrimitiveData, Integer> getPrimitiveIndexes() {
        return primitiveIndexes;
    }
}
