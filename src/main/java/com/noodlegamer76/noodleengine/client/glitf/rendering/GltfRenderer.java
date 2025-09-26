package com.noodlegamer76.noodleengine.client.glitf.rendering;

import com.mojang.blaze3d.vertex.*;
import com.noodlegamer76.noodleengine.client.glitf.McGltf;
import com.noodlegamer76.noodleengine.client.glitf.material.MaterialProperty;
import com.noodlegamer76.noodleengine.client.glitf.material.McMaterial;
import com.noodlegamer76.noodleengine.client.glitf.mesh.PrimitiveData;
import com.noodlegamer76.noodleengine.client.glitf.mesh.Vertex;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2f;

import java.util.List;
import java.util.Map;

import static com.noodlegamer76.noodleengine.client.glitf.mesh.ModVertexFormats.*;

public class GltfRenderer {

    public static void renderMaterials(McGltf model) {
        Map<McMaterial, GltfVbo> vboMap = model.vboMap;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        for (Map.Entry<McMaterial, GltfVbo> entry : vboMap.entrySet()) {
            GltfVbo vbo = entry.getValue();
            vbo.bind();

            List<PrimitiveData> primitives = model.materialToPrimitives.get(entry.getKey());

            buffer.begin(VertexFormat.Mode.TRIANGLES, GLB_PBR);

            for (PrimitiveData prim : primitives) {
                renderPrimitive(prim, buffer);
            }

            BufferBuilder.RenderedBuffer renderedBuffer = buffer.end();
            vbo.upload(renderedBuffer);
        }

        VertexBuffer.unbind();
    }

    public static void renderPrimitive(PrimitiveData prim, VertexConsumer buffer) {
        for (int i = 0; i < prim.getIndices().length; i += 3) {
            drawVertex(prim.getVertices(), prim.getIndices()[i], buffer, prim);
            drawVertex(prim.getVertices(), prim.getIndices()[i + 1], buffer, prim);
            drawVertex(prim.getVertices(), prim.getIndices()[i + 2], buffer, prim);
        }
    }

    private static void drawVertex(List<Vertex> vertices, int idx,
                                   VertexConsumer buffer,
                                   PrimitiveData prim) {
        Vertex v = vertices.get(idx);

        if (buffer instanceof BufferBuilder bb) {
            Vector2f albedoUv = getUv(v, prim, MaterialProperty.ALBEDO_MAP);
            Vector2f normalUv = getUv(v, prim, MaterialProperty.NORMAL_MAP);
            Vector2f metallicUv = getUv(v, prim, MaterialProperty.METALLIC_MAP);
            Vector2f roughnessUv = getUv(v, prim, MaterialProperty.ROUGHNESS_MAP);
            Vector2f aoUv = getUv(v, prim, MaterialProperty.AO_MAP);
            Vector2f emissiveUv = getUv(v, prim, MaterialProperty.EMISSIVE_MAP);

            // Position
            bb.vertex(v.x(), v.y(), v.z());

            // Color
            bb.color(255, 255, 255, 255);

            // UV0
            bb.uv(albedoUv.x, albedoUv.y);

            // Normal
            bb.normal(v.nx(), v.ny(), v.nz());

            bb.putFloat(0, normalUv.x);
            bb.putFloat(4, normalUv.y);
            bb.nextElement();

            bb.putFloat(0, metallicUv.x);
            bb.putFloat(4, metallicUv.y);
            bb.nextElement();

            bb.putFloat(0, roughnessUv.x);
            bb.putFloat(4, roughnessUv.y);
            bb.nextElement();

            bb.putFloat(0, aoUv.x);
            bb.putFloat(4, aoUv.y);
            bb.nextElement();

            bb.putFloat(0, emissiveUv.x);
            bb.putFloat(4, emissiveUv.y);
            bb.nextElement();

            float[] joints = v.jointIndices();
            float[] weights = v.jointWeights();

            float j0 = joints.length > 0 ? joints[0] : 0;
            float j1 = joints.length > 1 ? joints[1] : 0;
            float j2 = joints.length > 2 ? joints[2] : 0;
            float j3 = joints.length > 3 ? joints[3] : 0;

            float w0 = weights.length > 0 ? weights[0] : 1f;
            float w1 = weights.length > 1 ? weights[1] : 0;
            float w2 = weights.length > 2 ? weights[2] : 0;
            float w3 = weights.length > 3 ? weights[3] : 0;

            float sum = w0 + w1 + w2 + w3;
            if (sum > 0f) {
                w0 /= sum;
                w1 /= sum;
                w2 /= sum;
                w3 /= sum;
            }

            bb.putFloat(0, j0);
            bb.putFloat(4, j1);
            bb.putFloat(8, j2);
            bb.putFloat(12, j3);
            bb.nextElement();

            bb.putFloat(0, w0);
            bb.putFloat(4, w1);
            bb.putFloat(8, w2);
            bb.putFloat(12, w3);
            bb.nextElement();

            bb.endVertex();
        }
    }

    private static Vector2f getUv(Vertex v, PrimitiveData prim, MaterialProperty<ResourceLocation> property) {
        McMaterial mat = prim.getMaterial();
        if (!mat.hasTexCoord(property)) return new Vector2f(0, 0);
        return coordFromIndex(v, mat.getTexCoord(property));
    }

    private static int[] getUvInt(Vertex v, PrimitiveData prim, MaterialProperty<ResourceLocation> property) {
        Vector2f uv = getUv(v, prim, property);
        return new int[]{Float.floatToRawIntBits(uv.x), Float.floatToRawIntBits(uv.y)};
    }

    private static Vector2f coordFromIndex(Vertex v, int index) {
        Vector2f uv = v.UVs().get(index);
        if (uv == null) uv = v.UVs().get(0);
        if (uv == null) uv = new Vector2f(0, 0);
        return uv;
    }
}